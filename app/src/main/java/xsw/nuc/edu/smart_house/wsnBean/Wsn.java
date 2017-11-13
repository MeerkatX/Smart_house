package xsw.nuc.edu.smart_house.wsnBean;

/**
 * Smart_house
 * Created by 11749 on 2017/9/11,下午 5:54.
 */

public class Wsn {
    int funcCode;
    int data;

    public Wsn(int funcCode,int data){
        this.funcCode=funcCode;
        this.data=data;
    }

    public Wsn(){}

    public int getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(int funcCode) {
        this.funcCode = funcCode;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
