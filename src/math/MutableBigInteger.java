package math;
import java.util.Arrays;

class MutableBigInteger {

    final static long LONG_MASK = 0xffffffffL;
    int[] value;
    int intLen;
    int offset = 0;

    MutableBigInteger() {
        value = new int[1];
        intLen = 0;
    }


    MutableBigInteger(int val) {
        value = new int[1];
        intLen = 1;
        value[0] = val;
    }


    MutableBigInteger(int[] val) {
        value = val;
        intLen = val.length;
    }


    MutableBigInteger(MyBigInteger b) {
        intLen = b.mag.length;
        value = Arrays.copyOf(b.mag, intLen);
    }


    MutableBigInteger(MutableBigInteger val) {
        intLen = val.intLen;
        value = Arrays.copyOfRange(val.value, val.offset, val.offset + intLen);
    }


    private int[] getMagnitudeArray() {
        if (offset > 0 || value.length != intLen)
            return Arrays.copyOfRange(value, offset, offset + intLen);
        return value;
    }



    MyBigInteger toBigInteger(int sign) {
        if (intLen == 0 || sign == 0)
            return MyBigInteger.ZERO;
        return new MyBigInteger(getMagnitudeArray(), sign);
    }

    void clear() {
        offset = intLen = 0;
        for (int index=0, n=value.length; index < n; index++)
            value[index] = 0;
    }


    void reset() {
        offset = intLen = 0;
    }

    final int compare(MutableBigInteger b) {
        int blen = b.intLen;
        if (intLen < blen)
            return -1;
        if (intLen > blen)
            return 1;


        int[] bval = b.value;
        for (int i = offset, j = b.offset; i < intLen + offset; i++, j++) {
            int b1 = value[i] + 0x80000000;
            int b2 = bval[j]  + 0x80000000;
            if (b1 < b2)
                return -1;
            if (b1 > b2)
                return 1;
        }
        return 0;
    }


    private final int getLowestSetBit() {
        if (intLen == 0)
            return -1;
        int j, b;
        for (j=intLen-1; (j>0) && (value[j+offset]==0); j--)
            ;
        b = value[j+offset];
        if (b==0)
            return -1;
        return ((intLen-1-j)<<5) + Integer.numberOfTrailingZeros(b);
    }


    final void normalize() {
        if (intLen == 0) {
            offset = 0;
            return;
        }

        int index = offset;
        if (value[index] != 0)
            return;

        int indexBound = index+intLen;
        do {
            index++;
        } while(index < indexBound && value[index]==0);

        int numZeros = index - offset;
        intLen -= numZeros;
        offset = (intLen==0 ?  0 : offset+numZeros);
    }

    int[] toIntArray() {
        int[] result = new int[intLen];
        for(int i=0; i<intLen; i++)
            result[i] = value[offset+i];
        return result;
    }

    void setValue(int[] val, int length) {
        value = val;
        intLen = length;
        offset = 0;
    }


    void copyValue(MutableBigInteger src) {
        int len = src.intLen;
        if (value.length < len)
            value = new int[len];
        System.arraycopy(src.value, src.offset, value, 0, len);
        intLen = len;
        offset = 0;
    }


    boolean isOne() {
        return (intLen == 1) && (value[offset] == 1);
    }

    boolean isZero() {
        return (intLen == 0);
    }

    boolean isEven() {
        return (intLen == 0) || ((value[offset + intLen - 1] & 1) == 0);
    }

    boolean isOdd() {
        return isZero() ? false : ((value[offset + intLen - 1] & 1) == 1);
    }


    public String toString() {
        MyBigInteger b = toBigInteger(1);
        return b.toString();
    }

    void rightShift(int n) {
        if (intLen == 0)
            return;
        int nInts = n >>> 5;
        int nBits = n & 0x1F;
        this.intLen -= nInts;
        if (nBits == 0)
            return;
        int bitsInHighWord = MyBigInteger.bitLengthForInt(value[offset]);
        if (nBits >= bitsInHighWord) {
            this.primitiveLeftShift(32 - nBits);
            this.intLen--;
        } else {
            primitiveRightShift(nBits);
        }
    }

    void leftShift(int n) {

        if (intLen == 0)
            return;
        int nInts = n >>> 5;
        int nBits = n&0x1F;
        int bitsInHighWord = MyBigInteger.bitLengthForInt(value[offset]);


        if (n <= (32-bitsInHighWord)) {
            primitiveLeftShift(nBits);
            return;
        }

        int newLen = intLen + nInts +1;
        if (nBits <= (32-bitsInHighWord))
            newLen--;
        if (value.length < newLen) {

            int[] result = new int[newLen];
            for (int i=0; i<intLen; i++)
                result[i] = value[offset+i];
            setValue(result, newLen);
        } else if (value.length - offset >= newLen) {

            for(int i=0; i<newLen - intLen; i++)
                value[offset+intLen+i] = 0;
        } else {

            for (int i=0; i<intLen; i++)
                value[i] = value[offset+i];
            for (int i=intLen; i<newLen; i++)
                value[i] = 0;
            offset = 0;
        }
        intLen = newLen;
        if (nBits == 0)
            return;
        if (nBits <= (32-bitsInHighWord))
            primitiveLeftShift(nBits);
        else
            primitiveRightShift(32 -nBits);
    }


    private int divadd(int[] a, int[] result, int offset) {
        long carry = 0;

        for (int j=a.length-1; j >= 0; j--) {
            long sum = (a[j] & LONG_MASK) +
                    (result[j+offset] & LONG_MASK) + carry;
            result[j+offset] = (int)sum;
            carry = sum >>> 32;
        }
        return (int)carry;
    }


    private int mulsub(int[] q, int[] a, int x, int len, int offset) {
        long xLong = x & LONG_MASK;
        long carry = 0;
        offset += len;

        for (int j=len-1; j >= 0; j--) {
            long product = (a[j] & LONG_MASK) * xLong + carry;
            long difference = q[offset] - product;
            q[offset--] = (int)difference;
            carry = (product >>> 32)
                    + (((difference & LONG_MASK) >
                    (((~(int)product) & LONG_MASK))) ? 1:0);
        }
        return (int)carry;
    }


    private final void primitiveRightShift(int n) {
        int[] val = value;
        int n2 = 32 - n;
        for (int i=offset+intLen-1, c=val[i]; i>offset; i--) {
            int b = c;
            c = val[i-1];
            val[i] = (c << n2) | (b >>> n);
        }
        val[offset] >>>= n;
    }


    private final void primitiveLeftShift(int n) {
        int[] val = value;
        int n2 = 32 - n;
        for (int i=offset, c=val[i], m=i+intLen-1; i<m; i++) {
            int b = c;
            c = val[i+1];
            val[i] = (b << n) | (c >>> n2);
        }
        val[offset+intLen-1] <<= n;
    }


    void add(MutableBigInteger addend) {
        int x = intLen;
        int y = addend.intLen;
        int resultLen = (intLen > addend.intLen ? intLen : addend.intLen);
        int[] result = (value.length < resultLen ? new int[resultLen] : value);

        int rstart = result.length-1;
        long sum;
        long carry = 0;


        while(x>0 && y>0) {
            x--; y--;
            sum = (value[x+offset] & LONG_MASK) +
                    (addend.value[y+addend.offset] & LONG_MASK) + carry;
            result[rstart--] = (int)sum;
            carry = sum >>> 32;
        }


        while(x>0) {
            x--;
            if (carry == 0 && result == value && rstart == (x + offset))
                return;
            sum = (value[x+offset] & LONG_MASK) + carry;
            result[rstart--] = (int)sum;
            carry = sum >>> 32;
        }
        while(y>0) {
            y--;
            sum = (addend.value[y+addend.offset] & LONG_MASK) + carry;
            result[rstart--] = (int)sum;
            carry = sum >>> 32;
        }

        if (carry > 0) {
            resultLen++;
            if (result.length < resultLen) {
                int temp[] = new int[resultLen];

                System.arraycopy(result, 0, temp, 1, result.length);
                temp[0] = 1;
                result = temp;
            } else {
                result[rstart--] = 1;
            }
        }

        value = result;
        intLen = resultLen;
        offset = result.length - resultLen;
    }


    int subtract(MutableBigInteger b) {
        MutableBigInteger a = this;

        int[] result = value;
        int sign = a.compare(b);

        if (sign == 0) {
            reset();
            return 0;
        }
        if (sign < 0) {
            MutableBigInteger tmp = a;
            a = b;
            b = tmp;
        }

        int resultLen = a.intLen;
        if (result.length < resultLen)
            result = new int[resultLen];

        long diff = 0;
        int x = a.intLen;
        int y = b.intLen;
        int rstart = result.length - 1;


        while (y>0) {
            x--; y--;

            diff = (a.value[x+a.offset] & LONG_MASK) -
                    (b.value[y+b.offset] & LONG_MASK) - ((int)-(diff>>32));
            result[rstart--] = (int)diff;
        }

        while (x>0) {
            x--;
            diff = (a.value[x+a.offset] & LONG_MASK) - ((int)-(diff>>32));
            result[rstart--] = (int)diff;
        }

        value = result;
        intLen = resultLen;
        offset = value.length - resultLen;
        normalize();
        return sign;
    }


    private int difference(MutableBigInteger b) {
        MutableBigInteger a = this;
        int sign = a.compare(b);
        if (sign ==0)
            return 0;
        if (sign < 0) {
            MutableBigInteger tmp = a;
            a = b;
            b = tmp;
        }

        long diff = 0;
        int x = a.intLen;
        int y = b.intLen;


        while (y>0) {
            x--; y--;
            diff = (a.value[a.offset+ x] & LONG_MASK) -
                    (b.value[b.offset+ y] & LONG_MASK) - ((int)-(diff>>32));
            a.value[a.offset+x] = (int)diff;
        }

        while (x>0) {
            x--;
            diff = (a.value[a.offset+ x] & LONG_MASK) - ((int)-(diff>>32));
            a.value[a.offset+x] = (int)diff;
        }

        a.normalize();
        return sign;
    }


    void multiply(MutableBigInteger y, MutableBigInteger z) {
        int xLen = intLen;
        int yLen = y.intLen;
        int newLen = xLen + yLen;


        if (z.value.length < newLen)
            z.value = new int[newLen];
        z.offset = 0;
        z.intLen = newLen;


        long carry = 0;
        for (int j=yLen-1, k=yLen+xLen-1; j >= 0; j--, k--) {
            long product = (y.value[j+y.offset] & LONG_MASK) *
                    (value[xLen-1+offset] & LONG_MASK) + carry;
            z.value[k] = (int)product;
            carry = product >>> 32;
        }
        z.value[xLen-1] = (int)carry;


        for (int i = xLen-2; i >= 0; i--) {
            carry = 0;
            for (int j=yLen-1, k=yLen+i; j >= 0; j--, k--) {
                long product = (y.value[j+y.offset] & LONG_MASK) *
                        (value[i+offset] & LONG_MASK) +
                        (z.value[k] & LONG_MASK) + carry;
                z.value[k] = (int)product;
                carry = product >>> 32;
            }
            z.value[i] = (int)carry;
        }


        z.normalize();
    }


    void mul(int y, MutableBigInteger z) {
        if (y == 1) {
            z.copyValue(this);
            return;
        }

        if (y == 0) {
            z.clear();
            return;
        }

        long ylong = y & LONG_MASK;
        int[] zval = (z.value.length<intLen+1 ? new int[intLen + 1]
                : z.value);
        long carry = 0;
        for (int i = intLen-1; i >= 0; i--) {
            long product = ylong * (value[i+offset] & LONG_MASK) + carry;
            zval[i+1] = (int)product;
            carry = product >>> 32;
        }

        if (carry == 0) {
            z.offset = 1;
            z.intLen = intLen;
        } else {
            z.offset = 0;
            z.intLen = intLen + 1;
            zval[0] = (int)carry;
        }
        z.value = zval;
    }

    int divideOneWord(int divisor, MutableBigInteger quotient) {
        long divisorLong = divisor & LONG_MASK;


        if (intLen == 1) {
            long dividendValue = value[offset] & LONG_MASK;
            int q = (int) (dividendValue / divisorLong);
            int r = (int) (dividendValue - q * divisorLong);
            quotient.value[0] = q;
            quotient.intLen = (q == 0) ? 0 : 1;
            quotient.offset = 0;
            return r;
        }

        if (quotient.value.length < intLen)
            quotient.value = new int[intLen];
        quotient.offset = 0;
        quotient.intLen = intLen;


        int shift = Integer.numberOfLeadingZeros(divisor);

        int rem = value[offset];
        long remLong = rem & LONG_MASK;
        if (remLong < divisorLong) {
            quotient.value[0] = 0;
        } else {
            quotient.value[0] = (int)(remLong / divisorLong);
            rem = (int) (remLong - (quotient.value[0] * divisorLong));
            remLong = rem & LONG_MASK;
        }

        int xlen = intLen;
        int[] qWord = new int[2];
        while (--xlen > 0) {
            long dividendEstimate = (remLong<<32) |
                    (value[offset + intLen - xlen] & LONG_MASK);
            if (dividendEstimate >= 0) {
                qWord[0] = (int) (dividendEstimate / divisorLong);
                qWord[1] = (int) (dividendEstimate - qWord[0] * divisorLong);
            } else {
                divWord(qWord, dividendEstimate, divisor);
            }
            quotient.value[intLen - xlen] = qWord[0];
            rem = qWord[1];
            remLong = rem & LONG_MASK;
        }

        quotient.normalize();

        if (shift > 0)
            return rem % divisor;
        else
            return rem;
    }


    MutableBigInteger divide(MutableBigInteger b, MutableBigInteger quotient) {
        if (b.intLen == 0)
            throw new ArithmeticException("BigInteger divide by zero");

        if (intLen == 0) {
            quotient.intLen = quotient.offset;
            return new MutableBigInteger();
        }

        int cmp = compare(b);

        if (cmp < 0) {
            quotient.intLen = quotient.offset = 0;
            return new MutableBigInteger(this);
        }

        if (cmp == 0) {
            quotient.value[0] = quotient.intLen = 1;
            quotient.offset = 0;
            return new MutableBigInteger();
        }

        quotient.clear();

        if (b.intLen == 1) {
            int r = divideOneWord(b.value[b.offset], quotient);
            if (r == 0)
                return new MutableBigInteger();
            return new MutableBigInteger(r);
        }


        int[] div = Arrays.copyOfRange(b.value, b.offset, b.offset + b.intLen);
        return divideMagnitude(div, quotient);
    }

    private MutableBigInteger divideMagnitude(int[] divisor,
                                              MutableBigInteger quotient) {


        MutableBigInteger rem = new MutableBigInteger(new int[intLen + 1]);
        System.arraycopy(value, offset, rem.value, 1, intLen);
        rem.intLen = intLen;
        rem.offset = 1;

        int nlen = rem.intLen;


        int dlen = divisor.length;
        int limit = nlen - dlen + 1;
        if (quotient.value.length < limit) {
            quotient.value = new int[limit];
            quotient.offset = 0;
        }
        quotient.intLen = limit;
        int[] q = quotient.value;


        int shift = Integer.numberOfLeadingZeros(divisor[0]);
        if (shift > 0) {

            MyBigInteger.primitiveLeftShift(divisor, dlen, shift);

            rem.leftShift(shift);
        }


        if (rem.intLen == nlen) {
            rem.offset = 0;
            rem.value[0] = 0;
            rem.intLen++;
        }

        int dh = divisor[0];
        long dhLong = dh & LONG_MASK;
        int dl = divisor[1];
        int[] qWord = new int[2];


        for(int j=0; j<limit; j++) {

            int qhat = 0;
            int qrem = 0;
            boolean skipCorrection = false;
            int nh = rem.value[j+rem.offset];
            int nh2 = nh + 0x80000000;
            int nm = rem.value[j+1+rem.offset];

            if (nh == dh) {
                qhat = ~0;
                qrem = nh + nm;
                skipCorrection = qrem + 0x80000000 < nh2;
            } else {
                long nChunk = (((long)nh) << 32) | (nm & LONG_MASK);
                if (nChunk >= 0) {
                    qhat = (int) (nChunk / dhLong);
                    qrem = (int) (nChunk - (qhat * dhLong));
                } else {
                    divWord(qWord, nChunk, dh);
                    qhat = qWord[0];
                    qrem = qWord[1];
                }
            }

            if (qhat == 0)
                continue;

            if (!skipCorrection) {
                long nl = rem.value[j+2+rem.offset] & LONG_MASK;
                long rs = ((qrem & LONG_MASK) << 32) | nl;
                long estProduct = (dl & LONG_MASK) * (qhat & LONG_MASK);

                if (unsignedLongCompare(estProduct, rs)) {
                    qhat--;
                    qrem = (int)((qrem & LONG_MASK) + dhLong);
                    if ((qrem & LONG_MASK) >=  dhLong) {
                        estProduct -= (dl & LONG_MASK);
                        rs = ((qrem & LONG_MASK) << 32) | nl;
                        if (unsignedLongCompare(estProduct, rs))
                            qhat--;
                    }
                }
            }

            rem.value[j+rem.offset] = 0;
            int borrow = mulsub(rem.value, divisor, qhat, dlen, j+rem.offset);

            if (borrow + 0x80000000 > nh2) {

                divadd(divisor, rem.value, j+1+rem.offset);
                qhat--;
            }


            q[j] = qhat;
        }

        if (shift > 0)
            rem.rightShift(shift);

        quotient.normalize();
        rem.normalize();
        return rem;
    }


    private boolean unsignedLongCompare(long one, long two) {
        return (one+Long.MIN_VALUE) > (two+Long.MIN_VALUE);
    }


    private void divWord(int[] result, long n, int d) {
        long dLong = d & LONG_MASK;

        if (dLong == 1) {
            result[0] = (int)n;
            result[1] = 0;
            return;
        }

        long q = (n >>> 1) / (dLong >>> 1);
        long r = n - q*dLong;

        while (r < 0) {
            r += dLong;
            q--;
        }
        while (r >= dLong) {
            r -= dLong;
            q++;
        }

        result[0] = (int)q;
        result[1] = (int)r;
    }

    MutableBigInteger hybridGCD(MutableBigInteger b) {

        MutableBigInteger a = this;
        MutableBigInteger q = new MutableBigInteger();

        while (b.intLen != 0) {
            if (Math.abs(a.intLen - b.intLen) < 2)
                return a.binaryGCD(b);

            MutableBigInteger r = a.divide(b, q);
            a = b;
            b = r;
        }
        return a;
    }

    private MutableBigInteger binaryGCD(MutableBigInteger v) {

        MutableBigInteger u = this;
        MutableBigInteger r = new MutableBigInteger();


        int s1 = u.getLowestSetBit();
        int s2 = v.getLowestSetBit();
        int k = (s1 < s2) ? s1 : s2;
        if (k != 0) {
            u.rightShift(k);
            v.rightShift(k);
        }


        boolean uOdd = (k==s1);
        MutableBigInteger t = uOdd ? v: u;
        int tsign = uOdd ? -1 : 1;

        int lb;
        while ((lb = t.getLowestSetBit()) >= 0) {

            t.rightShift(lb);

            if (tsign > 0)
                u = t;
            else
                v = t;


            if (u.intLen < 2 && v.intLen < 2) {
                int x = u.value[u.offset];
                int y = v.value[v.offset];
                x  = binaryGcd(x, y);
                r.value[0] = x;
                r.intLen = 1;
                r.offset = 0;
                if (k > 0)
                    r.leftShift(k);
                return r;
            }

            if ((tsign = u.difference(v)) == 0)
                break;
            t = (tsign >= 0) ? u : v;
        }

        if (k > 0)
            u.leftShift(k);
        return u;
    }


    static int binaryGcd(int a, int b) {
        if (b==0)
            return a;
        if (a==0)
            return b;

        int aZeros = Integer.numberOfTrailingZeros(a);
        int bZeros = Integer.numberOfTrailingZeros(b);
        a >>>= aZeros;
        b >>>= bZeros;

        int t = (aZeros < bZeros ? aZeros : bZeros);

        while (a != b) {
            if ((a+0x80000000) > (b+0x80000000)) {
                a -= b;
                a >>>= Integer.numberOfTrailingZeros(a);
            } else {
                b -= a;
                b >>>= Integer.numberOfTrailingZeros(b);
            }
        }
        return a<<t;
    }

    MutableBigInteger mutableModInverse(MutableBigInteger p) {

        if (p.isOdd())
            return modInverse(p);

        if (isEven())
            throw new ArithmeticException("BigInteger not invertible.");

        int powersOf2 = p.getLowestSetBit();

        MutableBigInteger oddMod = new MutableBigInteger(p);
        oddMod.rightShift(powersOf2);

        if (oddMod.isOne())
            return modInverseMP2(powersOf2);

        MutableBigInteger oddPart = modInverse(oddMod);

        MutableBigInteger evenPart = modInverseMP2(powersOf2);

        MutableBigInteger y1 = modInverseBP2(oddMod, powersOf2);
        MutableBigInteger y2 = oddMod.modInverseMP2(powersOf2);

        MutableBigInteger temp1 = new MutableBigInteger();
        MutableBigInteger temp2 = new MutableBigInteger();
        MutableBigInteger result = new MutableBigInteger();

        oddPart.leftShift(powersOf2);
        oddPart.multiply(y1, result);

        evenPart.multiply(oddMod, temp1);
        temp1.multiply(y2, temp2);

        result.add(temp2);
        return result.divide(p, temp1);
    }

    MutableBigInteger modInverseMP2(int k) {
        if (isEven())
            throw new ArithmeticException("Non-invertible. (GCD != 1)");

        if (k > 64)
            return euclidModInverse(k);

        int t = inverseMod32(value[offset+intLen-1]);

        if (k < 33) {
            t = (k == 32 ? t : t & ((1 << k) - 1));
            return new MutableBigInteger(t);
        }

        long pLong = (value[offset+intLen-1] & LONG_MASK);
        if (intLen > 1)
            pLong |=  ((long)value[offset+intLen-2] << 32);
        long tLong = t & LONG_MASK;
        tLong = tLong * (2 - pLong * tLong);  // 1 more Newton iter step
        tLong = (k == 64 ? tLong : tLong & ((1L << k) - 1));

        MutableBigInteger result = new MutableBigInteger(new int[2]);
        result.value[0] = (int)(tLong >>> 32);
        result.value[1] = (int)tLong;
        result.intLen = 2;
        result.normalize();
        return result;
    }

    static int inverseMod32(int val) {

        int t = val;
        t *= 2 - val*t;
        t *= 2 - val*t;
        t *= 2 - val*t;
        t *= 2 - val*t;
        return t;
    }


    static MutableBigInteger modInverseBP2(MutableBigInteger mod, int k) {
        return fixup(new MutableBigInteger(1), new MutableBigInteger(mod), k);
    }


    private MutableBigInteger modInverse(MutableBigInteger mod) {
        MutableBigInteger p = new MutableBigInteger(mod);
        MutableBigInteger f = new MutableBigInteger(this);
        MutableBigInteger g = new MutableBigInteger(p);
        SignedMutableBigInteger c = new SignedMutableBigInteger(1);
        SignedMutableBigInteger d = new SignedMutableBigInteger();
        MutableBigInteger temp = null;
        SignedMutableBigInteger sTemp = null;

        int k = 0;

        if (f.isEven()) {
            int trailingZeros = f.getLowestSetBit();
            f.rightShift(trailingZeros);
            d.leftShift(trailingZeros);
            k = trailingZeros;
        }


        while(!f.isOne()) {

            if (f.isZero())
                throw new ArithmeticException("BigInteger not invertible.");


            if (f.compare(g) < 0) {
                temp = f; f = g; g = temp;
                sTemp = d; d = c; c = sTemp;
            }


            if (((f.value[f.offset + f.intLen - 1] ^
                    g.value[g.offset + g.intLen - 1]) & 3) == 0) {
                f.subtract(g);
                c.signedSubtract(d);
            } else {
                f.add(g);
                c.signedAdd(d);
            }


            int trailingZeros = f.getLowestSetBit();
            f.rightShift(trailingZeros);
            d.leftShift(trailingZeros);
            k += trailingZeros;
        }

        while (c.sign < 0)
            c.signedAdd(p);

        return fixup(c, p, k);
    }


    static MutableBigInteger fixup(MutableBigInteger c, MutableBigInteger p,
                                   int k) {
        MutableBigInteger temp = new MutableBigInteger();
        int r = -inverseMod32(p.value[p.offset+p.intLen-1]);

        for(int i=0, numWords = k >> 5; i<numWords; i++) {
            int  v = r * c.value[c.offset + c.intLen-1];
            p.mul(v, temp);
            c.add(temp);
            c.intLen--;
        }
        int numBits = k & 0x1f;
        if (numBits != 0) {
            int v = r * c.value[c.offset + c.intLen-1];
            v &= ((1<<numBits) - 1);
            p.mul(v, temp);
            c.add(temp);
            c.rightShift(numBits);
        }

        while (c.compare(p) >= 0)
            c.subtract(p);

        return c;
    }


    MutableBigInteger euclidModInverse(int k) {
        MutableBigInteger b = new MutableBigInteger(1);
        b.leftShift(k);
        MutableBigInteger mod = new MutableBigInteger(b);

        MutableBigInteger a = new MutableBigInteger(this);
        MutableBigInteger q = new MutableBigInteger();
        MutableBigInteger r = b.divide(a, q);

        MutableBigInteger swapper = b;
        b = r;
        r = swapper;

        MutableBigInteger t1 = new MutableBigInteger(q);
        MutableBigInteger t0 = new MutableBigInteger(1);
        MutableBigInteger temp = new MutableBigInteger();

        while (!b.isOne()) {
            r = a.divide(b, q);

            if (r.intLen == 0)
                throw new ArithmeticException("BigInteger not invertible.");

            swapper = r;
            a = swapper;

            if (q.intLen == 1)
                t1.mul(q.value[q.offset], temp);
            else
                q.multiply(t1, temp);
            swapper = q;
            q = temp;
            temp = swapper;
            t0.add(q);

            if (a.isOne())
                return t0;

            r = b.divide(a, q);

            if (r.intLen == 0)
                throw new ArithmeticException("BigInteger not invertible.");

            swapper = b;
            b =  r;

            if (q.intLen == 1)
                t0.mul(q.value[q.offset], temp);
            else
                q.multiply(t0, temp);
            swapper = q; q = temp; temp = swapper;

            t1.add(q);
        }
        mod.subtract(t1);
        return mod;
    }

}