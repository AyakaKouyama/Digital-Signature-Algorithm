package DSA;

public class RandomNumberGenerator {

    private long last;
    private long next;

    public RandomNumberGenerator(long seed) {
        last = seed | 1;
        next = seed;
    }

    public int random(int max) {
        last ^= (last << 21);
        last ^= (last >>> 35);
        last ^= (last << 4);
        next += 123456789123456789L;
        int out = (int) ((last + next) % max);
        return Math.abs(out);
    }

    public int random() {
        last ^= (last << 21);
        last ^= (last >>> 35);
        last ^= (last << 4);
        next += 123456789123456789L;
        int out = (int) (last + next);
        return Math.abs(out);
    }

    public void nextBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; )
            for (int rnd = random(), n = Math.min(bytes.length - i, 4);
                 n-- > 0; rnd >>= 8)
                bytes[i++] = (byte) rnd;
    }

}
