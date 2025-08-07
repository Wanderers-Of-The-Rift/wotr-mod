package com.wanderersoftherift.wotr.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Representation for a range of time
 * 
 * @param from  The start of the range
 * @param until The end of the range (exclusive)
 */
public record TimeRange(long from, long until) {
    public static final Codec<TimeRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("from").forGetter(TimeRange::from),
            Codec.LONG.fieldOf("until").forGetter(TimeRange::until)
    ).apply(instance, TimeRange::new));

    public static final TimeRange NONE = new TimeRange(Long.MIN_VALUE, Long.MIN_VALUE);

    /**
     * @param time
     * @return whether the given time falls within the range
     */
    public boolean inRange(long time) {
        return from <= time && until > time;
    }

    /**
     * @param time
     * @return the fractional position of time within the range, clamped between 0 and 1. If the time range is 0 length
     *         then the position is always 1.
     */
    public float fractionalPosition(long time) {
        if (until == from) {
            return 1f;
        }
        return Math.clamp((float) (time - from) / (until - from), 0f, 1f);
    }

    /**
     * @param offset
     * @return A copy of this time range but offset by the given amount
     */
    public TimeRange offset(long offset) {
        return new TimeRange(from + offset, until + offset);
    }
}
