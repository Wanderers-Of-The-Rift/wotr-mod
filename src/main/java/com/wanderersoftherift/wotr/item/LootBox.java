package com.wanderersoftherift.wotr.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;

public record LootBox(ResourceKey<LootTable> lootTable) implements ConsumableListener {

    public static Codec<LootBox> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(LootBox::lootTable))
            .apply(inst, LootBox::new));

    @Override
    public void onConsume(Level level, LivingEntity livingEntity, ItemStack itemStack, Consumable consumable) {
        if (level.isClientSide()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTable);
        LootParams lootparams = (new LootParams.Builder(serverLevel)).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams);
        if (!objectarraylist.isEmpty()) {
            for (ItemStack itemstack : objectarraylist) {
                DefaultDispenseItemBehavior.spawnItem(serverLevel, itemstack, 2, Direction.UP,
                        Vec3.atBottomCenterOf(livingEntity.blockPosition()).relative(Direction.UP, 1.2));
            }

            serverLevel.levelEvent(3014, livingEntity.blockPosition(), 0);
        }
        // itemStack.shrink(1);
    }
}
