import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Signing {

    public BigInteger[] sign(PrivateKey privateKey, PublicKey publicKey,String message){

        BigInteger signature[] = new BigInteger[2];
        BigInteger hash = new BigInteger(hash(message), 16);

        BigInteger k;
        do {
           k = new BigInteger(8, new Random());
        }while(k.gcd(publicKey.getQ()).compareTo(BigInteger.valueOf(1)) != 0 || k.compareTo(publicKey.getQ()) > 0);

        BigInteger k_inv = k.modInverse(publicKey.getQ());
        signature[0] = publicKey.getG().modPow(k, publicKey.getP()).mod(publicKey.getQ());
        BigInteger multi = privateKey.getA().multiply(signature[0]);
        BigInteger sum = multi.add(hash);
        signature[1] = k_inv.multiply(sum).mod(publicKey.getQ());

        return signature;
    }


    public boolean verify(BigInteger[] signature, String message, PublicKey publicKey){

        BigInteger hash = new BigInteger(hash(message), 16);
        BigInteger w =  signature[1].modInverse(publicKey.getQ());
        BigInteger u1 = hash.multiply(w).mod(publicKey.getQ());
        BigInteger u2 = (signature[0].multiply(w)).mod(publicKey.getQ());

        BigInteger t1 = publicKey.getG().modPow(u1, publicKey.getP());
        BigInteger t2 = publicKey.getB().modPow(u2, publicKey.getP());
        BigInteger t3 = t1.multiply(t2);
        BigInteger t4 = t3.mod(publicKey.getP());
        BigInteger t = t4.mod(publicKey.getQ());

        return  t.compareTo(signature[0]) == 0;

    }


    private String hash(Object data){
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(StandardCharsets.UTF_8.encode(data.toString()));
            return String.format("%032x", new BigInteger(1, sha512.digest())).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
