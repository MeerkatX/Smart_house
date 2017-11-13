package xsw.nuc.edu.smart_house;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lecho.lib.hellocharts.view.LineChartView;
import xsw.nuc.edu.smart_house.httpUtils.HttpConn;
import xsw.nuc.edu.smart_house.threadControl.ControlSignal;
import xsw.nuc.edu.smart_house.threadControl.OffHomeSignal;
import xsw.nuc.edu.smart_house.threadControl.OffHomeThread;
import xsw.nuc.edu.smart_house.threadControl.PrintDarw;
import xsw.nuc.edu.smart_house.wsnBean.Wsn;

import static java.lang.Thread.sleep;

/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 9:47.
 */

public class InfoFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "InfoFragment";
    private View view;
    private TextView temp, humm, irpers, illum, refresh;
    private Button mbtn_offhome, mbtn_athome, mbtn_sleep;
    private String password;
    private SharedPreferences sharedPreferences;
    private List<Wsn> wsns;
    private LineChartView lineChartView;
    private ControlSignal controlSignal;
    private PrintDarw printDarw;
    private OffHomeThread offHomeThread;
    private CustomPopupWindow popupWindow;
    private OffHomeSignal offHomeSignal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.infofragment, container, false);
        //初始化
        temp = (TextView) view.findViewById(R.id.tv_temp);
        humm = (TextView) view.findViewById(R.id.tv_humm);
        irpers = (TextView) view.findViewById(R.id.tv_irpers);
        illum = (TextView) view.findViewById(R.id.tv_illum);
        refresh = (TextView) view.findViewById(R.id.tv_hello);
        lineChartView = (LineChartView) view.findViewById(R.id.lineChart);

        mbtn_athome = (Button) view.findViewById(R.id.at_home);
        mbtn_sleep = (Button) view.findViewById(R.id.sleep_mode);
        mbtn_offhome = (Button) view.findViewById(R.id.off_home);

        sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        controlSignal = new ControlSignal();
        offHomeSignal = new OffHomeSignal();

        offHomeThread = new OffHomeThread(getActivity(), offHomeSignal);
        printDarw = new PrintDarw(getActivity(), lineChartView, controlSignal);
        printDarw.start();
        offHomeThread.start();
        popupWindow = new CustomPopupWindow.Builder(getContext())
                .setContentView(R.layout.layout_popup)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocus(true)
                .setOutsideCancel(false)
                .setElevation(3.0F)
                .build();


        mbtn_offhome.setOnClickListener(this);
        mbtn_athome.setOnClickListener(this);
        mbtn_sleep.setOnClickListener(this);
        refresh.setOnClickListener(this);

        popupWindow.setOnClickListener(R.id.btn_submit, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                password = ((EditText) popupWindow.findView(R.id.password_edit_text)).getText().toString();
                popupWindow.dismiss();
            }
        });
        new Thread(new Runnable() {
            int i = 10000;

            @Override
            public void run() {
                while (i > 0) {
                    new GetWsnData().execute();
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }

            }
        }).start();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.off_home:
                if (sharedPreferences.getString("password", "").toString() == "") {
                    popupWindow.showAtLocation(R.layout.activity_main, Gravity.BOTTOM, 0, 0);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("password", password);
                            editor.commit();
                            Toast.makeText(getContext(), "密码设置完毕，请妥善保管", Toast.LENGTH_LONG).show();
                            offHomeSignal.offSignal = true;
                            Toast.makeText(getContext(), "安防已开启", Toast.LENGTH_LONG).show();
                            new AutoControlWsnAsyn().execute("1", "0", "1");
                        }
                    });
                } else {
                    offHomeSignal.offSignal = true;
                    Toast.makeText(getContext(), "安防已开启", Toast.LENGTH_LONG).show();
                    new AutoControlWsnAsyn().execute("1", "0", "1");
                }
                break;
            case R.id.at_home:
                popupWindow.showAtLocation(R.layout.activity_main, Gravity.BOTTOM, 0, 0);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (sharedPreferences.getString("password", "000").toString().equals(password)) {
                            offHomeSignal.offSignal = false;
                            Toast.makeText(getContext(), "密码正确\n已解除安防", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "密码错误！！！", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                new AutoControlWsnAsyn().execute("0", "0", "0");
                break;
            case R.id.sleep_mode:
                new ControlWsnAsyn().execute("0");
                break;
            case R.id.tv_hello:
                //获取传感器数据
                break;
            default:
                break;
        }
    }


    class GetWsnData extends AsyncTask<String, String, List<Wsn>> {

        @Override
        protected List<Wsn> doInBackground(String... params) {
            wsns = HttpConn.getWsnData();
            return wsns;
        }

        @Override
        protected void onPostExecute(List<Wsn> wsns) {
            if (wsns != null) {
                for (int i = 0; i < wsns.size(); i++) {
                    if (wsns.get(i).getFuncCode() == 1) {
                        temp.setText(String.valueOf(wsns.get(i).getData()));
                    }
                    if (wsns.get(i).getFuncCode() == 2) {
                        humm.setText(String.valueOf(wsns.get(i).getData()));
                    }
                    if (wsns.get(i).getFuncCode() == 3) {
                        illum.setText(String.valueOf(wsns.get(i).getData()));
                    }
                    if (wsns.get(i).getFuncCode() == 9) {
                        irpers.setText(String.valueOf(wsns.get(i).getData()));
                    }
                }
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
            HttpConn.autoControlExecute(params[0], params[1], params[2]);
            return null;
        }
    }

}
