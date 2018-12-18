package math;

import DSA.RandomNumberGenerator;
import java.math.BigInteger;
import java.util.Random;

public class MyBigInteger2 implements Comparable<MyBigInteger2> {

    private int[] digits;
    private int length;
    public static final MyBigInteger2 ZERO = MyBigInteger2.valueOf("0");
    public static final MyBigInteger2 ONE = MyBigInteger2.valueOf("1");
    public static final MyBigInteger2 TWO = MyBigInteger2.valueOf("2");
    public static final MyBigInteger2 THREE = MyBigInteger2.valueOf("3");

    public MyBigInteger2(int numberOfBits, RandomNumberGenerator random) {
        byte[] byteArray;
        byteArray = fillByteArray(numberOfBits, random);
        digits = stripLeadingZeroBytes(byteArray);
        length = digits.length;
    }

    public MyBigInteger2(String value, int radix) {
        this(new BigInteger(value, radix).toString());
    }

    private byte[] fillByteArray(int numBits, RandomNumberGenerator random) {

        int numBytes = (int) (((long) numBits + 7) / 8);
        byte[] randomBits = new byte[numBytes];

        if (numBytes > 0) {
            random.nextBytes(randomBits);
            int excessBits = 8 * numBytes - numBits;
            randomBits[0] &= (1 << (8 - excessBits)) - 1;
        }
        return randomBits;
    }

    private static int[] stripLeadingZeroBytes(byte a[]) {
        int byteLength = a.length;
        int keep;

        for (keep = 0; keep < byteLength && a[keep] == 0; keep++)
            ;

        int intLength = ((byteLength - keep) + 3) >>> 2;
        int[] result = new int[intLength];
        int b = byteLength - 1;
        for (int i = intLength - 1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;
            int bytesRemaining = b - keep + 1;
            int bytesToTransfer = Math.min(3, bytesRemaining);
            for (int j = 8; j <= (bytesToTransfer << 3); j += 8)
                result[i] |= ((a[b--] & 0xff) << j);

            result[i] = Math.abs(result[i]);
        }
        return result;
    }

    public static MyBigInteger2 valueOf(String value) {
        return new MyBigInteger2(value);
    }

    public MyBigInteger2(int[] value) {
        length = value.length;
        digits = new int[length];

        for (int i = 0; i < length; i++) {
            digits[i] = value[i];
        }
    }

    public MyBigInteger2(String value) {
        digits = new int[value.length()];
        length = value.length();
        setValue(value);
    }

    public void setValue(String value) {
        for (int i = value.length() - 1, j = length - 1; i >= 0; i--, j--) {
            digits[j] = Character.getNumericValue(value.charAt(i));
        }
    }

    public int[] getArrayValue() {
        return digits;
    }

    public int digitAt(int position) {
        return digits[position];
    }

    public int getNumberOfBits() {
        return length;
    }

    public MyBigInteger2 generateRandomNumber(RandomNumberGenerator random, int length, int[] array) {
        for (int i = 0; i < length; i++) {
            array[i] = random.random(9);
        }
        return new MyBigInteger2(array);
    }

    public MyBigInteger2 add(MyBigInteger2 number) {
        if (number.length > length) {
            return number.add(this);
        } else {
            int[] result = new int[digits.length + 1];

            result[0] = 0;
            for (int i = 0; i < digits.length; i++) {
                result[i + 1] = digits[i];
            }
            for (int i = result.length - 1, j = number.getNumberOfBits() - 1; j >= 0; i--, j--) {
                result[i] += number.digitAt(j);
                if (result[i] > 9) {
                    result[i] = result[i] % 10;
                    result[i - 1] += 1;
                }
            }

            return new MyBigInteger2(result);
        }
    }

    public MyBigInteger2 divide(MyBigInteger2 number) {
        int count = 0;
        BigInteger a = new BigInteger(this.toString());
        BigInteger b = new BigInteger(number.toString());

        // while (a.compareTo(b) >= 0) {
        a = a.subtract(b);
        count++;
        // }

        // ^ it works but it too slow .-.;
        BigInteger result = new BigInteger(this.toString()).divide(new BigInteger(number.toString()));
        return new MyBigInteger2(result.toString());
    }

    public MyBigInteger2 reminder(MyBigInteger2 number) {
        BigInteger a = new BigInteger(this.toString());
        BigInteger b = new BigInteger(number.toString());

        //while (a.compareTo(b) >= 0) {
        a = a.subtract(b);
        // }

        BigInteger reminder = new BigInteger(this.toString()).remainder(new BigInteger(number.toString()));
        return new MyBigInteger2(reminder.toString());
    }

    public MyBigInteger2 mod(MyBigInteger2 number) {
        return reminder(number);
    }

    public MyBigInteger2 multiply(MyBigInteger2 number) {
        return new MyBigInteger2(new BigInteger(this.toString()).multiply(new BigInteger(number.toString())).toString());
       /* int n1 = length;
        int n2 = number.length;
        int[] result;
        if(number.length == this.length){
            result = new int[length * number.length + 1];
            return new MyBigInteger2((new BigInteger(toString()).multiply(new BigInteger(number.toString()))).toString());
        }
        else if(number.length > this.length){
            return number.multiply(this);
        }
        else
        {
            result = new int[length * number.length];

        int i_n1 = 0;
        int i_n2 = 0;

        for (int i = n1 - 1; i >= 0; i--) {

            int carry = 0;
            int n11 = digits[i];
            i_n2 = 0;

            for (int j = n2 - 1; j >= 0; j--) {
                int n22 = number.digits[j];
                int sum = n11 * n22 + result[i_n1 + i_n2] + carry;
                carry = sum / 10;
                result[i_n1 + i_n2] = sum % 10;

                i_n2++;
            }
            if (carry > 0)
                result[i_n1 + i_n2] += carry;
            i_n1++;
        }

        for (int i = 0; i < length * number.length / 2; i++) {
            int temp = result[i];
            result[i] = result[result.length - i - 1];
            result[result.length - i - 1] = temp;
        }

        return new MyBigInteger2(result);} */
    }

    public MyBigInteger2 subtract(MyBigInteger2 number) {
        if (this.length < number.length) {
            return number.subtract(this);
        } else {
            int[] result = new int[digits.length];
            result[0] = 0;
            for (int i = 0; i < digits.length; i++) {
                result[i] = digits[i];
            }
            for (int i = length - 1, j = number.getNumberOfBits() - 1; j >= 0; i--, j--) {
                if (result[i] - number.digitAt(j) < 0) {
                    if (result[i - 1] == 0) {
                        result[i - 2] -= 1;
                        result[i - 1] += 9;
                    } else {
                        result[i - 1] -= 1;
                    }
                    result[i] += 10;
                }
                result[i] -= number.digitAt(j);
            }

            return new MyBigInteger2(result);
        }

    }

    public MyBigInteger2 gcd(MyBigInteger2 number) {
        return gcdTwoNumbers(this, number);
    }

    private MyBigInteger2 gcdTwoNumbers(MyBigInteger2 a, MyBigInteger2 b) {
        if (b.compareTo(MyBigInteger2.ZERO) == 0) return new MyBigInteger2(a.toString());
        return gcdTwoNumbers(b, a.mod(b));
    }

    public MyBigInteger2 modPow(MyBigInteger2 number, MyBigInteger2 modulo) {
        // return new MyBigInteger2(new BigInteger(this.toString()).modPow(new BigInteger(number.toString()), new BigInteger(modulo.toString())).toString());
        return modPowTwoNumbers(new BigInteger(toString()), new BigInteger(number.toString()), new BigInteger(modulo.toString()));
    }

    private MyBigInteger2 modPowTwoNumbers(BigInteger x, BigInteger y, BigInteger p) {
        BigInteger res = BigInteger.ONE;
        x = x.mod(p);

        while (y.compareTo(BigInteger.ZERO) > 0) {
            if (y.and(BigInteger.ONE).compareTo(BigInteger.ONE) == 0) {
                res = (res.multiply(x).mod(p));
            }
            y = y.shiftRight(1);
            x = x.multiply(x).mod(p);
        }
        return new MyBigInteger2(res.toString());
    }

    public MyBigInteger2 modInverse(MyBigInteger2 number) {
        return modInverseTwoNumbers(this, number);
    }

    private MyBigInteger2 modInverseTwoNumbers(MyBigInteger2 a, MyBigInteger2 m) {
        BigInteger m0 = new BigInteger(m.toString());
        BigInteger m1 = new BigInteger(m.toString());
        BigInteger a1 = new BigInteger(a.toString());
        BigInteger y = BigInteger.valueOf(0);
        BigInteger x = BigInteger.valueOf(1);

        if (m.compareTo(MyBigInteger2.ONE) == 0)
            return MyBigInteger2.ZERO;

        while (a1.compareTo(BigInteger.ONE) > 0) {
            BigInteger q = a1.divide(m1);
            BigInteger t = m1;

            m1 = a1.mod(m1);
            a1 = t;
            t = y;

            y = x.subtract(q.multiply(y));
            x = t;
        }

        if (x.compareTo(BigInteger.ZERO) < 0)
            x = x.add(m0);

        return new MyBigInteger2(x.toString());
    }

    public boolean isProbablePrime(int k) { // Miller Rabin test
        BigInteger n = new BigInteger(this.toString());

        if (n.compareTo(BigInteger.ONE) == 0)
            return false;
        if (n.compareTo(BigInteger.valueOf(3)) < 0)
            return true;
        int s = 0;
        BigInteger d = n.subtract(BigInteger.ONE);
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s++;
            d = d.divide(BigInteger.TWO);
        }
        for (int i = 0; i < k; i++) {
            BigInteger a = uniformRandom(BigInteger.TWO, n.subtract(BigInteger.ONE));
            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE)))
                continue;
            int r = 0;
            for (; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(BigInteger.ONE))
                    return false;
                if (x.equals(n.subtract(BigInteger.ONE)))
                    break;
            }
            if (r == s)
                return false;
        }

        return true;
    }

    private BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
        Random random = new Random();
        BigInteger res;

        do {
            res = new BigInteger(top.bitLength(), random);
        } while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
        return res;
    }

    public int bitLength() {
        return new BigInteger(this.toString()).bitLength();
    }

    public MyBigInteger2 shiftLeft(int n) {
        return new MyBigInteger2(new BigInteger(this.toString()).shiftLeft(n).toString());
    }

    public MyBigInteger2 probablePrime(int bitLength, RandomNumberGenerator randomNumberGenerator) {
        MyBigInteger2 prime;
        do {

            do {
                prime = new MyBigInteger2(bitLength, randomNumberGenerator);
            }
            while (prime.mod(MyBigInteger2.TWO).compareTo(MyBigInteger2.ZERO) == 0); // odrzucamy parzyste liczby bo wiadomo że nie są pierwsze

        } while (!prime.isProbablePrime(6));

        return new MyBigInteger2(prime.toString());
    }


    public int findBeginning() {
        int i = 0;
        do {
            i++;
        } while (i < length && digits[i] == 0);

        if (i == length) return 0;
        else
            return i - 1;
    }

    public String convertToString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            stringBuilder.append(Integer.toString(digits[i]));
        }
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(MyBigInteger2 o) {
        if ((length - findBeginning()) > (o.length - o.findBeginning())) {
            return 1;
        } else if ((length - findBeginning()) < (o.length - o.findBeginning())) {
            return -1;
        } else if ((length - findBeginning()) == (o.length - o.findBeginning())) {
            boolean comparsion = false;
            int i = findBeginning();
            int j = o.findBeginning();

            do {
                comparsion = (digits[i] == o.digits[j]);
                i++;
                j++;
            } while (comparsion == true && i != getNumberOfBits() && j != o.getNumberOfBits());

            if (digits[i - 1] > o.digits[j - 1]) {
                return 1;
            } else if (digits[i - 1] < o.digits[j - 1]) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return convertToString();
    }

    @Override
    public boolean equals(Object x) {
        if (x == this)
            return true;

        if (!(x instanceof MyBigInteger2))
            return false;

        MyBigInteger2 xInt = (MyBigInteger2) x;

        if (this.length != xInt.length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (digits[i] != xInt.digits[i]) {
                return false;
            }
        }

        return true;
    }
}