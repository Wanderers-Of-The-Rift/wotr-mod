package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.init.WotrBlockEntities;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entity for NpcBlocks to track the associated NpcIdentity
 */
public class NpcBlockEntity extends BlockEntity implements Nameable {

    private static final Component DEFAULT_NAME = Component
            .translatable(WanderersOfTheRift.translationId("block", "npc"));

    public NpcBlockEntity(BlockPos pos, BlockState blockState) {
        super(WotrBlockEntities.NPC_BLOCK_ENTITY.get(), pos, blockState);
    }

    public @Nullable Holder<NpcIdentity> getNpcIdentity() {
        return components().get(WotrDataComponentType.NPC_IDENTITY.get());
    }

    @Override
    public @NotNull Component getName() {
        Holder<NpcIdentity> identity = getNpcIdentity();
        if (identity != null) {
            return NpcIdentity.getDisplayName(identity);
        }
        return DEFAULT_NAME;
    }

    @Override
    public @Nullable Component getCustomName() {
        Holder<NpcIdentity> identity = getNpcIdentity();
        if (identity != null) {
            return NpcIdentity.getDisplayName(identity);
        }
        return null;
    }

    public void interact(Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull Player player) {
        Holder<NpcIdentity> npcIdentity = getNpcIdentity();
        if (npcIdentity == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        npcIdentity.value().npcInteraction().interactAsBlock(npcIdentity, serverLevel, pos, block, player);
    }
}
