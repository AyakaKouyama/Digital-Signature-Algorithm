package DSA;

public class SHA {

    public static String SHAHash(String str) {

        byte[] stringBytes = str.getBytes();

        int[] blocks = new int[(((stringBytes.length + 8) >> 6) + 1) * 16];

        int i;
        for(i = 0; i < stringBytes.length; i++) {
            blocks[i >> 2] |= stringBytes[i] << (24 - (i % 4) * 8);
        }

        blocks[i >> 2] |= 0x80 << (24 - (i % 4) * 8);
        blocks[blocks.length - 1] = stringBytes.length * 8; // na końcu długość wiadomości

        int[] w = new int[80];

        // Wartości początkowe:
        int h0 =  1732584193;
        int h1 = -271733879;
        int h2 = -1732584194;
        int h3 =  271733878;
        int h4 = -1009589776;

        // for (każda porcja)
        //   podziel porcję na 16 32-bitowych słów kodowanych big-endian w(i), 0 ≤ i ≤ 15
        for(i = 0; i < blocks.length; i += 16) {

           // Zainicjuj zmienne dla tej porcji:
            int a = h0;
            int b = h1;
            int c = h2;
            int d = h3;
            int e = h4;


            for(int j = 0; j < 80; j++) {

                // Rozszerz 16 32-bitowych słów w 80 32-bitowych słów:
                // for i from 16 to 79
                //  w(i) := (w(i-3) xor w(i-8) xor w(i-14) xor w(i-16)) <<< 1

                if(j < 16){
                    w[j] = blocks[i + j];
                }else{
                    w[j] = rot(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
                }

                int k = 0;
                int f = 0;

                 if(j < 20){
                     f = ((b & c) | ((~b) & d));
                     k = 1518500249;
                 }else if(j < 40){
                     f = (b ^ c ^ d);
                     k = 1859775393;
                 }else if(j < 60){
                     f = ((b & c) | (b & d) | (c & d));
                     k = -1894007588;
                 }else if(j < 80){
                     f = (b ^ c ^ d);
                     k = -899497514;
                 }

                int temp = rot(a, 5) + e + w[j] + f + k;

                e = d;
                d = c;
                c = rot(b, 30);
                b = a;
                a = temp;
            }

            //Dodaj skrót tej porcji do dotychczasowego wyniku:
            h0 += a;
            h1 += b;
            h2 += c;
            h3 += d;
            h4 += e;
        }

        //skrót = h0 dopisz h1 dopisz h2 dopisz h3 dopisz h4
        int[] words = {h0,h1,h2,h3,h4};
        StringBuilder stringBuilder = new StringBuilder();

        //zamiana skrótu na stringa zapisanego w hexie
        for(int j = 0; j< words.length; j++)
        {
            String wordHex = Integer.toHexString(words[j]);

            while(wordHex.length() < 8){
                wordHex += '0';
            }

            stringBuilder.append(wordHex);
        }

        return stringBuilder.toString();
    }

    private static int rot(int number, int count) {
        return (number << count) | (number >>> (32 - count));
    }
}
