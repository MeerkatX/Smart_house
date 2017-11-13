package xsw.nuc.edu.smart_house.threadControl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import xsw.nuc.edu.smart_house.httpUtils.HttpConn;
import xsw.nuc.edu.smart_house.wsnBean.Wsn;

/**
 * Smart_house
 * Created by 11749 on 2017/9/12,下午 3:40.
 */

public class PrintDarw extends Thread {
    private List<PointValue> mPointValues_1 = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private List<AxisValue> b_k = new ArrayList<AxisValue>();
    private LineChartView lineChart_1;
    private Context context;
    private float hum;
    private float wet;
    private float light;
    private int position = 0;
    private Intent intent;
    private Viewport port;
    private List<Wsn> wsns;
    private int i=10000;
    private ControlSignal control_signal;

    public PrintDarw(Context context, LineChartView lineChart_1,
                     ControlSignal control_signal) {
        this.lineChart_1 = lineChart_1;
        this.context = context;
        this.control_signal = control_signal;
        intent = new Intent("showMs");
    }

    public void run() {
        while (i>0) {
            if (control_signal.flag) {
                new GetWsnData().execute();
                print(lineChart_1, mPointValues_1, "#B5B5B5", "", mAxisXValues);
                try {
                    sleep(2000);
                    mAxisXValues.add(new AxisValue(position).setLabel(""));

                    //light = (float) (new Random().nextInt() % 5 + 50);

                    if (wsns!=null)
                    for (int j=0;j<wsns.size();j++){
                        if (wsns.get(j).getFuncCode()==3){
                            light=wsns.get(j).getData();
                            Log.d("aaaa", "run: "+wsns.get(j).getData());
                        }
                    }

                    mPointValues_1.add(new PointValue(position, light));

                    position++;
                    if (position > 10)
                        port = initViewPort(position - 10, position);
                    else
                        port = initViewPort(0, 10);

                    setDraw(lineChart_1, port);
                    intent.putExtra("light", light + " ");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    Log.i("tttttt", "onStart" + (new Random().nextInt() % 50 + 50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        i--;
        }
    }

    private void setDraw(LineChartView lineChart, Viewport port) {
        lineChart.setMaximumViewport(port);
        lineChart.setCurrentViewport(port);
    }

    private Viewport initViewPort(int left, int right) {
        Viewport port = new Viewport();
        port.top = 700;                //Y轴上限，固定(不固定上下限的话，Y轴坐标值可自适应变化)
        port.bottom = 0;            //Y轴下限，固定
        port.left = left;            //X轴左边界，变化
        port.right = right;            //X轴右边界，变化
        return port;
    }

    private void print(LineChartView lineChart, List<PointValue> pointValues
            , String color_, String name, List<AxisValue> axisValues) {
        Line line = new Line(pointValues).setColor(Color.parseColor(color_));
        List<Line> lines = new ArrayList<Line>();
        line.setShape((ValueShape.CIRCLE));//折线图上每个数据点的形状
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        //line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels就无效了）
        line.setHasLines(true);//是否用线显示，如果为false则只有点
        line.setHasPoints(false);//是否显示圆点，

        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis();//X轴
        axisX.setHasTiltedLabels(false);//X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);//设置字体颜色
        //axisX.setName("时间");//表格名称
        axisX.setTextSize(8);//设置字体大小
        axisX.setMaxLabelChars(10);//最多几个X轴坐标，意思是缩放让X轴上数据的个数为7
        axisX.setValues(axisValues);//填充X轴的做表名称
        //data.setAxisXBottom(axisX);//X轴在底部
        data.setAxisXTop(axisX);//X轴在顶部
        axisX.setHasLines(true);//x轴分割线

        //Y轴是根据数据的大小自动设置Y轴上限
        Axis axisY = new Axis();//Y轴
        //axisY.setName("温度");//y轴标注
        axisY.setTextSize(8);//设置字体大小
        data.setAxisYLeft(axisY);//Y轴设置在左边
        //data.setAxisYRight(axisY);//y轴设置在右边
        axisY.setName(name);

        //设置行为属性，支持缩放，滑动以及平移
        lineChart.setInteractive(false);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.setScrollEnabled(true);
        lineChart.setValueTouchEnabled(false);
        lineChart.setFocusableInTouchMode(false);
        lineChart.setViewportCalculationEnabled(false);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
    }


    class GetWsnData extends AsyncTask<String, String, List<Wsn>> {

        @Override
        protected List<Wsn> doInBackground(String... params) {
            wsns = HttpConn.getWsnData();
            return wsns;
        }
    }
}
