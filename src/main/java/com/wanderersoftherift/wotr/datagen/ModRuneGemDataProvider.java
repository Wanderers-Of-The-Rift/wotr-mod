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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModRuneGemDataProvider {

    public static Map<ResourceKey<RunegemData>, RunegemData> DATA = new LinkedHashMap<>();

    public static void bootstrapRuneGems(BootstrapContext<RunegemData> context) {
        HolderGetter<Modifier> lookup = context.lookup(ModDatapackRegistries.MODIFIER_KEY);
        HolderSet.Named<Item> socketablesTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE);
        HolderSet.Named<Item> helmetTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_HELMET_SLOT);
        HolderSet.Named<Item> chestplateTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_CHESTPLATE_SLOT);
        HolderSet.Named<Item> leggingsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_LEGGINGS_SLOT);
        HolderSet.Named<Item> bootsTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_BOOTS_SLOT);
        HolderSet.Named<Item> mainHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_MAIN_HAND_SLOT);
        HolderSet.Named<Item> offHandTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE_OFF_HAND_SLOT);
        //Replace context.register with a separate method, that creates a temporary static DATA table
        registerRunegem(context, getRunegemResourceKey("cut_attack"),
                new RunegemData(getTranslatableName("cut_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack"))
                                )),new RunegemData.ModifierGroup(
                                        offHandTag,
                                        List.of(
                                                new TieredModifier(2, getModifier(lookup, "knockback")),
                                                new TieredModifier(2, getModifier(lookup, "heavy_knockback"))
                                        )),new RunegemData.ModifierGroup(
                                        helmetTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback"))
                                        )),new RunegemData.ModifierGroup(
                                        chestplateTag,
                                        List.of(
                                                new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),new RunegemData.ModifierGroup(
                                        leggingsTag,
                                        List.of(
                                                new TieredModifier(2, getModifier(lookup, "knockback")),
                                                new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                        )),new RunegemData.ModifierGroup(
                                        bootsTag,
                                        List.of(
                                                new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                                new TieredModifier(1, getModifier(lookup, "knockback"))
                                        ))
                                ),
                        RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("cut_defense"),
                new RunegemData(getTranslatableName("cut_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.CUT));

        registerRunegem(context, getRunegemResourceKey("cut_health"),
                new RunegemData(getTranslatableName("cut_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_health"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_health")),
                                        new TieredModifier(2, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_health")),
                                        new TieredModifier(2, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "hybrid_health"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_health")),
                                        new TieredModifier(2, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("cut_speed"),
                new RunegemData(getTranslatableName("cut_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "attack_speed"))
                                ))),
                        RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("cut_zombie"),
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
        registerRunegem(context, getRunegemResourceKey("framed_attack"),
                new RunegemData(getTranslatableName("framed_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(5, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "knockback")),
                                        new TieredModifier(5, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(4, getModifier(lookup, "knockback"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(4, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "knockback")),
                                        new TieredModifier(4, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                        ),
                        RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("framed_defense"),
                new RunegemData(getTranslatableName("framed_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance")),
                                        new TieredModifier(2, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("framed_health"),
                new RunegemData(getTranslatableName("framed_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "percent_health")),
                                        new TieredModifier(4, getModifier(lookup, "absorption"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_health"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_health")),
                                        new TieredModifier(5, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_health")),
                                        new TieredModifier(5, getModifier(lookup, "percent_health")),
                                        new TieredModifier(4, getModifier(lookup, "hybrid_health"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "flat_health")),
                                        new TieredModifier(5, getModifier(lookup, "percent_health")),
                                        new TieredModifier(4, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("framed_speed"),
                new RunegemData(getTranslatableName("framed_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(5, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "jump_height")),
                                        new TieredModifier(4, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(3, getModifier(lookup, "movement_speed"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "jump_height")),
                                        new TieredModifier(5, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(3, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("framed_zombie"),
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
        registerRunegem(context, getRunegemResourceKey("polished_attack"),
                new RunegemData(getTranslatableName("polished_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(4, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(4, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(3, getModifier(lookup, "knockback"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(3, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                        ),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("polished_defense"),
                new RunegemData(getTranslatableName("polished_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("polished_health"),
                new RunegemData(getTranslatableName("polished_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "percent_health")),
                                        new TieredModifier(3, getModifier(lookup, "absorption"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_health"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_health")),
                                        new TieredModifier(4, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_health")),
                                        new TieredModifier(4, getModifier(lookup, "percent_health")),
                                        new TieredModifier(3, getModifier(lookup, "hybrid_health"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "flat_health")),
                                        new TieredModifier(4, getModifier(lookup, "percent_health")),
                                        new TieredModifier(3, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("polished_speed"),
                new RunegemData(getTranslatableName("polished_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(3, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(4, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("polished_zombie"),
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
        registerRunegem(context, getRunegemResourceKey("raw_attack"),
                new RunegemData(getTranslatableName("raw_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_attack"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))
                        ),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("raw_defense"),
                new RunegemData(getTranslatableName("raw_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("raw_health"),
                new RunegemData(getTranslatableName("raw_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_health"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_health")),
                                        new TieredModifier(1, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_health")),
                                        new TieredModifier(1, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "hybrid_health"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "flat_health")),
                                        new TieredModifier(1, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("raw_speed"),
                new RunegemData(getTranslatableName("raw_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(1, getModifier(lookup, "jump_height")),
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("raw_zombie"),
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
        registerRunegem(context, getRunegemResourceKey("shaped_attack"),
                new RunegemData(getTranslatableName("shaped_attack"),
                        RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(3, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "heavy_knockback")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "knockback"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "heavy_attack")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "heavy_knockback"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_attack")),
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                        ),
                        RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("shaped_defense"),
                new RunegemData(getTranslatableName("shaped_defense"),
                        RunegemShape.CIRCLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                ))),
                        RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("shaped_health"),
                new RunegemData(getTranslatableName("shaped_health"),
                        RunegemShape.HEART,
                        List.of(new RunegemData.ModifierGroup(
                                offHandTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "percent_health")),
                                        new TieredModifier(1, getModifier(lookup, "absorption"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_health"))
                                )),new RunegemData.ModifierGroup(
                                chestplateTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_health")),
                                        new TieredModifier(3, getModifier(lookup, "percent_health"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_health")),
                                        new TieredModifier(3, getModifier(lookup, "percent_health")),
                                        new TieredModifier(2, getModifier(lookup, "hybrid_health"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "flat_health")),
                                        new TieredModifier(3, getModifier(lookup, "percent_health")),
                                        new TieredModifier(2, getModifier(lookup, "hybrid_health"))
                                ))),
                        RunegemTier.SHAPED));

        registerRunegem(context, getRunegemResourceKey("shaped_speed"),
                new RunegemData(getTranslatableName("shaped_speed"),
                        RunegemShape.TRIANGLE,
                        List.of(new RunegemData.ModifierGroup(
                                mainHandTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                helmetTag,
                                List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_speed"))
                                )),new RunegemData.ModifierGroup(
                                leggingsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                )),new RunegemData.ModifierGroup(
                                bootsTag,
                                List.of(
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(3, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "movement_speed"))
                                ))),
                        RunegemTier.SHAPED));

        registerRunegem(context, getRunegemResourceKey("shaped_zombie"),
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

    private static void registerRunegem(BootstrapContext<RunegemData> context, ResourceKey<RunegemData> resourceKey, RunegemData runegemData) {
        DATA.put(resourceKey, runegemData);
        context.register(resourceKey, runegemData);
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
