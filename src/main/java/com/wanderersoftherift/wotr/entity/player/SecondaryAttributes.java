package com.wanderersoftherift.wotr.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Information on how an attribute modifies other based on its value. These are applied based on integer thresholds.
 * <p>
 * For data definition the modifiers per value for a range can be specified. At runtime this converted into the effect
 * that the attribute has at each possible integer value.
 * </p>
 * <p>
 * To support the possibility of change between each run of minecraft (due to updating the mod pack) the effects
 * produced are temporary - they will be reapplied on log in.
 * </p>
 */
public final class SecondaryAttributes {
    public static final Codec<SecondaryAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("base_modifier_id").forGetter(SecondaryAttributes::getBaseModifierId),
            ConfigurationItem.CODEC.listOf().fieldOf("configuration").forGetter(SecondaryAttributes::effects)
    ).apply(instance, SecondaryAttributes::new));

    private final ResourceLocation baseModifierId;
    private final List<ConfigurationItem> effects;

    private final StatLevel[] levels;

    public SecondaryAttributes(ResourceLocation baseModifierId, List<ConfigurationItem> effects) {
        this.baseModifierId = baseModifierId;
        this.effects = effects;

        // Capture all attribute + operation combinations involved in this stat
        Set<AttributeModifierType> allModifierTypes = effects.stream()
                .flatMap(x -> x.modifiersPerLevel.stream())
                .map(x -> new AttributeModifierType(baseModifierId, x.attribute, x.operation))
                .collect(Collectors.toUnmodifiableSet());
        int maxLevel = effects.stream().mapToInt(x -> x.fromLevel + x.range - 1).max().orElse(0);

        // Determine level increments.
        List<List<StatisticModifier>> incrementsPerLevel = IntStream.range(0, maxLevel + 1)
                .mapToObj(
                        level -> effects.stream()
                                .filter(effect -> level >= effect.fromLevel && level < effect.fromLevel + effect.range)
                                .flatMap(effect -> effect.modifiersPerLevel.stream())
                                .toList()
                )
                .toList();

        // Accumulate final per level effects
        levels = new StatLevel[maxLevel + 1];
        levels[0] = StatLevel.create(baseModifierId, incrementsPerLevel.getFirst(), allModifierTypes);
        for (int level = 1; level <= maxLevel; level++) {
            levels[level] = StatLevel.create(baseModifierId, levels[level - 1], incrementsPerLevel.get(level));
        }
    }

    /**
     * @param entity The entity to apply the stat to
     * @param value  The value of the stat
     */
    public void apply(LivingEntity entity, int value) {
        int level = Math.clamp(value, 0, levels.length - 1);
        StatLevel statLevel = levels[level];

        AttributeMap attributes = entity.getAttributes();
        statLevel.modifiers.object2DoubleEntrySet().forEach(entry -> {
            AttributeInstance instance = attributes.getInstance(entry.getKey().attribute());
            if (instance != null) {
                instance.addOrUpdateTransientModifier(
                        new AttributeModifier(entry.getKey().id(), entry.getDoubleValue(), entry.getKey().operation()));
            }
        });
        statLevel.absent.forEach(type -> {
            AttributeInstance instance = attributes.getInstance(type.attribute);
            if (instance != null) {
                instance.removeModifier(type.id);
            }
        });
    }

    public List<ConfigurationItem> effects() {
        return effects;
    }

    public ResourceLocation getBaseModifierId() {
        return baseModifierId;
    }

    /**
     * Describes the effect of the stat at a given level
     * 
     * @param modifiers The modifiers applied by this level
     * @param absent    The modifiers that should be removed at this level (e.g. if decrementing the stat)
     */
    private record StatLevel(Object2DoubleMap<AttributeModifierType> modifiers, Set<AttributeModifierType> absent) {

        public static StatLevel create(
                ResourceLocation baseId,
                List<StatisticModifier> levelModifiers,
                Set<AttributeModifierType> allAttributeTypes) {
            return create(baseId, new StatLevel(Object2DoubleMaps.emptyMap(), allAttributeTypes), levelModifiers);
        }

        public static StatLevel create(
                ResourceLocation baseId,
                StatLevel previous,
                List<StatisticModifier> levelModifiers) {
            StatLevel level = new StatLevel(new Object2DoubleArrayMap<>(previous.modifiers),
                    new ObjectArraySet<>(previous.absent));
            for (var modifier : levelModifiers) {
                var type = new AttributeModifierType(baseId, modifier.attribute, modifier.operation);
                double newValue = modifier.value + level.modifiers.getOrDefault(type, 0);
                level.modifiers.put(type, newValue);
                level.absent.remove(type);
            }
            return level;
        }

    }

    /**
     * Definition of the modifiers to apply across a single or range of levels of the stat
     * 
     * @param fromLevel
     * @param range
     * @param modifiersPerLevel
     */
    public record ConfigurationItem(int fromLevel, int range, List<StatisticModifier> modifiersPerLevel) {
        public static final Codec<ConfigurationItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("from_level").forGetter(ConfigurationItem::fromLevel),
                Codec.INT.optionalFieldOf("range", 1).forGetter(ConfigurationItem::range),
                StatisticModifier.CODEC.listOf()
                        .fieldOf("modifiers_per_level")
                        .forGetter(ConfigurationItem::modifiersPerLevel)
        ).apply(instance, ConfigurationItem::new));
    }

    /**
     * Definition of a single attribute modifier
     * 
     * @param attribute
     * @param operation
     * @param value
     */
    public record StatisticModifier(Holder<Attribute> attribute, AttributeModifier.Operation operation, double value) {
        public static final Codec<StatisticModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(StatisticModifier::attribute),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(StatisticModifier::operation),
                Codec.DOUBLE.fieldOf("value").forGetter(StatisticModifier::value)
        ).apply(instance, StatisticModifier::new));
    }

    /**
     * Definition of a type of attribute modifier - everything but the value
     */
    private static final class AttributeModifierType {
        private final Holder<Attribute> attribute;
        private final AttributeModifier.Operation operation;
        private final ResourceLocation id;

        private AttributeModifierType(ResourceLocation baseId, Holder<Attribute> attribute,
                AttributeModifier.Operation operation) {
            this.attribute = attribute;
            this.operation = operation;
            this.id = baseId.withSuffix("." + operation.getSerializedName());
        }

        public ResourceLocation id() {
            return id;
        }

        public Holder<Attribute> attribute() {
            return attribute;
        }

        public AttributeModifier.Operation operation() {
            return operation;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof AttributeModifierType other) {
                return attribute.equals(other.attribute) && operation == other.operation;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(attribute, operation);
        }
    }
}
