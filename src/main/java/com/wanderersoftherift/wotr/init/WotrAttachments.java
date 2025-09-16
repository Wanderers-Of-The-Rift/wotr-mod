package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityConditions;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityCooldowns;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityEnhancements;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityResourceData;
import com.wanderersoftherift.wotr.abilities.attachment.AbilitySlots;
import com.wanderersoftherift.wotr.abilities.attachment.AbilityStates;
import com.wanderersoftherift.wotr.abilities.attachment.AttachedEffects;
import com.wanderersoftherift.wotr.abilities.attachment.EffectMarkers;
import com.wanderersoftherift.wotr.abilities.attachment.OngoingAbilities;
import com.wanderersoftherift.wotr.abilities.attachment.TriggerTracker;
import com.wanderersoftherift.wotr.abilities.triggers.TickTrigger;
import com.wanderersoftherift.wotr.abilities.triggers.TriggerRegistry;
import com.wanderersoftherift.wotr.client.rift.BannedRiftList;
import com.wanderersoftherift.wotr.core.guild.currency.Wallet;
import com.wanderersoftherift.wotr.core.quest.ActiveQuests;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.core.rift.RiftEntryState;
import com.wanderersoftherift.wotr.entity.npc.MobInteraction;
import com.wanderersoftherift.wotr.entity.npc.NoInteract;
import com.wanderersoftherift.wotr.entity.player.PrimaryStatistics;
import com.wanderersoftherift.wotr.entity.portal.RiftEntrance;
import com.wanderersoftherift.wotr.init.ability.WotrTrackedAbilityTriggers;
import com.wanderersoftherift.wotr.serialization.MutableListCodec;
import com.wanderersoftherift.wotr.util.EntityAttachmentRegistry;
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
    public static final Supplier<AttachmentType<RiftEntrance>> RIFT_ENTRANCE = ATTACHMENT_TYPES.register(
            "rift_entrance", () -> AttachmentType.builder(RiftEntrance::create).serialize(RiftEntrance.CODEC).build()
    );

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
    public static final Supplier<AttachmentType<AbilityCooldowns>> ABILITY_COOLDOWNS = ATTACHMENT_TYPES.register(
            "ability_cooldowns",
            () -> AttachmentType.builder(AbilityCooldowns::new).serialize(AbilityCooldowns.getSerializer()).build()
    );
    public static final Supplier<AttachmentType<AttachedEffects>> ATTACHED_EFFECTS = ATTACHMENT_TYPES.register(
            "attached_effects",
            () -> AttachmentType.builder(AttachedEffects::new).serialize(AttachedEffects.getSerializer()).build());
    public static final Supplier<AttachmentType<EffectMarkers>> EFFECT_MARKERS = ATTACHMENT_TYPES.register(
            "effect_markers", () -> AttachmentType.builder(EffectMarkers::new).build());
    public static final Supplier<AttachmentType<AbilityResourceData>> MANA = ATTACHMENT_TYPES.register("mana",
            () -> AttachmentType.builder(AbilityResourceData::new)
                    .serialize(AbilityResourceData.getSerializer())
                    .build());
    public static final Supplier<AttachmentType<OngoingAbilities>> ONGOING_ABILITIES = ATTACHMENT_TYPES.register(
            "ongoing_abilities",
            () -> AttachmentType.builder(OngoingAbilities::new).serialize(OngoingAbilities.getSerializer()).build());
    public static final Supplier<AttachmentType<AbilityStates>> ABILITY_STATES = ATTACHMENT_TYPES.register(
            "ability_states",
            () -> AttachmentType.builder(AbilityStates::new).serialize(AbilityStates.getSerializer()).build());

    public static final Supplier<AttachmentType<? extends TriggerTracker>> TRIGGER_TRACKER = ATTACHMENT_TYPES.register(
            "trigger_tracker", () -> AttachmentType.builder(TriggerTracker::new).build());
    public static final Supplier<AttachmentType<AbilityEnhancements>> ABILITY_ENHANCEMENTS = ATTACHMENT_TYPES.register(
            "ability_enhancements", () -> AttachmentType.builder(AbilityEnhancements::new).build()
    );
    public static final Supplier<AttachmentType<? extends AbilityConditions>> ABILITY_CONDITIONS = ATTACHMENT_TYPES
            .register(
                    "ability_conditions", () -> AttachmentType.builder(AbilityConditions::new).build()
            );

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

    public static final Supplier<AttachmentType<MobInteraction>> MOB_INTERACT = ATTACHMENT_TYPES.register(
            "mob_interact",
            () -> AttachmentType.<MobInteraction>builder(() -> NoInteract.INSTANCE)
                    .serialize(MobInteraction.DIRECT_CODEC)
                    .build()
    );

    /// Player progression
    public static final Supplier<AttachmentType<PrimaryStatistics>> PRIMARY_STATISTICS = ATTACHMENT_TYPES.register(
            "primary_statistics",
            () -> AttachmentType.builder(PrimaryStatistics::new)
                    .serialize(PrimaryStatistics.getSerializer())
                    .copyOnDeath()
                    .build());

    /// Level attachments
    public static final Supplier<AttachmentType<EntityAttachmentRegistry<AttachedEffects>>> ATTACHED_EFFECT_ENTITY_REGISTRY = ATTACHMENT_TYPES
            .register(
                    "attached_effect_entity_registry",
                    () -> AttachmentType.builder(() -> new EntityAttachmentRegistry<>(ATTACHED_EFFECTS)).build()
            );

    public static final Supplier<AttachmentType<EntityAttachmentRegistry<OngoingAbilities>>> ONGOING_ABILITY_ENTITY_REGISTRY = ATTACHMENT_TYPES
            .register(
                    "ongoing_ability_entity_registry",
                    () -> AttachmentType.builder(() -> new EntityAttachmentRegistry<>(ONGOING_ABILITIES)).build()
            );

    public static final Supplier<AttachmentType<TriggerRegistry<TickTrigger>>> TICK_TRIGGER_REGISTRY = ATTACHMENT_TYPES
            .register(
                    "tick_trigger_registry",
                    () -> AttachmentType.builder(() -> new TriggerRegistry<>(WotrTrackedAbilityTriggers.TICK_TRIGGER))
                            .build()
            );
}
