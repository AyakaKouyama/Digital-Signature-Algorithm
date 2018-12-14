import java.math.BigInteger;

public class PrivateKey {

    private BigInteger a;

    public PrivateKey(BigInteger a){
        this.a = a;

    }
    public void setA(BigInteger a){
        this.a = a;
    }

    public BigInteger getA(){
        return a;
    }

}
