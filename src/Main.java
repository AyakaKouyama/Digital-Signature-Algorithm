import DSA.*;
import math.MyBigInteger;
import math.MyBigInteger2;

import java.math.BigInteger;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        System.out.println("Key Generation...");
        KeyGenerator kg = new KeyGenerator();
        kg.generateKey();

        System.out.println("Key generated.");
        Signing s = new Signing();

        System.out.println("Signing...");
        MyBigInteger2 signature[] = s.sign(kg.getPrivateKey(), kg.getPublicKey(), "ala ma kota");
        System.out.println("Signed.");

        System.out.println("Verifying...");
        System.out.println("Correct signature: " + s.verify(signature, "ala ma kota", kg.getPublicKey()));
    }
}
