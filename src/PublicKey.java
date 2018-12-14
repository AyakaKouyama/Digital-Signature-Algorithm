import java.math.BigInteger;

public class PublicKey {

    private BigInteger b;
    private BigInteger g;
    private BigInteger p;
    private BigInteger q;

    public PublicKey(BigInteger b, BigInteger g, BigInteger p, BigInteger q){
        this.b = b;
        this.g = g;
        this.p = p;
        this.q = q;
    }

    public void setB(BigInteger b){
        this.b = b;
    }

    public void setG(BigInteger g){
        this.g = g;
    }

    public void setP(BigInteger p){
        this.p = p;
    }

    public  void setQ(BigInteger q){ this. q = q;}

    public BigInteger getB(){
        return b;
    }

    public BigInteger getG(){
        return g;
    }

    public BigInteger getP(){
        return p;
    }
    public BigInteger getQ() { return q;}
}
