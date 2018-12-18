package DSA;
import math.MyBigInteger2;

public class PrivateKey {

    private MyBigInteger2 a;

    public PrivateKey(MyBigInteger2 a){
        this.a = a;

    }
    public void setA(MyBigInteger2 a){
        this.a = a;
    }

    public MyBigInteger2 getA(){
        return a;
    }

}
