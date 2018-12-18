package DSA;
import math.MyBigInteger2;

public class PublicKey {

    private MyBigInteger2 b;
    private MyBigInteger2 g;
    private MyBigInteger2 p;
    private MyBigInteger2 q;

    public PublicKey(MyBigInteger2 b, MyBigInteger2 g, MyBigInteger2 p, MyBigInteger2 q){
        this.b = b;
        this.g = g;
        this.p = p;
        this.q = q;
    }

    public void setB(MyBigInteger2 b){
        this.b = b;
    }

    public void setG(MyBigInteger2 g){
        this.g = g;
    }

    public void setP(MyBigInteger2 p){
        this.p = p;
    }

    public  void setQ(MyBigInteger2 q){ this. q = q;}

    public MyBigInteger2 getB(){
        return b;
    }

    public MyBigInteger2 getG(){
        return g;
    }

    public MyBigInteger2 getP(){
        return p;
    }
    public MyBigInteger2 getQ() { return q;}
}
