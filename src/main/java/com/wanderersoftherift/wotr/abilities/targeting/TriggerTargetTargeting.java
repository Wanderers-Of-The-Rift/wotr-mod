package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.attachment.TargetComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * Targets the target provided by the trigger activating the ability, if any
 */
public final class TriggerTargetTargeting implements AbilityTargeting {

    public static final TriggerTargetTargeting INSTANCE = new TriggerTargetTargeting();
    public static final MapCodec<TriggerTargetTargeting> CODEC = MapCodec.unit(INSTANCE);

    private TriggerTargetTargeting() {
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        TargetComponent triggerSource = context.get(WotrDataComponentType.AbilityContextData.TRIGGER_TARGET);
        if (triggerSource == null) {
            return List.of();
        }
        HitResult target = triggerSource.asHitResult((ServerLevel) context.level());
        return List.of(new TargetInfo(origin.source(), List.of(target)));
    }
}
