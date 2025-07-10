package com.wanderersoftherift.wotr.init.guild;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.GoalDefinition;
import com.wanderersoftherift.wotr.core.guild.quest.GoalType;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.core.guild.quest.goal.KillMobGoal;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrGoalTypes {

    public static final DeferredRegister<GoalType<?>> GOAL_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<MapCodec<? extends GoalDefinition>> GOAL_DEFINITION_TYPES = DeferredRegister
            .create(WotrRegistries.Keys.GOAL_DEFINITION_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<GoalType<?>> GIVE_ITEM = register("give_item", () -> GiveItemGoal.TYPE);

    public static final Supplier<GoalType<?>> KILL_MOB = register("kill_mob", () -> KillMobGoal.TYPE);

    private static Supplier<GoalType<?>> register(String id, Supplier<GoalType<?>> typeSupplier) {
        GOAL_DEFINITION_TYPES.register(id, () -> typeSupplier.get().codec());
        return GOAL_TYPES.register(id, typeSupplier);
    }
}
