package com.wanderersoftherift.wotr.core.rift;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RiftParameterInstance {
    public static final Codec<RiftParameterInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.DOUBLE.fieldOf("base").forGetter(it -> it.baseValue),
                    Codec.DOUBLE.fieldOf("accumulated_multiplier").forGetter(it -> it.accumulatedMultiplierValue),
                    Codec.DOUBLE.fieldOf("total_multiplier").forGetter(it -> it.totalMultiplierValue)
            ).apply(instance, RiftParameterInstance::new)
    );
    private double finalValue = 0.0;
    private double baseValue = 0.0;
    private double accumulatedMultiplierValue = 1.0;
    private double totalMultiplierValue = 1.0;

    public RiftParameterInstance() {

    }

    private RiftParameterInstance(double baseValue, double accumulatedMultiplierValue, double totalMultiplierValue) {

        this.baseValue = baseValue;
        this.accumulatedMultiplierValue = accumulatedMultiplierValue;
        this.totalMultiplierValue = totalMultiplierValue;
        updateFinal();
    }

    public void addBase(double value) {
        baseValue += value;
        updateFinal();
    }

    public void addAccumulatedMultiplier(double value) {
        accumulatedMultiplierValue += value;
        updateFinal();
    }

    public void multiplyTotal(double value) {
        totalMultiplierValue *= value;
        updateFinal();
    }

    private void updateFinal() {
        finalValue = baseValue * accumulatedMultiplierValue * totalMultiplierValue;
    }

    public double get() {
        return finalValue;
    }

    public double getBase() {
        return baseValue;
    }

    public double getAccumulatedMultiplier() {
        return accumulatedMultiplierValue;
    }

    public double getTotalMultiplier() {
        return totalMultiplierValue;
    }

    public void setBase(double value) {
        baseValue = value;
        updateFinal();
    }

    public void setAccumulatedMultiplier(double value) {
        accumulatedMultiplierValue = value;
        updateFinal();
    }

    public void setTotalMultiplier(double value) {
        totalMultiplierValue = value;
        updateFinal();
    }
}
