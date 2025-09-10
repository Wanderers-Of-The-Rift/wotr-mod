package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.entity.npc.MobInteraction;
import com.wanderersoftherift.wotr.entity.npc.NoInteract;
import com.wanderersoftherift.wotr.entity.npc.QuestGiverInteract;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrMobInteractions {

    public static final DeferredRegister<MapCodec<? extends MobInteraction>> MOB_INTERACTIONS = DeferredRegister
            .create(WotrRegistries.Keys.MOB_INTERACTIONS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends MobInteraction>> NONE = MOB_INTERACTIONS.register("none",
            () -> NoInteract.CODEC);

    public static final Supplier<MapCodec<? extends MobInteraction>> QUEST_GIVER = MOB_INTERACTIONS
            .register("quest_giver", () -> QuestGiverInteract.CODEC);

}
