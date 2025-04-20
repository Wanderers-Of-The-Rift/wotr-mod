package com.wanderersoftherift.wotr.item.tools;

import com.wanderersoftherift.wotr.block.SpringBlock;
import com.wanderersoftherift.wotr.block.TrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WotRTweaker extends Item {
    public WotRTweaker(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (state.getBlock() instanceof TrapBlock) {
            level.setBlockAndUpdate(pos, ((TrapBlock) state.getBlock()).getTweak());
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() instanceof SpringBlock) {
            int strength = state.getValue(SpringBlock.STRENGTH);
            strength++;
            if (strength > SpringBlock.MAX_STRENGTH) {
                strength = SpringBlock.MIN_STRENGTH;
            }

            level.setBlockAndUpdate(pos, state.setValue(SpringBlock.STRENGTH, strength));
            if (player != null) {
                message(player, Component.literal("strength = " + strength));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static void message(@NotNull Player player, Component messageComponent) {
        player.displayClientMessage(messageComponent, true);
    }
}
