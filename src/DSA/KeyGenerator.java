package DSA;
import math.MyBigInteger;

public class KeyGenerator {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }

    public MyBigInteger generateG(MyBigInteger p, MyBigInteger q){
        MyBigInteger h = MyBigInteger.ONE.shiftLeft(159);
        MyBigInteger exp = p.subtract(MyBigInteger.ONE).divide(q);
        do {
            h = h.add(MyBigInteger.ONE);
        }while(h.modPow(exp, p).compareTo(MyBigInteger.ONE) <= 0);
        return  h.modPow(exp, p);

    }

    public MyBigInteger[] generatePAndQ(){

        RandomNumberGenerator random = new RandomNumberGenerator(System.currentTimeMillis());

        final int pSizeInBits = 1024;
        final int qSizeInBits = 160;
        MyBigInteger q = MyBigInteger.ONE;
        q = q.probablePrime(qSizeInBits, random);
        MyBigInteger k = MyBigInteger.ONE.shiftLeft(pSizeInBits - qSizeInBits); // k = 2**(pSizeInBits - qSizeInBits);

        MyBigInteger probablyPrime = q.multiply(k).add(MyBigInteger.ONE); // probablyPrime = q * k + 1
        while (!probablyPrime.isProbablePrime(4)) {
            q = q.probablePrime(qSizeInBits, random);
            probablyPrime = q.multiply(k).add(MyBigInteger.ONE);
        }

        MyBigInteger[] qAndP = new MyBigInteger[2];
        qAndP[0] = q;
        qAndP[1] = probablyPrime;

        return qAndP;
    }

    public void generateKey() {

        MyBigInteger a = new  MyBigInteger(160, new RandomNumberGenerator(System.currentTimeMillis()));
        MyBigInteger qAndp[] = generatePAndQ();
        MyBigInteger p = qAndp[1];
        MyBigInteger q = qAndp[0];
        MyBigInteger g = generateG(p, q);
        MyBigInteger b = g.modPow(a, p);

        publicKey = new PublicKey(b, g, p, q);
        privateKey = new PrivateKey(a);
    }

}
