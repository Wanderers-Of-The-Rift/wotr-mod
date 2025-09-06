package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestMenuHelper;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.util.HolderSetUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * MobInteraction attachment for providing Quest Giver behavior
 */
public record QuestGiverInteract(Optional<HolderSet<Quest>> quests, int choiceCount) implements MobInteraction {
    public static final MapCodec<QuestGiverInteract> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Quest.SET_CODEC.optionalFieldOf("quests").forGetter(QuestGiverInteract::quests),
            Codec.INT.optionalFieldOf("choice_count", 5).forGetter(QuestGiverInteract::choiceCount)
    ).apply(instance, QuestGiverInteract::new));

    @Override
    public MapCodec<? extends MobInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interact(Mob mob, Player player, InteractionHand hand) {
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        HolderSet<Quest> choices = quests.orElse(
                HolderSetUtil.registryToHolderSet(serverPlayer.level().registryAccess(), WotrRegistries.Keys.QUESTS));
        QuestMenuHelper.openQuestMenu(serverPlayer, mob, choices, choiceCount);
        return InteractionResult.CONSUME;
    }
}
