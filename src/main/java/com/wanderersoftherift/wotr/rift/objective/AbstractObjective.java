package com.wanderersoftherift.wotr.rift.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.function.Function;

import static com.wanderersoftherift.wotr.init.ModOngoingObjectiveTypes.ONGOING_OBJECTIVE_TYPE_REGISTRY;

public abstract class AbstractObjective {
    public abstract MapCodec<? extends AbstractObjective> getCodec();
    public static final Codec<AbstractObjective> DIRECT_CODEC = ONGOING_OBJECTIVE_TYPE_REGISTRY.byNameCodec().dispatch(AbstractObjective::getCodec, Function.identity());

    public boolean onLivingDeath(LivingDeathEvent event, ServerLevel serverLevel, LevelRiftObjectiveData data) {
        return false;
    }
}
