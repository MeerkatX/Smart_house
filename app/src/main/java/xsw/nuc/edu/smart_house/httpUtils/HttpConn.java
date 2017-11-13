package xsw.nuc.edu.smart_house.httpUtils;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import xsw.nuc.edu.smart_house.wsnBean.Wsn;

/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 5:03.
 */

public class HttpConn {

    private static String URL_PATH = "http://192.168.137.1:8080/WsnServer/";
    private static String SHOW_SERVLET = "showServlet";
    private static String CONTROL_SERVLET = "controlServlet";
    private static String AUTO_CONTROL_SERVLET="autoControlServlet";

    //建立http链接
    private static HttpURLConnection setConnection(String servlet) {
        try {
            URL url = new URL(URL_PATH + servlet);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);//设置允许输出
            conn.setDoInput(true);
            return conn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //接受数据
    public static List<Wsn> getWsnData() {
        JSONStringer stringer = new JSONStringer();
        HttpURLConnection conn = setConnection(SHOW_SERVLET);
        try {
            OutputStream outputStream = conn.getOutputStream();
            outputStream.close();
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                List<Wsn> wsns = Analysis.parseWsn(in);
                if (wsns != null)
                    return wsns;
                else
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //发送数据
    public static void controlExecute(String msg){
        JSONStringer stringer = new JSONStringer();
        HttpURLConnection conn = setConnection(CONTROL_SERVLET);
        try {
            String json=String.valueOf(stringer.object().key("state").value(msg).endObject());
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //
    public static void autoControlExecute(String auto,String state,String leave){
        JSONStringer stringer = new JSONStringer();
        HttpURLConnection conn = setConnection(AUTO_CONTROL_SERVLET);
        try {
            String json=String.valueOf(stringer.object().key("auto").value(auto).key("state").value(state).key("leave").value(leave).endObject());
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
