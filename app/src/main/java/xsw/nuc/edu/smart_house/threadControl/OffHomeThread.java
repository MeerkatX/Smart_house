package xsw.nuc.edu.smart_house.threadControl;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import xsw.nuc.edu.smart_house.httpUtils.HttpConn;
import xsw.nuc.edu.smart_house.wsnBean.Wsn;

/**
 * Smart_house
 * Created by 11749 on 2017/9/13,上午 9:22.
 */

public class OffHomeThread extends Thread{
    private int i;
    private List<Wsn> wsns;
    private OffHomeSignal offHomeSignal;
    private final String TAG="OffHomeThread";
    private Context context;

    public OffHomeThread(Context context,OffHomeSignal offHomeSignal){
        this.context=context;
        i=100000;
        this.offHomeSignal=offHomeSignal;
    }

    @Override
    public void run() {
        while (i>0){
            if (offHomeSignal.offSignal){

                new GetWsnData().execute();
                if (wsns!=null)
                for(int j=0;j<wsns.size();j++){
                    if (wsns.get(j).getFuncCode()==9&&wsns.get(j).getData()==1){
                        Looper.prepare();
                        Toast.makeText(this.context,"家中有人，请注意",Toast.LENGTH_LONG).show();
                        Looper.loop();
                        Log.d(TAG, "run: ");
                    }
                }

            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
        }
    }

    class GetWsnData extends AsyncTask<String, String, List<Wsn>> {

        @Override
        protected List<Wsn> doInBackground(String... params) {
            wsns = HttpConn.getWsnData();
            return wsns;
        }
    }
}
