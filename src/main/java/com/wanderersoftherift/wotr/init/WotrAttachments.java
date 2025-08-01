package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffectData;
import com.wanderersoftherift.wotr.abilities.attachment.ManaData;
import com.wanderersoftherift.wotr.abilities.effects.marker.EffectDisplayData;
import com.wanderersoftherift.wotr.client.rift.BannedRiftList;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.serialization.MutableListCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WotrAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, WanderersOfTheRift.MODID);

    public static final Supplier<AttachmentType<List<ItemStack>>> RESPAWN_ITEMS = ATTACHMENT_TYPES.register(
            "respawn_items",
            () -> AttachmentType.builder(() -> (List<ItemStack>) new ArrayList<ItemStack>())
                    .serialize(ItemStack.CODEC.listOf())
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<List<RiftEntryState>>> RIFT_ENTRY_STATES = ATTACHMENT_TYPES.register(
            "rift_entry_states",
            () -> AttachmentType.builder(() -> (List<RiftEntryState>) new ArrayList<RiftEntryState>())
                    .serialize(MutableListCodec.of(RiftEntryState.CODEC))
                    .copyOnDeath()
                    .build());

    /// Rift
    public static final Supplier<AttachmentType<RiftEntryState>> DEATH_RIFT_ENTRY_STATE = ATTACHMENT_TYPES.register(
            "died_in_rift",
            () -> AttachmentType.builder(() -> RiftEntryState.EMPTY)
                    .serialize(RiftEntryState.CODEC)
                    .copyOnDeath()
                    .build());

    public static final Supplier<AttachmentType<RiftEntryState>> EXITED_RIFT_ENTRY_STATE = ATTACHMENT_TYPES.register(
            "exited_rift",
            () -> AttachmentType.builder(() -> RiftEntryState.EMPTY)
                    .serialize(RiftEntryState.CODEC)
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<BannedRiftList>> BANNED_RIFTS = ATTACHMENT_TYPES
            .register("banned_rifts", () -> AttachmentType.builder(() -> new BannedRiftList()).build());

    /// Abilities
    public static final Supplier<AttachmentType<AbilitySlots>> ABILITY_SLOTS = ATTACHMENT_TYPES.register(
            "ability_slots",
            () -> AttachmentType.builder(AbilitySlots::new)
                    .serialize(AbilitySlots.getSerializer())
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
            () -> AttachmentType.builder(Wallet::new).serialize(Wallet.getSerializer()).copyOnDeath().build());
    public static final Supplier<AttachmentType<List<QuestState>>> AVAILABLE_QUESTS = ATTACHMENT_TYPES.register(
            "available_quests",
            () -> AttachmentType.<List<QuestState>>builder(() -> new ArrayList<>())
                    .serialize(MutableListCodec.of(QuestState.CODEC))
                    .copyOnDeath()
                    .build());
    public static final Supplier<AttachmentType<ActiveQuests>> ACTIVE_QUESTS = ATTACHMENT_TYPES.register(
            "active_quests",
            () -> AttachmentType.builder(ActiveQuests::new)
                    .serialize(ActiveQuests.getSerializer())
                    .copyOnDeath()
                    .build());
}
