package com.wanderersoftherift.wotr.abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.sources.AbilitySource;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgrade;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * A serializable form of an {@link AbilityContext} - this largely deals with the caster being stored as a UUID and
 * needing to be looked up in the level
 */
public record StoredAbilityContext(UUID instanceId, Holder<Ability> ability, UUID casterId, ItemStack abilityItem,
        AbilitySource source, List<Holder<AbilityUpgrade>> upgrades, List<EnhancingModifierInstance> enhancements,
        List<ResourceLocation> conditions, PatchedDataComponentMap components) {

    public static final Codec<StoredAbilityContext> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("instance_id").forGetter(StoredAbilityContext::instanceId),
            Ability.CODEC.fieldOf("ability").forGetter(StoredAbilityContext::ability),
            UUIDUtil.CODEC.fieldOf("caster").forGetter(StoredAbilityContext::casterId),
            ItemStack.OPTIONAL_CODEC.fieldOf("ability_item").forGetter(StoredAbilityContext::abilityItem),
            AbilitySource.DIRECT_CODEC.fieldOf("slot").forGetter(StoredAbilityContext::source),
            AbilityUpgrade.REGISTRY_CODEC.listOf()
                    .optionalFieldOf("upgrades", List.of())
                    .forGetter(StoredAbilityContext::upgrades),
            EnhancingModifierInstance.CODEC.listOf()
                    .fieldOf("enhancements")
                    .forGetter(StoredAbilityContext::enhancements),
            ResourceLocation.CODEC.listOf().fieldOf("conditions").forGetter(StoredAbilityContext::conditions),
            DataComponentPatch.CODEC
                    .xmap(patch -> PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, patch),
                            PatchedDataComponentMap::asPatch)
                    .optionalFieldOf("components", new PatchedDataComponentMap(DataComponentMap.EMPTY))
                    .forGetter(StoredAbilityContext::components)
    ).apply(instance, StoredAbilityContext::new));

    public StoredAbilityContext(AbilityContext context) {
        this(context.instanceId(), context.ability(), context.caster().getUUID(), context.abilityItem(),
                context.source(), context.upgrades(), context.enhancements(), List.copyOf(context.conditions()),
                context.dataComponents().copy());
    }

    public AbilityContext toContext(LivingEntity caster, Level level, long age) {
        return new AbilityContext(instanceId, ability, caster, abilityItem, source, level, age, upgrades, enhancements,
                new HashSet<>(conditions), components);
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
