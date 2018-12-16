package DSA;
import math.MyBigInteger;

public class PrivateKey {

    private MyBigInteger a;

    public PrivateKey(MyBigInteger a){
        this.a = a;

    }
    public void setA(MyBigInteger a){
        this.a = a;
    }

    public MyBigInteger getA(){
        return a;
    }

}
