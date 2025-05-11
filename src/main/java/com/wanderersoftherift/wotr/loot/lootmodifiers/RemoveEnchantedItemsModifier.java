package com.wanderersoftherift.wotr.loot.lootmodifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class RemoveEnchantedItemsModifier extends LootModifier {

    public static final MapCodec<RemoveEnchantedItemsModifier> CODEC = RecordCodecBuilder.mapCodec(
            inst -> LootModifier.codecStart(inst).apply(inst, RemoveEnchantedItemsModifier::new));

    protected RemoveEnchantedItemsModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context) {

        // Check if the current level is a rift level and return
        // note this should never be the case, as the conditions from glm should prevent it
        ServerLevel serverlevel = context.getLevel();
        if (RiftData.isRift(serverlevel)) {
            return generatedLoot;
        }

        ObjectArrayList<ItemStack> overrideLoot = new ObjectArrayList<>();
        for (ItemStack itemStack : generatedLoot) {
            if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
                // if item is enchanted book, remove from returned loot
                continue;
            }

            if (itemStack.isEnchanted()) {
                // if item is enchanted, remove all enchantments
                EnchantmentHelper.setEnchantments(itemStack, ItemEnchantments.EMPTY); // Remove all enchantments
            }

            overrideLoot.add(itemStack);
        }
        return overrideLoot; // Return the modified loot
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}