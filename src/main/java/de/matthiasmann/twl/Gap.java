package de.matthiasmann.twl;

public class Gap {
    public final int min;
    public final int preferred;
    public final int max;

    public Gap() {
        this(0,0,32767);
    }
    public Gap(int size) {
        this(size, size, size);
    }
    public Gap(int min, int preferred) {
        this(min, preferred, 32767);
    }
    public Gap(int min, int preferred, int max) {
        if(min < 0) {
            throw new IllegalArgumentException("min");
        }
        if(preferred < min) {
            throw new IllegalArgumentException("preferred");
        }
        if(max < 0 || (max > 0 && max < preferred)) {
            throw new IllegalArgumentException("max");
        }
        this.min = min;
        this.preferred = preferred;
        this.max = max;
    }
}
