package natural.genericGA.binaryGA;

import java.util.stream.IntStream;

@SuppressWarnings({"WeakerAccess"})
public class Dna {
    private static final long MASK = -1L;
    private final long[] words;
    private final int bits;
    private final int length;

    private static final int ADDRESS_BITS_PER_WORD = 6;

    private static int wordIndex(int index) {
        return index >> ADDRESS_BITS_PER_WORD;
    }

    public Dna(int size) {
        bits = size;
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

    public int get(int startBit, int endBit) {
        int startWord = wordIndex(startBit);
        int endWord = wordIndex(endBit - 1);
        long firstWord = MASK << startBit;
        long lastWord = MASK >>> -endBit;
        if (startWord == endWord)
            return (int) ((words[startWord] & firstWord & lastWord) >>> startBit);
        else
            return (int) (((firstWord & words[startWord]) >>> startBit) + ((words[endWord] & lastWord) << startBit));
    }

    /**
     * Optimized version of getUsedNodes(startBit, endBit)
     * Requires start and end bit to be inside the same word for correct result (otherwise unexpected results will be returned)
     *
     * @param startBit
     * @param endBit
     * @return
     */
    public int getUnsafe(int startBit, int endBit) {
        return (int) ((words[wordIndex(startBit)] & (MASK << startBit) & (MASK >>> -endBit)) >>> startBit);
    }

    public void clear() {
        for (var i = 0; i < length; i++)
            words[i] = 0L;
    }

    public int cardinality() {
        var sum = 0;
        for (var word : words)
            sum += Long.bitCount(word);
        return sum;
    }

    public void flip() {
        for (var i = 0; i < length; i++)
            words[i] = ~words[i];
    }

    public void and(Dna genes) {
        for (var i = 0; i < length; i++)
            words[i] &= genes.words[i];
    }

    public void nand(Dna genes) {
        for (var i = 0; i < length; i++)
            words[i] = ~(words[i] & genes.words[i]);
    }

    public void or(Dna genes) {
        for (var i = 0; i < length; i++)
            words[i] |= genes.words[i];
    }

    public void nor(Dna genes) {
        for (var i = 0; i < length; i++)
            words[i] = ~(words[i] | genes.words[i]);
    }

    public void xor(Dna genes) {
        for (var i = 0; i < length; i++)
            words[i] ^= genes.words[i];
    }

    public void copyFrom(Dna other) {
        System.arraycopy(other.words, 0, words, 0, length);
    }

    public void copyFrom(Dna other, int startIndex, int endIndex) {
        int start = wordIndex(startIndex);
        int end = wordIndex(endIndex - 1);
        long firstWord = MASK << startIndex;
        long lastWord = MASK >>> -endIndex;
        if (start == end) {
            words[start] = combine(other.words[start], words[start], firstWord & lastWord);
        } else {
            if (firstWord != MASK) {
                words[start] = combine(other.words[start], words[start], firstWord);
                start++;
            }
            if (lastWord != MASK) {
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

    public boolean[] toArray() {
        boolean[] arr = new boolean[length * ADDRESS_BITS_PER_WORD];
        for (int i = 0; i < length; i++) {
            int start = i * ADDRESS_BITS_PER_WORD;
            long bit = 1L;
            for (int j = 0; j < ADDRESS_BITS_PER_WORD; j++, bit <<= 1)
                arr[start + j] = (bit & words[i]) != 0;
        }
        return arr;
    }

    private static long[] maskes = IntStream.range(0, 64).mapToLong(i -> 1L << i).toArray();

    public int leadingOnes() {
        var leading = 0;
        for (var word : words)
            if (word == -1L)
                leading += 64;
            else for (var i = 0; i < maskes.length; i++)
                if ((word & maskes[i]) == 0L)
                    return leading + i;
        return leading;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length - 1; i++)
            sb.append(longToString(words[i], 64)).append(" ");
        int lastLength = bits % 64;
        if (lastLength == 0 && bits != 0) lastLength = 64;
        return sb.append(longToString(words[length - 1], lastLength)).toString();
    }

    private String longToString(long value, int minLength) {
        StringBuilder sb = new StringBuilder(Long.toBinaryString(value));
        sb.reverse();
        if (sb.length() > minLength) sb = new StringBuilder(sb.substring(0, minLength));
        while (sb.length() < minLength) sb.append("0");
        return sb.toString();
    }

    public int size() {
        return bits;
    }
}
