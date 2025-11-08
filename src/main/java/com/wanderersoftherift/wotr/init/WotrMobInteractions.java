package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.MerchantInteract;
import com.wanderersoftherift.wotr.core.npc.NoInteract;
import com.wanderersoftherift.wotr.core.npc.NpcInteraction;
import com.wanderersoftherift.wotr.core.npc.QuestGiverInteract;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrMobInteractions {

    public static final DeferredRegister<MapCodec<? extends NpcInteraction>> MOB_INTERACTIONS = DeferredRegister
            .create(WotrRegistries.Keys.MOB_INTERACTIONS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends NpcInteraction>> NONE = MOB_INTERACTIONS.register("none",
            () -> NoInteract.CODEC);

    public static final Supplier<MapCodec<? extends NpcInteraction>> QUEST_GIVER = MOB_INTERACTIONS
            .register("quest_giver", () -> QuestGiverInteract.CODEC);

    public static final Supplier<MapCodec<? extends NpcInteraction>> MERCHANT = MOB_INTERACTIONS.register("merchant",
            () -> MerchantInteract.CODEC);

}
