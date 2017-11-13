package xsw.nuc.edu.smart_house.httpUtils;

import android.util.Log;

import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import xsw.nuc.edu.smart_house.wsnBean.Wsn;


/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 6:01.
 */

public class Analysis {
    private static String TAG="Analysis";
    public static List<Wsn> parseWsn(InputStream in) throws Exception {
        List<Wsn> wsns = new ArrayList<Wsn>();
        Wsn wsn = null;
        String str = read(in);
        JSONObject object = JSONObject.fromObject(str);
        if (object != null) {
            for (Object data : object.keySet()) {
                wsn = new Wsn(Integer.valueOf(String.valueOf(data)),
                        object.getInt(String.valueOf(data)));
                Log.d(TAG, "parseWsn: "+wsn.getData()+"++"+wsn.getFuncCode());
                wsns.add(wsn);
            }
        }
        return wsns;
    }

    public static String read(InputStream in) throws IOException {
        byte[] data;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            bout.write(buf, 0, len);
        }
        data = bout.toByteArray();
        return new String(data, "UTF-8");
    }
}
