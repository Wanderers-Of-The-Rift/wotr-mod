package com.wanderersoftherift.wotr.item;

import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.entity.projectile.ThrownExitPearl;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExitPearlItem extends Item {
    public static final float PROJECTILE_SHOOT_POWER = 1.5F;

    public ExitPearlItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverlevel) {
            if (!RiftData.isRift(serverlevel)) {
                player.displayClientMessage(Component.literal("You can only use this item in a rift!"), true);
                return InteractionResult.FAIL;
            }
            Projectile.spawnProjectileFromRotation(ThrownExitPearl::new, serverlevel, itemstack, player, 0.0F,
                    PROJECTILE_SHOOT_POWER, 1.0F);
        }
        level.playSound(
                null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

}
