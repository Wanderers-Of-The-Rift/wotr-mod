package com.wanderersoftherift.wotr.item;

import com.wanderersoftherift.wotr.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BuilderGlasses extends Item {
    public BuilderGlasses() {
        super(new Properties().setId(ModItems.BUILDER_GLASSES.getKey()).equippable(EquipmentSlot.HEAD).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (level.isClientSide() || player == null || !player.isCrouching()
                || !level.getBlockState(clickedPos).is(Blocks.STRUCTURE_BLOCK)) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        // temporary while im too lazy to make a proper data component
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("structurePos", new int[] { clickedPos.getX(), clickedPos.getY(), clickedPos.getZ() });
        tag.putString("structureDim", level.dimension().location().toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        player.displayClientMessage(Component.literal("Structure linked!"), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        if (!stack.is(ModItems.BUILDER_GLASSES.get()) || !stack.has(DataComponents.CUSTOM_DATA)) {
            return;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        CompoundTag tag = data.copyTag();
        if (!tag.contains("structureDim") || !tag.contains("structurePos")) {
            return;
        }
        String dim = tag.getString("structureDim");
        int[] pos = tag.getIntArray("structurePos");
        tooltipComponents.add(Component.literal("Structure: " + dim + " " + pos[0] + " " + pos[1] + " " + pos[2]));
    }
}
