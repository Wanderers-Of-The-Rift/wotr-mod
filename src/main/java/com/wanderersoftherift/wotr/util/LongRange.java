package com.wanderersoftherift.wotr.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Representation for a range
 * 
 * @param from The start of the range
 * @param to   The end of the range (exclusive)
 */
public record LongRange(long from, long to) {
    public static final Codec<LongRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("from").forGetter(LongRange::from), Codec.LONG.fieldOf("to").forGetter(LongRange::to)
    ).apply(instance, LongRange::new));

    public static final LongRange EMPTY = new LongRange(Long.MIN_VALUE, Long.MIN_VALUE);

    /**
     * @param point
     * @return whether the given point falls within the range
     */
    public boolean contains(long point) {
        return from <= point && to > point;
    }

    /**
     * @param position
     * @return the fractional position within the range, clamped between 0 and 1. If the range is 0 length then the
     *         result is always 1.
     */
    public float fractionalPosition(long position) {
        if (to == from) {
            return 1f;
        }
        return Math.clamp((float) (position - from) / (to - from), 0f, 1f);
    }

    /**
     * @param position
     * @param fractionalPart A fractional part of the position (separate to reduce floating point error)
     * @return the fractional position within the range, clamped between 0 and 1. If the range is 0 length then the
     *         result is always 1.
     */
    public float fractionalPosition(long position, float fractionalPart) {
        if (to == from) {
            return 1f;
        }
        return Math.clamp((position - from + fractionalPart) / (to - from), 0f, 1f);
    }

    /**
     * @param offset
     * @return A copy of this range but offset by the given amount
     */
    public LongRange offset(long offset) {
        return new LongRange(from + offset, to + offset);
    }
}
