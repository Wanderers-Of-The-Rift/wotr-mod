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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModRuneGemDataProvider {
    public static void bootstrapRuneGems(BootstrapContext<RunegemData> context) {
        HolderGetter<Modifier> lookup = context.lookup(ModDatapackRegistries.MODIFIER_KEY);
        HolderSet.Named<Item> socketablesTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE);
        HolderSet.Named<Item> helmetTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_HELMET_SLOT);
        HolderSet.Named<Item> chestplateTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_CHESTPLATE_SLOT);
        HolderSet.Named<Item> leggingsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_LEGGINGS_SLOT);
        HolderSet.Named<Item> bootsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_BOOTS_SLOT);
        HolderSet.Named<Item> mainHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_MAIN_HAND_SLOT);
        HolderSet.Named<Item> offHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_OFF_HAND_SLOT);
        context.register(getRunegemResourceKey("cut_attack"),
                new RunegemData(getTranslatableName("cut_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                ))),
                        RunegemTier.CUT));
        context.register(getRunegemResourceKey("cut_defense"),
                new RunegemData(getTranslatableName("cut_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.CUT));
        context.register(getRunegemResourceKey("cut_health"),
                new RunegemData(getTranslatableName("cut_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_health")),
                                        new TieredModifier(1, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.CUT));
        context.register(getRunegemResourceKey("cut_speed"),
                new RunegemData(getTranslatableName("cut_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "attack_speed"))
                                ))),
                        RunegemTier.CUT));
        context.register(getRunegemResourceKey("cut_zombie"),
                new RunegemData(getTranslatableName("cut_zombie"),
                        RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(2, getModifier(lookup, "percent_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))),
                        RunegemTier.CUT));
        context.register(getRunegemResourceKey("framed_attack"),
                new RunegemData(getTranslatableName("framed_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                ))),
                        RunegemTier.FRAMED));
        context.register(getRunegemResourceKey("framed_defense"),
                new RunegemData(getTranslatableName("framed_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                ))),
                        RunegemTier.FRAMED));
        context.register(getRunegemResourceKey("framed_health"),
                new RunegemData(getTranslatableName("framed_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "health")),
                                        new TieredModifier(2, getModifier(lookup, "absorption"))
                                ))),
                        RunegemTier.FRAMED));
        context.register(getRunegemResourceKey("framed_speed"),
                new RunegemData(getTranslatableName("framed_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(4, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.FRAMED));
        context.register(getRunegemResourceKey("framed_zombie"),
                new RunegemData(getTranslatableName("framed_zombie"),
                        RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(4, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(5, getModifier(lookup, "percent_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(3, getModifier(lookup, "knockback"))
                                ))),
                        RunegemTier.FRAMED));
        context.register(getRunegemResourceKey("polished_attack"),
                new RunegemData(getTranslatableName("polished_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                ))),
                        RunegemTier.POLISHED));
        context.register(getRunegemResourceKey("polished_defense"),
                new RunegemData(getTranslatableName("polished_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                ))),
                        RunegemTier.POLISHED));
        context.register(getRunegemResourceKey("polished_health"),
                new RunegemData(getTranslatableName("polished_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "health")),
                                        new TieredModifier(1, getModifier(lookup, "absorption"))
                                ))),
                        RunegemTier.POLISHED));
        context.register(getRunegemResourceKey("polished_speed"),
                new RunegemData(getTranslatableName("polished_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(3, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.POLISHED));
        context.register(getRunegemResourceKey("polished_zombie"),
                new RunegemData(getTranslatableName("polished_zombie"),
                        RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(3, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(4, getModifier(lookup, "percent_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(2, getModifier(lookup, "knockback"))
                                ))),
                        RunegemTier.POLISHED));
        context.register(getRunegemResourceKey("raw_attack"),
                new RunegemData(getTranslatableName("raw_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                ))),
                        RunegemTier.RAW));
        context.register(getRunegemResourceKey("raw_defense"),
                new RunegemData(getTranslatableName("raw_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.RAW));
        context.register(getRunegemResourceKey("raw_health"),
                new RunegemData(getTranslatableName("raw_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "health"))
                                ))),
                        RunegemTier.RAW));
        context.register(getRunegemResourceKey("raw_speed"),
                new RunegemData(getTranslatableName("raw_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                ))),
                        RunegemTier.RAW));
        context.register(getRunegemResourceKey("raw_zombie"),
                new RunegemData(getTranslatableName("raw_zombie"),
                        RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "percent_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))),
                        RunegemTier.RAW));
        context.register(getRunegemResourceKey("shaped_attack"),
                new RunegemData(getTranslatableName("shaped_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                ))),
                        RunegemTier.SHAPED));
        context.register(getRunegemResourceKey("shaped_defense"),
                new RunegemData(getTranslatableName("shaped_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.SHAPED));
        context.register(getRunegemResourceKey("shaped_health"),
                new RunegemData(getTranslatableName("shaped_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "health")),
                                        new TieredModifier(1, getModifier(lookup, "absorption"))
                                ))),
                        RunegemTier.SHAPED));
        context.register(getRunegemResourceKey("shaped_speed"),
                new RunegemData(getTranslatableName("shaped_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.SHAPED));
        context.register(getRunegemResourceKey("shaped_zombie"),
                new RunegemData(getTranslatableName("shaped_zombie"),
                        RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(3, getModifier(lookup, "percent_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))),
                        RunegemTier.SHAPED));
    }

    private static @NotNull MutableComponent getTranslatableName(String id) {
        return Component.translatable("runegem." + WanderersOfTheRift.MODID + "." + id);
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
