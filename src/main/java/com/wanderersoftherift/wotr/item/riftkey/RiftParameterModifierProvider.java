package com.wanderersoftherift.wotr.item.riftkey;

import com.wanderersoftherift.wotr.core.rift.parameter.RiftParameterModifier;

import java.util.stream.Stream;

public interface RiftParameterModifierProvider {

    Stream<RiftParameterModifier> getModifiers();
}
