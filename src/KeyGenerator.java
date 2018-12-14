import java.math.BigInteger;
import java.util.Random;

public class KeyGenerator {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }

    public BigInteger generateG(BigInteger p, BigInteger q){
        BigInteger h = BigInteger.valueOf(1);
        BigInteger exp = p.subtract(BigInteger.valueOf(1)).divide(q);
        do {
            h = h.add(BigInteger.valueOf(1));
        }while(h.modPow(exp, p).compareTo(BigInteger.valueOf(1)) <= 0);

        return  h.modPow(exp, p);

    }

    public BigInteger[] generatePAndQ(){

        Random random = new Random();

        final int pSizeInBits = 1024;
        final int qSizeInBits = 160;
        BigInteger q = BigInteger.probablePrime(qSizeInBits, random);
        BigInteger k = BigInteger.ONE.shiftLeft(pSizeInBits - qSizeInBits); // k = 2**(pSizeInBits - qSizeInBits);

        BigInteger probablyPrime = q.multiply(k).add(BigInteger.ONE); // probablyPrime = q * k + 1
        while (!probablyPrime.isProbablePrime(50)) {
            q = BigInteger.probablePrime(qSizeInBits, random);
            probablyPrime = q.multiply(k).add(BigInteger.ONE);
        }

        BigInteger[] qAndP = new BigInteger[2];
        qAndP[0] = q;
        qAndP[1] = probablyPrime;

        return qAndP;
    }


    public void generateKey() {

        BigInteger a = new BigInteger(160, new Random());
        BigInteger qAndp[] = generatePAndQ();
        BigInteger p = qAndp[1];
        BigInteger q = qAndp[0];
        BigInteger g = generateG(p, q);
        BigInteger b = g.modPow(a, p);

        publicKey = new PublicKey(b, g, p, q);
        privateKey = new PrivateKey(a);
    }

}

