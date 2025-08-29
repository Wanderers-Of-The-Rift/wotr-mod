package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * A serializable form of an {@link AbilityContext} - this largely deals with the caster being stored as a UUID and
 * needing to be looked up in the level
 */
public record StoredAbilityContext(UUID instanceId, Holder<Ability> ability, UUID casterId, ItemStack abilityItem,
        AbilitySource source) {

    public static final Codec<StoredAbilityContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("instance_id").forGetter(x -> x.instanceId),
            Ability.CODEC.fieldOf("ability").forGetter(x -> x.ability),
            UUIDUtil.CODEC.fieldOf("caster").forGetter(x -> x.casterId),
            ItemStack.OPTIONAL_CODEC.fieldOf("ability_item").forGetter(x -> x.abilityItem),
            AbilitySource.DIRECT_CODEC.fieldOf("slot").forGetter(x -> x.source)
    ).apply(instance, StoredAbilityContext::new));

    public StoredAbilityContext(AbilityContext context) {
        this(context.instanceId(), context.ability(), context.caster().getUUID(), context.abilityItem(),
                context.source());
    }

    public AbilityContext toContext(LivingEntity caster, Level level) {
        return new AbilityContext(instanceId, ability, caster, abilityItem, source, level);
    }

    public LivingEntity getCaster(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(casterId);
            if (entity != null) {
                if (entity instanceof LivingEntity caster) {
                    return caster;
                }
                return null;
            }
        }
        return null;
    }

}
