import DSA.KeyGenerator;
import DSA.PublicKey;
import DSA.Signing;
import math.MyBigInteger;

public class Main {

    public static void main(String[] args)
    {
        System.out.println("Key Generation...");
        KeyGenerator kg = new KeyGenerator();
        kg.generateKey();

        System.out.println("Key generated.");
        Signing s = new Signing();

        System.out.println("Signing...");
        MyBigInteger signature[] = s.sign(kg.getPrivateKey(), kg.getPublicKey(), "ala ma kota");
        System.out.println("Signed.");

        PublicKey publicKey = new PublicKey(kg.getPublicKey().getB(), kg.getPublicKey().getQ(), MyBigInteger.valueOf(123), MyBigInteger.valueOf(123));
        System.out.println("Verifying...");
        System.out.println("Correct signature: " + s.verify(signature, "ala ma kota", kg.getPublicKey()));
    }
}
