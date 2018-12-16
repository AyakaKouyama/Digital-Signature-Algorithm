package DSA;
import math.MyBigInteger;

public class PublicKey {

    private MyBigInteger b;
    private MyBigInteger g;
    private MyBigInteger p;
    private MyBigInteger q;

    public PublicKey(MyBigInteger b, MyBigInteger g, MyBigInteger p, MyBigInteger q){
        this.b = b;
        this.g = g;
        this.p = p;
        this.q = q;
    }

    public void setB(MyBigInteger b){
        this.b = b;
    }

    public void setG(MyBigInteger g){
        this.g = g;
    }

    public void setP(MyBigInteger p){
        this.p = p;
    }

    public  void setQ(MyBigInteger q){ this. q = q;}

    public MyBigInteger getB(){
        return b;
    }

    public MyBigInteger getG(){
        return g;
    }

    public MyBigInteger getP(){
        return p;
    }
    public MyBigInteger getQ() { return q;}
}
