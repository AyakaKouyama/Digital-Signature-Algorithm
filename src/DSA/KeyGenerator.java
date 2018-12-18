package DSA;
import math.MyBigInteger2;

public class KeyGenerator {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }

    public MyBigInteger2 generateG(MyBigInteger2 p, MyBigInteger2 q){
        MyBigInteger2 h = MyBigInteger2.ONE.shiftLeft(159);
        MyBigInteger2 exp = p.subtract(MyBigInteger2.ONE).divide(q);
        do {
            h = h.add(MyBigInteger2.ONE);
        }while(h.modPow(exp, p).compareTo(MyBigInteger2.ONE) <= 0);
        return  h.modPow(exp, p);

    }

    public MyBigInteger2[] generatePAndQ(){

        RandomNumberGenerator random = new RandomNumberGenerator(System.currentTimeMillis());

        final int pSizeInBits = 1024;
        final int qSizeInBits = 160;
        MyBigInteger2 q = MyBigInteger2.ONE;
        q = q.probablePrime(qSizeInBits, random);
        MyBigInteger2 k = MyBigInteger2.ONE.shiftLeft(pSizeInBits - qSizeInBits); // k = 2**(pSizeInBits - qSizeInBits);

        MyBigInteger2 probablyPrime = q.multiply(k).add(MyBigInteger2.ONE); // probablyPrime = q * k + 1
        while (!probablyPrime.isProbablePrime(4)) {
            q = q.probablePrime(qSizeInBits, random);
            probablyPrime = q.multiply(k).add(MyBigInteger2.ONE);
        }

        MyBigInteger2[] qAndP = new MyBigInteger2[2];
        qAndP[0] = q;
        qAndP[1] = probablyPrime;

        return qAndP;
    }

    public void generateKey() {

        MyBigInteger2 a = new  MyBigInteger2(160, new RandomNumberGenerator(System.currentTimeMillis()));
        MyBigInteger2 qAndp[] = generatePAndQ();
        MyBigInteger2 p = qAndp[1];
        MyBigInteger2 q = qAndp[0];
        MyBigInteger2 g = generateG(p, q);
        MyBigInteger2 b = g.modPow(a, p);

        publicKey = new PublicKey(b, g, p, q);
        privateKey = new PrivateKey(a);
    }

}
