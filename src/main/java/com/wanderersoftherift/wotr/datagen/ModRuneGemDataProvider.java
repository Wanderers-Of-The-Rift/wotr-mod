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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModRuneGemDataProvider {

    public static final Map<ResourceKey<RunegemData>, RunegemData> DATA = new LinkedHashMap<>();

    public static void bootstrapRuneGems(BootstrapContext<RunegemData> context) {
        HolderGetter<Modifier> lookup = context.lookup(ModDatapackRegistries.MODIFIER_KEY);
        HolderSet.Named<Item> socketablesTag = context.lookup(Registries.ITEM).getOrThrow(ModTags.Items.SOCKETABLE);
        HolderSet.Named<Item> helmetTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_HELMET_SLOT);
        HolderSet.Named<Item> chestplateTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_CHESTPLATE_SLOT);
        HolderSet.Named<Item> leggingsTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_LEGGINGS_SLOT);
        HolderSet.Named<Item> bootsTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_BOOTS_SLOT);
        HolderSet.Named<Item> mainHandTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_MAIN_HAND_SLOT);
        HolderSet.Named<Item> offHandTag = context.lookup(Registries.ITEM)
                .getOrThrow(ModTags.Items.SOCKETABLE_OFF_HAND_SLOT);
        // Replace context.register with a separate method, that creates a temporary static DATA table
        registerRunegem(context, getRunegemResourceKey("attack_raw"), new RunegemData(getTranslatableName("attack_raw"),
                RunegemShape.PENTAGON, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                new TieredModifier(1, getModifier(lookup, "attack_heavy"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))
                ), RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("attack_cut"), new RunegemData(getTranslatableName("attack_cut"),
                RunegemShape.PENTAGON, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(2, getModifier(lookup, "attack_flat")),
                                new TieredModifier(2, getModifier(lookup, "attack_heavy"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(1, getModifier(lookup, "knockback"))
                                ))
                ), RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("attack_shaped"), new RunegemData(
                getTranslatableName("attack_shaped"), RunegemShape.PENTAGON, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(3, getModifier(lookup, "attack_flat")),
                                new TieredModifier(3, getModifier(lookup, "attack_heavy")),
                                new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(2, getModifier(lookup, "knockback"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                ), RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("attack_polished"), new RunegemData(
                getTranslatableName("attack_polished"), RunegemShape.PENTAGON, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(4, getModifier(lookup, "attack_flat")),
                                new TieredModifier(4, getModifier(lookup, "attack_heavy")),
                                new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(4, getModifier(lookup, "knockback_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(3, getModifier(lookup, "knockback"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                ), RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("attack_framed"), new RunegemData(
                getTranslatableName("attack_framed"), RunegemShape.PENTAGON, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(5, getModifier(lookup, "attack_flat")),
                                new TieredModifier(5, getModifier(lookup, "attack_heavy")),
                                new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "knockback")),
                                        new TieredModifier(5, getModifier(lookup, "knockback_heavy")),
                                        new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(4, getModifier(lookup, "knockback"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(4, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "knockback")),
                                        new TieredModifier(4, getModifier(lookup, "knockback_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(4, getModifier(lookup, "knockback")),
                                        new TieredModifier(3, getModifier(lookup, "sweeping_attack_damage"))
                                ))
                ), RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("defense_raw"), new RunegemData(
                getTranslatableName("defense_raw"), RunegemShape.CIRCLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "armor")),
                                new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("defense_cut"), new RunegemData(
                getTranslatableName("defense_cut"), RunegemShape.CIRCLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "armor")),
                                        new TieredModifier(1, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_resistance"))
                                ))),
                RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("defense_shaped"), new RunegemData(
                getTranslatableName("defense_shaped"), RunegemShape.CIRCLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                ))),
                RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("defense_polished"), new RunegemData(
                getTranslatableName("defense_polished"), RunegemShape.CIRCLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "armor")),
                                        new TieredModifier(2, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_resistance"))
                                ))),
                RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("defense_framed"), new RunegemData(
                getTranslatableName("defense_framed"), RunegemShape.CIRCLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                        )), new RunegemData.ModifierGroup(
                                offHandTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "armor")),
                                        new TieredModifier(4, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "armor_toughness")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "armor_toughness"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "armor")),
                                        new TieredModifier(4, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "armor_toughness"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "armor")),
                                        new TieredModifier(3, getModifier(lookup, "armor_heavy")),
                                        new TieredModifier(3, getModifier(lookup, "knockback_resistance"))
                                ))),
                RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("health_raw"), new RunegemData(getTranslatableName("health_raw"),
                RunegemShape.HEART, List.of(new RunegemData.ModifierGroup(
                        offHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "health_percent"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "health_flat"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "health_flat")),
                                        new TieredModifier(1, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "health_flat")),
                                        new TieredModifier(1, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(1, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "health_flat")),
                                        new TieredModifier(1, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(1, getModifier(lookup, "health_percent"))
                                ))),
                RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("health_cut"), new RunegemData(getTranslatableName("health_cut"),
                RunegemShape.HEART, List.of(new RunegemData.ModifierGroup(
                        offHandTag, List.of(
                                new TieredModifier(2, getModifier(lookup, "health_percent"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "health_flat"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "health_flat")),
                                        new TieredModifier(2, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "health_flat")),
                                        new TieredModifier(1, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(2, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "health_flat")),
                                        new TieredModifier(1, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(2, getModifier(lookup, "health_percent"))
                                ))),
                RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("health_shaped"), new RunegemData(
                getTranslatableName("health_shaped"), RunegemShape.HEART, List.of(new RunegemData.ModifierGroup(
                        offHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "absorption")),
                                new TieredModifier(3, getModifier(lookup, "health_percent"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "health_flat"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "health_flat")),
                                        new TieredModifier(3, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "health_flat")),
                                        new TieredModifier(2, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(3, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "health_flat")),
                                        new TieredModifier(2, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(3, getModifier(lookup, "health_percent"))
                                ))),
                RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("health_polished"), new RunegemData(
                getTranslatableName("health_polished"), RunegemShape.HEART, List.of(new RunegemData.ModifierGroup(
                        offHandTag, List.of(
                                new TieredModifier(3, getModifier(lookup, "absorption")),
                                new TieredModifier(4, getModifier(lookup, "health_percent"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "health_flat"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "health_flat")),
                                        new TieredModifier(4, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "health_flat")),
                                        new TieredModifier(3, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(4, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "health_flat")),
                                        new TieredModifier(3, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(4, getModifier(lookup, "health_percent"))
                                ))),
                RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("health_framed"), new RunegemData(
                getTranslatableName("health_framed"), RunegemShape.HEART, List.of(new RunegemData.ModifierGroup(
                        offHandTag, List.of(
                                new TieredModifier(4, getModifier(lookup, "absorption")),
                                new TieredModifier(5, getModifier(lookup, "health_percent"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "health_flat"))
                                )),
                        new RunegemData.ModifierGroup(
                                chestplateTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "health_flat")),
                                        new TieredModifier(5, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "health_flat")),
                                        new TieredModifier(4, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(5, getModifier(lookup, "health_percent"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "health_flat")),
                                        new TieredModifier(4, getModifier(lookup, "health_hybrid")),
                                        new TieredModifier(5, getModifier(lookup, "health_percent"))
                                ))),
                RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("magic_raw"),
                new RunegemData(getTranslatableName("magic_raw"), RunegemShape.SQUARE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "ability_aoe")),
                                        new TieredModifier(1, getModifier(lookup, "mana_cost")),
                                        new TieredModifier(1, getModifier(lookup, "mana_regen_rate")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_spread"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("magic_cut"),
                new RunegemData(getTranslatableName("magic_cut"), RunegemShape.SQUARE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "ability_aoe")),
                                        new TieredModifier(2, getModifier(lookup, "mana_cost")),
                                        new TieredModifier(2, getModifier(lookup, "mana_regen_rate")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_spread"))
                                ))),
                        RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("magic_shaped"),
                new RunegemData(getTranslatableName("magic_shaped"), RunegemShape.SQUARE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "ability_aoe")),
                                        new TieredModifier(3, getModifier(lookup, "mana_cost")),
                                        new TieredModifier(3, getModifier(lookup, "mana_regen_rate")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_spread"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("magic_polished"),
                new RunegemData(getTranslatableName("magic_polished"), RunegemShape.SQUARE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "ability_aoe")),
                                        new TieredModifier(4, getModifier(lookup, "mana_cost")),
                                        new TieredModifier(4, getModifier(lookup, "mana_regen_rate")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_spread"))
                                ))),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("magic_framed"),
                new RunegemData(getTranslatableName("magic_framed"), RunegemShape.SQUARE,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "ability_aoe")),
                                        new TieredModifier(5, getModifier(lookup, "mana_cost")),
                                        new TieredModifier(5, getModifier(lookup, "mana_regen_rate")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_spread"))
                                ))),
                        RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("projectile_raw"),
                new RunegemData(getTranslatableName("projectile_raw"), RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("projectile_cut"),
                new RunegemData(getTranslatableName("projectile_cut"), RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("projectile_shaped"),
                new RunegemData(getTranslatableName("projectile_shaped"), RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("projectile_polished"),
                new RunegemData(getTranslatableName("projectile_polished"), RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("projectile_framed"),
                new RunegemData(getTranslatableName("projectile_framed"), RunegemShape.PENTAGON,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("skeleton_raw"),
                new RunegemData(getTranslatableName("skeleton_raw"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("skeleton_cut"),
                new RunegemData(getTranslatableName("skeleton_cut"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("skeleton_shaped"),
                new RunegemData(getTranslatableName("skeleton_shaped"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(3, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("skeleton_polished"),
                new RunegemData(getTranslatableName("skeleton_polished"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(1, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(4, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("skeleton_framed"),
                new RunegemData(getTranslatableName("skeleton_framed"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "projectile_pierce")),
                                        new TieredModifier(2, getModifier(lookup, "projectile_count")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_speed")),
                                        new TieredModifier(5, getModifier(lookup, "projectile_spread"))
                                ))
                        ), RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("speed_raw"), new RunegemData(getTranslatableName("speed_raw"),
                RunegemShape.TRIANGLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "attack_speed"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "jump_height"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "jump_height"))
                                ))),
                RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("speed_cut"), new RunegemData(getTranslatableName("speed_cut"),
                RunegemShape.TRIANGLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(1, getModifier(lookup, "attack_speed"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "jump_height"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(1, getModifier(lookup, "jump_height"))
                                ))),
                RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("speed_shaped"), new RunegemData(
                getTranslatableName("speed_shaped"), RunegemShape.TRIANGLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(2, getModifier(lookup, "attack_speed"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(1, getModifier(lookup, "movement_speed"))
                                ))),
                RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("speed_polished"), new RunegemData(
                getTranslatableName("speed_polished"), RunegemShape.TRIANGLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(3, getModifier(lookup, "attack_speed"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(2, getModifier(lookup, "jump_height")),
                                        new TieredModifier(2, getModifier(lookup, "movement_speed"))
                                ))),
                RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("speed_framed"), new RunegemData(
                getTranslatableName("speed_framed"), RunegemShape.TRIANGLE, List.of(new RunegemData.ModifierGroup(
                        mainHandTag, List.of(
                                new TieredModifier(4, getModifier(lookup, "attack_speed"))
                        )), new RunegemData.ModifierGroup(
                                helmetTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "attack_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                leggingsTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(3, getModifier(lookup, "jump_height")),
                                        new TieredModifier(3, getModifier(lookup, "movement_speed"))
                                )),
                        new RunegemData.ModifierGroup(
                                bootsTag, List.of(
                                        new TieredModifier(5, getModifier(lookup, "attack_speed")),
                                        new TieredModifier(3, getModifier(lookup, "jump_height")),
                                        new TieredModifier(3, getModifier(lookup, "movement_speed"))
                                ))),
                RunegemTier.FRAMED));
        registerRunegem(context, getRunegemResourceKey("zombie_raw"),
                new RunegemData(getTranslatableName("zombie_raw"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(1, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(1, getModifier(lookup, "attack_percent")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                ))),
                        RunegemTier.RAW));
        registerRunegem(context, getRunegemResourceKey("zombie_cut"),
                new RunegemData(getTranslatableName("zombie_cut"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(1, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(2, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(2, getModifier(lookup, "attack_percent")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                ))),
                        RunegemTier.CUT));
        registerRunegem(context, getRunegemResourceKey("zombie_shaped"),
                new RunegemData(getTranslatableName("zombie_shaped"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(2, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(2, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(3, getModifier(lookup, "attack_percent")),
                                        new TieredModifier(1, getModifier(lookup, "knockback")),
                                        new TieredModifier(1, getModifier(lookup, "knockback_heavy"))
                                ))),
                        RunegemTier.SHAPED));
        registerRunegem(context, getRunegemResourceKey("zombie_polished"),
                new RunegemData(getTranslatableName("zombie_polished"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(3, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(3, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(4, getModifier(lookup, "attack_percent")),
                                        new TieredModifier(2, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_heavy"))
                                ))),
                        RunegemTier.POLISHED));
        registerRunegem(context, getRunegemResourceKey("zombie_framed"),
                new RunegemData(getTranslatableName("zombie_framed"), RunegemShape.DIAMOND,
                        List.of(new RunegemData.ModifierGroup(
                                socketablesTag, List.of(
                                        new TieredModifier(4, getModifier(lookup, "attack_flat")),
                                        new TieredModifier(4, getModifier(lookup, "attack_heavy")),
                                        new TieredModifier(5, getModifier(lookup, "attack_percent")),
                                        new TieredModifier(3, getModifier(lookup, "knockback")),
                                        new TieredModifier(2, getModifier(lookup, "knockback_heavy"))
                                ))),
                        RunegemTier.FRAMED));
    }

    private static void registerRunegem(
            BootstrapContext<RunegemData> context,
            ResourceKey<RunegemData> resourceKey,
            RunegemData runegemData) {
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
