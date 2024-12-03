package com.kaboomroads.tehshadur.util;

import java.util.Objects;

public class BitQueue {
    public long bits;

    public BitQueue() {
        this.bits = 0L;
    }

    public BitQueue(long bits) {
        this.bits = bits;
    }

    public void add(boolean bit) {
        bits <<= 1;
        if (bit) bits |= 1L;
    }

    public boolean get(byte position) {
        if (position < (byte) 0 || position >= (byte) 64)
            throw new IllegalArgumentException("Position must be between 0 and 63");
        return ((bits >>> position) & 1L) == 1L;
    }

    public void clear() {
        bits = 0L;
    }

    public String toCompactString() {
        StringBuilder result = new StringBuilder();
        byte firstBitPos = 63;
        while (firstBitPos >= 0 && !get(firstBitPos)) firstBitPos--;
        if (firstBitPos < 0) return "0 -> 64";
        boolean currentBit = get(firstBitPos);
        int count = 0;
        boolean firstGroup = true;
        for (byte i = firstBitPos; i >= 0; i--) {
            boolean bit = get(i);
            if (bit == currentBit) count++;
            else {
                if (firstGroup) {
                    result.append(currentBit ? "1" : "0").append(" -> ").append(count);
                    firstGroup = false;
                } else result.append(" | ").append(count);
                currentBit = bit;
                count = 1;
            }
        }
        if (count > 0) {
            if (firstGroup) result.append(currentBit ? "1" : "0").append(" -> ").append(count);
            else result.append(" | ").append(count);
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        for (byte b = 63; b >= 0; b--) sb.append(get(b) ? '1' : '0');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitQueue bitQueue = (BitQueue) o;
        return bits == bitQueue.bits;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bits);
    }
}