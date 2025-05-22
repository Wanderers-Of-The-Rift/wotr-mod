package com.wanderersoftherift.wotr.util;

import java.util.ArrayList;
import java.util.List;

public class GradientMixer {
    private final float speed;
    private final List<ColorSegment> segments = new ArrayList<>();
    private float durationTotal;

    public GradientMixer(float speed) {
        this.speed = speed;
    }

    public GradientMixer add(int color, float interval) {
        this.segments.add(new ColorSegment(color, interval));
        this.durationTotal += interval;
        return this;
    }

    public int getColor(float time) {
        if (segments.isEmpty() || durationTotal == 0) {
            return 0xFFFFFF; // white fallback
        }

        float phaseTime = (time * speed) % durationTotal;

        return getBlendedColor(phaseTime);
    }

    private int getBlendedColor(float phaseTime) {
        float mixRatio = 0f;

        for (int i = 0; i < segments.size(); i++) {
            ColorSegment seg = segments.get(i);
            float nextThreshold = mixRatio + seg.duration();

            if (phaseTime < nextThreshold) {
                ColorSegment nextSeg = segments.get((i + 1) % segments.size());
                float blendFactor = (phaseTime - mixRatio) / seg.duration();
                return ColorUtil.blendColors(nextSeg.color(), seg.color(), blendFactor);
            }

            mixRatio = nextThreshold;
        }

        return 0xFFFFFF; // fallback, shouldnt be hit
    }

    private record ColorSegment(int color, float duration) {
    }
}
