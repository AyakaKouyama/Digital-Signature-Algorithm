package DSA;
import math.MyBigInteger;

public class Signing {

    public MyBigInteger[] sign(PrivateKey privateKey, PublicKey publicKey,String message){

        MyBigInteger signature[] = new MyBigInteger[2];
        MyBigInteger hash = new MyBigInteger(SHA.SHAHash(message), 16);

        MyBigInteger k;
        do {
           k = new MyBigInteger(160, new RandomNumberGenerator(System.currentTimeMillis()));
        }while(k.bitLength() != 160 || k.gcd(publicKey.getQ()).compareTo(MyBigInteger.valueOf(1)) != 0 || k.compareTo(publicKey.getQ()) > 0);

        MyBigInteger k_inv = k.modInverse(publicKey.getQ());
        signature[0] = publicKey.getG().modPow(k, publicKey.getP()).mod(publicKey.getQ());
        MyBigInteger multi = privateKey.getA().multiply(signature[0]);
        MyBigInteger sum = multi.add(hash);
        signature[1] = k_inv.multiply(sum).mod(publicKey.getQ());

        return signature;
    }

    public boolean verify(MyBigInteger[] signature, String message, PublicKey publicKey){

        MyBigInteger hash = new MyBigInteger(SHA.SHAHash(message), 16);
        MyBigInteger w =  signature[1].modInverse(publicKey.getQ());
        MyBigInteger u1 = hash.multiply(w).mod(publicKey.getQ());
        MyBigInteger u2 = (signature[0].multiply(w)).mod(publicKey.getQ());

        MyBigInteger t1 = publicKey.getG().modPow(u1, publicKey.getP());
        MyBigInteger t2 = publicKey.getB().modPow(u2, publicKey.getP());
        MyBigInteger t3 = t1.multiply(t2);
        MyBigInteger t4 = t3.mod(publicKey.getP());
        MyBigInteger t  = t4.mod(publicKey.getQ());

        return  t.compareTo(signature[0]) == 0;

    }
}
