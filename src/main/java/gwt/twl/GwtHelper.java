package gwt.twl;

import com.badlogic.gdx.utils.Bits;

public class GwtHelper {

    public static int getCardinality(Bits bits) {
        int cardinality = 0;
        int idx = -1;
        while ((idx = bits.nextSetBit(idx + 1)) >= 0) {
            cardinality++;
        }
        return cardinality;
    }

    // startIndex - inclusive
    // endIndex - exclusive
    public static void set(Bits bits, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            bits.set(i);
        }
    }

    public static void set(Bits bits, int bitIndex, boolean set) {
        if (set) {
            bits.set(bitIndex);
        }
        else {
            bits.clear(bitIndex);
        }
    }

    public static boolean isJavaIdentifierStartCharacter(char c) {
        if (Character.isLetter(c)) {
            return true;
        }
        if (c == '_' || c == '$') {
            return true;
        }
        return false;
    }

    public static boolean isJavaIdentifierPartCharacter(char c) {
        if (isJavaIdentifierStartCharacter(c)) {
            return true;
        }
        if (Character.isDigit(c)) {
            return true;
        }
        return false;
    }

    public static <T> T cast(Class<T> c, Object o) {
        return (T)o;
    }

    public static float[] cloneFloatArray(float[] a) {
        float[] c = new float[a.length];
        System.arraycopy(a, 0, c, 0, a.length);
        return c;
    }

    public static String[] cloneStringArray(String[] a) {
        String[] c = new String[a.length];
        System.arraycopy(a, 0, c, 0, a.length);
        return c;
    }

    public static <T> T[] cloneArray(T[] a) {
        T[] c = (T[])new Object[a.length];
        System.arraycopy(a, 0, c, 0, a.length);
        return c;
    }
}
