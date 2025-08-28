package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.quest.QuestMenuHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/**
 * MobInteraction attachment for providing Quest Giver behavior
 */
public final class QuestGiverInteract implements MobInteraction {
    public static final QuestGiverInteract INSTANCE = new QuestGiverInteract();
    public static final MapCodec<QuestGiverInteract> CODEC = MapCodec.unit(INSTANCE);

    private QuestGiverInteract() {
    }

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

        QuestMenuHelper.openQuestMenu(serverPlayer, mob);
        return InteractionResult.CONSUME;
    }
}
