package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModRuneGemDataProvider {
    public static void bootstrapRuneGems(BootstrapContext<RunegemData> context) {
        HolderGetter<Modifier> lookup = context.lookup(ModDatapackRegistries.MODIFIER_KEY);
        HolderSet.Named<Item> HelmetTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_HELMET_SLOT);
        HolderSet.Named<Item> chestplateTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_CHESTPLATE_SLOT);
        HolderSet.Named<Item> leggingsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_LEGGINGS_SLOT);
        HolderSet.Named<Item> bootsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_BOOTS_SLOT);
        HolderSet.Named<Item> mainHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_MAIN_HAND_SLOT);
        HolderSet.Named<Item> offHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_OFF_HAND_SLOT);
        context.register(getRunegemResourceKey("raw_attack"),
                new RunegemData(RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                        HelmetTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),
                                new RunegemData.ModifierGroup(
                                        chestplateTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),
                                new RunegemData.ModifierGroup(
                                        leggingsTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),
                                new RunegemData.ModifierGroup(
                                        bootsTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),
                                new RunegemData.ModifierGroup(
                                        mainHandTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),
                                new RunegemData.ModifierGroup(
                                        offHandTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        ))),
                        RunegemTier.RAW));
    }

    private static @NotNull ResourceKey<RunegemData> getRunegemResourceKey(String id) {
        return ResourceKey.create(ModDatapackRegistries.RUNEGEM_DATA_KEY, WanderersOfTheRift.id(id));
    }

    private static @NotNull ResourceKey<Modifier> getModifierResourceKey(String id) {
        return ResourceKey.create(ModDatapackRegistries.MODIFIER_KEY, WanderersOfTheRift.id(id));
    }

    private static Holder<Modifier> getModifier(HolderGetter<Modifier> lookup, String id) {
        return lookup.getOrThrow(getModifierResourceKey(id));
    }
}
