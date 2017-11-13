package xsw.nuc.edu.smart_house;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import xsw.nuc.edu.smart_house.httpUtils.HttpConn;

/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 9:47.
 */

public class ControlFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    private Switch mSwitch;
    private Switch mDs1;
    private Switch mDs2;
    private View view;
    private int lights=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.controlframgment, container, false);
        initView();
        return view;
    }

    private void initView() {
        mSwitch = (Switch) view.findViewById(R.id.auto_control);
        mDs1 = (Switch) view.findViewById(R.id.living_room);
        mDs2 = (Switch) view.findViewById(R.id.bed_room);
        mSwitch.setOnCheckedChangeListener(this);
        mDs1.setOnCheckedChangeListener(this);
        mDs2.setOnCheckedChangeListener(this);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.bed_room:
                    if (lights==0){
                        new ControlWsnAsyn().execute("1");
                        lights=1;
                        break;
                    }
                    if (lights==1){
                        new ControlWsnAsyn().execute("3");
                        lights=2;
                        break;
                    }
                case R.id.living_room:
                    if (lights==0){
                        new ControlWsnAsyn().execute("2");
                        lights=1;
                        break;
                    }
                    if (lights==1){
                        new ControlWsnAsyn().execute("3");
                        lights=2;
                        break;
                    }
                    break;
                case R.id.auto_control:
                    mDs1.setClickable(false);
                    mDs2.setClickable(false);
                    mDs1.setVisibility(View.INVISIBLE);
                    mDs2.setVisibility(View.INVISIBLE);
                    new AutoControlWsnAsyn().execute("1","0","0");
                    break;
            }
        }else {
            switch (buttonView.getId()) {
                case R.id.bed_room:
                    if (lights==2){
                        new ControlWsnAsyn().execute("2");
                        lights=1;
                        break;
                    }
                    if (lights==1){
                        new ControlWsnAsyn().execute("0");
                        lights=0;
                        break;
                    }
                    break;
                case R.id.living_room:
                    if (lights==2){
                        new ControlWsnAsyn().execute("1");
                        lights=1;
                        break;
                    }
                    if (lights==1){
                        new ControlWsnAsyn().execute("0");
                        lights=0;
                        break;
                    }
                    break;
                case R.id.auto_control:
                    mDs1.setClickable(true);
                    mDs2.setClickable(true);
                    mDs1.setVisibility(View.VISIBLE);
                    mDs2.setVisibility(View.VISIBLE);
                    new AutoControlWsnAsyn().execute("0","0","0");
                    break;
            }
        }

    }

    class ControlWsnAsyn extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpConn.controlExecute(params[0]);
            return null;
        }
    }
    class AutoControlWsnAsyn extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpConn.autoControlExecute(params[0],params[1],params[2]);
            return null;
        }
    }
}
