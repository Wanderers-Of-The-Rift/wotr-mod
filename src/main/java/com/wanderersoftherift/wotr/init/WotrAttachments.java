package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.Codec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerCooldownData;
import com.wanderersoftherift.wotr.abilities.attachment.PlayerDurationData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectDisplayData;
import com.wanderersoftherift.wotr.client.rift.BannedRiftList;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.inventory.snapshot.InventorySnapshot;
import com.wanderersoftherift.wotr.core.rift.stats.StatSnapshot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WotrAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, WanderersOfTheRift.MODID);

    /// Inventory Snapshot
    public static final Supplier<AttachmentType<InventorySnapshot>> INVENTORY_SNAPSHOT = ATTACHMENT_TYPES.register(
            "inventory_snapshot",
            () -> AttachmentType.builder(InventorySnapshot::new).serialize(InventorySnapshot.CODEC).build());
    public static final Supplier<AttachmentType<List<ItemStack>>> RESPAWN_ITEMS = ATTACHMENT_TYPES.register(
            "respawn_items",
            () -> AttachmentType.builder(() -> (List<ItemStack>) new ArrayList<ItemStack>())
                    .serialize(ItemStack.CODEC.listOf())
                    .copyOnDeath()
                    .build());

    /// Rift
    public static final Supplier<AttachmentType<Boolean>> DIED_IN_RIFT = ATTACHMENT_TYPES.register(
            "died_in_rift", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<StatSnapshot>> PRE_RIFT_STATS = ATTACHMENT_TYPES.register(
            "pre_rift_stats",
            () -> AttachmentType.builder(() -> new StatSnapshot()).serialize(StatSnapshot.CODEC).copyOnDeath().build());
    public static final Supplier<AttachmentType<BannedRiftList>> BANNED_RIFTS = ATTACHMENT_TYPES
            .register("banned_rifts", () -> AttachmentType.builder(() -> new BannedRiftList()).build());

    /// Abilities
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerCooldownData>> ABILITY_COOLDOWNS = ATTACHMENT_TYPES
            .register("cooldowns",
                    () -> AttachmentType.builder(PlayerCooldownData::new).serialize(PlayerCooldownData.CODEC).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerDurationData>> DURATIONS = ATTACHMENT_TYPES
            .register("durations",
                    () -> AttachmentType.builder(() -> new PlayerDurationData())
                            .serialize(PlayerDurationData.CODEC)
                            .build());
    public static final Supplier<AttachmentType<AbilitySlots>> ABILITY_SLOTS = ATTACHMENT_TYPES.register(
            "ability_slots",
            () -> AttachmentType.builder((holder) -> new AbilitySlots(holder, null))
                    .serialize(AbilitySlots.SERIALIZER)
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<AttachedEffectData>> ATTACHED_EFFECTS = ATTACHMENT_TYPES.register(
            "attached_effects",
            () -> AttachmentType.builder(AttachedEffectData::new).serialize(AttachedEffectData.CODEC).build());
    public static final Supplier<AttachmentType<EffectDisplayData>> EFFECT_DISPLAY = ATTACHMENT_TYPES
            .register("effect_display", () -> AttachmentType.builder(() -> new EffectDisplayData()).build());
    public static final Supplier<AttachmentType<ManaData>> MANA = ATTACHMENT_TYPES.register("mana",
            () -> AttachmentType.builder(ManaData::new).serialize(ManaData.CODEC).build());

    /// Guilds
    public static final Supplier<AttachmentType<Wallet>> WALLET = ATTACHMENT_TYPES.register("wallet",
            () -> AttachmentType.builder(Wallet::new).serialize(Wallet.CODEC).copyOnDeath().build());
    public static final Supplier<AttachmentType<ActiveQuests>> ACTIVE_QUESTS = ATTACHMENT_TYPES.register(
            "active_quests",
            () -> AttachmentType.builder(() -> new ActiveQuests()).serialize(ActiveQuests.CODEC).copyOnDeath().build());

}
