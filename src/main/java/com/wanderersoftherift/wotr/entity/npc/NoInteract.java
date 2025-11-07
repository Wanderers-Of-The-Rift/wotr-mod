package com.wanderersoftherift.wotr.entity.npc;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.gui.menu.ValidatingLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * No-op mob interaction attachment
 */
public final class NoInteract implements MobInteraction {

    public static final NoInteract INSTANCE = new NoInteract();
    public static final MapCodec<NoInteract> CODEC = MapCodec.unit(INSTANCE);

    private NoInteract() {
    }

    @Override
    public MapCodec<? extends MobInteraction> getCodec() {
        return CODEC;
    }

    @Override
    public InteractionResult interactWithMob(Mob mob, Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public void interactWithBlock(
            Holder<NpcIdentity> npc,
            ServerLevel level,
            @NotNull BlockPos pos,
            @NotNull Block block,
            @NotNull Player player) {
    }

    @Override
    public void interact(Holder<NpcIdentity> npc, ValidatingLevelAccess access, ServerLevel level, Player player) {
    }
}
