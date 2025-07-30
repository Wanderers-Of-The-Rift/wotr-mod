package com.wanderersoftherift.wotr.item.tools;

import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AnomalyNeedle extends Item {
    private static final int MAX_CHARGE = 8; // max 8 charges of skill thread on needle. Maybe make this configurable

    public AnomalyNeedle(Properties properties) {
        super(properties);
    }

    public static int getCharge(ItemStack stack) {
        return stack.getOrDefault(WotrDataComponentType.ANOMALY_NEEDLE_CHARGE.get(), 0);
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.set(WotrDataComponentType.ANOMALY_NEEDLE_CHARGE.get(), Math.min(charge, MAX_CHARGE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack otherHand = player.getItemInHand(
                hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (otherHand.is(WotrItems.SKILL_THREAD.get()) && getCharge(stack) < MAX_CHARGE) {
            setCharge(stack, getCharge(stack) + 1);
            if (!player.isCreative()) {
                otherHand.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCharge(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        // 13 is the max width for the bar in vanilla
        return Math.round(13.0f * getCharge(stack) / MAX_CHARGE);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float) getCharge(stack) / MAX_CHARGE);
        int r = (int) (255 * (1.0F - f));
        int g = (int) (255 * f);
        return (r << 16) | (g << 8);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            java.util.List<net.minecraft.network.chat.Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int charge = getCharge(stack);
        tooltip.add(Component.translatable("tooltip.wotr.anomaly_needle_charge", charge, MAX_CHARGE));
    }
}