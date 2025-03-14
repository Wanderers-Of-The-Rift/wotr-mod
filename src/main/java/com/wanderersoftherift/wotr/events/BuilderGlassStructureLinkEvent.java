package com.wanderersoftherift.wotr.events;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BuilderGlassStructureLinkEvent {
    @SubscribeEvent
    public static void onStructureLink(UseItemOnBlockEvent event) {
        if (event.getSide() != LogicalSide.SERVER ||
                event.getPlayer() == null ||
                event.getItemStack().isEmpty() ||
                !event.getPlayer().isCrouching() ||
                !event.getLevel().getBlockState(event.getPos()).is(Blocks.STRUCTURE_BLOCK) ||
                !event.getItemStack().is(ModItems.BUILDER_GLASSES.get()))
        { return;}

        event.cancelWithResult(InteractionResult.CONSUME);
        Player player = event.getPlayer();
        ItemStack stack = event.getItemStack();
        // temporary while im too lazy to make a proper data component
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("structurePos", new int[]{event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()});
        tag.putString("structureDim", event.getLevel().dimension().location().toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        player.displayClientMessage(Component.literal("Structure linked!"), true);
    }
}
