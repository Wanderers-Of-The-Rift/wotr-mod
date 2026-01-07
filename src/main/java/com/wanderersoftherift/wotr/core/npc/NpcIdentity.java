package com.wanderersoftherift.wotr.core.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.Guild;
import com.wanderersoftherift.wotr.core.npc.interaction.NoInteract;
import com.wanderersoftherift.wotr.core.npc.interaction.NpcInteraction;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.serialization.LaxRegistryCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * The identity data for an NPC. Multiple entities (blocks, mobs) can share an identity, but they are considered the
 * same for the purpose of available quests and/or trades and similar.
 * 
 * @param guild          The guild the NPC belongs to, if any
 * @param entityType     The preferred entityType for this npc
 * @param npcInteraction Interaction behavior for the NPC
 */
public record NpcIdentity(Optional<Holder<Guild>> guild, Optional<Holder<EntityType<?>>> entityType,
        NpcInteraction npcInteraction) {

    public static final ResourceKey<NpcIdentity> DEFAULT = ResourceKey.create(WotrRegistries.Keys.NPCS,
            WanderersOfTheRift.id("default"));

    public static final Codec<NpcIdentity> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Guild.CODEC.optionalFieldOf("guild").forGetter(NpcIdentity::guild),
            RegistryFixedCodec.create(BuiltInRegistries.ENTITY_TYPE.key())
                    .optionalFieldOf("entity_type")
                    .forGetter(NpcIdentity::entityType),
            NpcInteraction.DIRECT_CODEC.optionalFieldOf("interaction", NoInteract.INSTANCE)
                    .forGetter(NpcIdentity::npcInteraction)
    ).apply(instance, NpcIdentity::new));

    public static final Codec<Holder<NpcIdentity>> CODEC = LaxRegistryCodec.create(WotrRegistries.Keys.NPCS);
    public static final StreamCodec<? super RegistryFriendlyByteBuf, Holder<NpcIdentity>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(WotrRegistries.Keys.NPCS);

    /**
     * @param npc
     * @return The display name for the npc
     */
    public static Component getDisplayName(Holder<NpcIdentity> npc) {
        ResourceLocation loc = npc.getKey().location();
        return Component.translatable(loc.toLanguageKey("npc"));
    }

    /**
     * Spawns an instance of the npc as an entity
     * 
     * @param npcIdentity The npc to spawn
     * @param level       The level to spawn in
     * @param position    The position to place the npc
     * @param reason      The reason the npc is being spawned
     * @return The new npc entity, if any. Will be null if the npc does not define an entity type.
     */
    public static @Nullable Entity spawn(
            Holder<NpcIdentity> npcIdentity,
            ServerLevel level,
            BlockPos position,
            EntitySpawnReason reason) {
        NpcIdentity npc = npcIdentity.value();
        return npc.entityType.map(type -> {
            Entity entity = type.value().spawn(level, position, reason);
            if (entity != null) {
                entity.setData(WotrAttachments.NPC_IDENTITY, new NpcIdentity.Attachment(Optional.of(npcIdentity)));
                entity.setCustomName(getDisplayName(npcIdentity));
                if (entity instanceof TamableAnimal tamableAnimal) {
                    tamableAnimal.setTame(true, true);
                }
            }
            return entity;
        }).orElse(null);
    }

    /**
     * Attachment for an NPC identity
     * 
     * @param identity
     */
    public record Attachment(Optional<Holder<NpcIdentity>> identity) {
        public static final Codec<Attachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NpcIdentity.CODEC.optionalFieldOf("identity").forGetter(Attachment::identity)
        ).apply(instance, Attachment::new));
    }
}
