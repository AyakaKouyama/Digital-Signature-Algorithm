package DSA;
import math.MyBigInteger2;

public class Signing {

    public MyBigInteger2[] sign(PrivateKey privateKey, PublicKey publicKey, String message){

        MyBigInteger2 signature[] = new MyBigInteger2[2];
        MyBigInteger2 hash = new MyBigInteger2(SHA.SHAHash(message), 16);

        RandomNumberGenerator random = new RandomNumberGenerator(System.currentTimeMillis());
        MyBigInteger2 k;
        do {
            k = new MyBigInteger2(160, random);
        }while(k.bitLength() != 160 || k.gcd(publicKey.getQ()).compareTo(MyBigInteger2.ONE) != 0 || k.compareTo(publicKey.getQ()) > 0);

        MyBigInteger2 k_inv = k.modInverse(publicKey.getQ());
        signature[0] = publicKey.getG().modPow(k, publicKey.getP()).mod(publicKey.getQ());
        MyBigInteger2 multi = privateKey.getA().multiply(signature[0]);
        MyBigInteger2 sum = multi.add(hash);
        signature[1] = k_inv.multiply(sum).mod(publicKey.getQ());

        return signature;
    }

    public boolean verify(MyBigInteger2[] signature, String message, PublicKey publicKey){

        MyBigInteger2 hash = new MyBigInteger2(SHA.SHAHash(message), 16);
        MyBigInteger2 w =  signature[1].modInverse(publicKey.getQ());
        MyBigInteger2 u1 = hash.multiply(w).mod(publicKey.getQ());
        MyBigInteger2 u2 = (signature[0].multiply(w)).mod(publicKey.getQ());

        MyBigInteger2 t1 = publicKey.getG().modPow(u1, publicKey.getP());
        MyBigInteger2 t2 = publicKey.getB().modPow(u2, publicKey.getP());
        MyBigInteger2 t3 = t1.multiply(t2);
        MyBigInteger2 t4 = t3.mod(publicKey.getP());
        MyBigInteger2 t  = t4.mod(publicKey.getQ());

        return  t.compareTo(signature[0]) == 0;

    }
}
