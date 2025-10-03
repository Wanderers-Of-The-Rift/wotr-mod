package com.wanderersoftherift.wotr.core.rift.parameter;

import com.wanderersoftherift.wotr.core.rift.parameter.definitions.RiftParameter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record RiftParameterModifier(HolderSet<RiftParameter> applicableParameters,
        AttributeModifier.Operation operation, double value) {

    public boolean isApplicable(Holder<RiftParameter> parameter) {
        return applicableParameters.contains(parameter);
    }

    public void enable(RiftParameterInstance parameterInstance) {
        switch (operation) {
            case ADD_VALUE -> {
                parameterInstance.addBase(value);
            }
            case ADD_MULTIPLIED_BASE -> {
                parameterInstance.addAccumulatedMultiplier(value);
            }
            case ADD_MULTIPLIED_TOTAL -> {
                parameterInstance.multiplyTotal(value);
            }
        }
    }

    public void disable(RiftParameterInstance parameterInstance) {
        switch (operation) {
            case ADD_VALUE -> {
                parameterInstance.addBase(-value);
            }
            case ADD_MULTIPLIED_BASE -> {
                parameterInstance.addAccumulatedMultiplier(-value);
            }
            case ADD_MULTIPLIED_TOTAL -> {
                parameterInstance.multiplyTotal(1.0 / value);
            }
        }
    }
}
