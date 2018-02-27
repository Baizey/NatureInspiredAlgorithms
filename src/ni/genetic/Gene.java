package ni.genetic;

@SuppressWarnings({"WeakerAccess"})
public class Gene {
    private static final long MASK = -1L;
    private final long[] words;
    private final int length;

    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static int wordIndex(int index) {
        return index >> ADDRESS_BITS_PER_WORD;
    }

    public Gene(int size) {
        length = wordIndex(size - 1) + 1;
        words = new long[length];
    }

    public void setFalse(int index) {
        words[wordIndex(index)] &= ~(1L << index);
    }

    public void setTrue(int index) {
        words[wordIndex(index)] |= (1L << index);
    }

    public void set(int index, boolean value) {
        if (value) setTrue(index);
        else setFalse(index);
    }

    public void flip(int index) {
        words[wordIndex(index)] ^= (1L << index);
    }

    public boolean get(int index) {
        return (words[wordIndex(index)] & (1L << index)) != 0L;
    }

    public void clear() {
        for (int i = 0; i < length; i++)
            words[i] = 0L;
    }

    public int cardinality() {
        int sum = 0;
        for (long word : words)
            sum += Long.bitCount(word);
        return sum;
    }

    public void flip() {
        for (int i = 0; i < length; i++)
            words[i] = ~words[i];
    }

    public void and(Gene genes) {
        for (int i = 0; i < length; i++)
            words[i] &= genes.words[i];
    }

    public void nand(Gene genes) {
        for (int i = 0; i < length; i++)
            words[i] = ~(words[i] & genes.words[i]);
    }

    public void or(Gene genes) {
        for (int i = 0; i < length; i++)
            words[i] |= genes.words[i];
    }

    public void nor(Gene genes) {
        for (int i = 0; i < length; i++)
            words[i] = ~(words[i] | genes.words[i]);
    }

    public void xor(Gene genes) {
        for (int i = 0; i < length; i++)
            words[i] ^= genes.words[i];
    }

    public void copyFrom(Gene other) {
        System.arraycopy(other.words, 0, words, 0, length);
    }

    public void copyFrom(Gene other, int startIndex, int endIndex) {
        int start = wordIndex(startIndex);
        int end = wordIndex(endIndex - 1);
        long firstWord = MASK << startIndex;
        long lastWord = MASK >>> -endIndex;
        if (start == end) {
            words[start] = combine(other.words[start], words[start], firstWord & lastWord);
        } else {
            if(firstWord != MASK) {
                words[start] = combine(other.words[start], words[start], firstWord);
                start++;
            }
            if(lastWord != MASK) {
                words[end] = combine(other.words[end], words[end], lastWord);
                end--;
            }
            int length = end - start;
            if (length > 0) System.arraycopy(other.words, start, words, start, length);
        }
    }

    private long combine(long a, long b, long mask) {
        return (a & mask) | (b & ~mask);
    }
}