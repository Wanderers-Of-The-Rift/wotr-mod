package com.wanderersoftherift.wotr.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Defines a primary statistic
 * <p>
 * Primary Statistics are an attribute that modifies other attributes based on its level. This is defined in terms of
 * ranges, but at runtime is converted into the effect that the stat has at each possible value. PrimaryStatistics are
 * defined in data, so may be tweaked or changed by the mod pack.
 * </p>
 * <p>
 * To support the possibility of change between each run of minecraft (due to updating the mod pack) the effects
 * produced by primary statistics are temporary - they will be reapplied on log in.
 * </p>
 */
public final class PrimaryStatistic {
    public static final Codec<PrimaryStatistic> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(PrimaryStatistic::attribute),
            StatisticRangeDefinition.CODEC.listOf().fieldOf("effects").forGetter(PrimaryStatistic::effects)
    ).apply(instance, PrimaryStatistic::new));
    public static final Codec<Holder<PrimaryStatistic>> CODEC = RegistryFixedCodec
            .create(WotrRegistries.Keys.PRIMARY_STATISTICS);

    private final ResourceLocation id;
    private final Holder<Attribute> primaryAttribute;
    private final List<StatisticRangeDefinition> effects;

    private final StatLevel[] levels;

    public PrimaryStatistic(Holder<Attribute> attribute, List<StatisticRangeDefinition> effects) {
        this.id = ResourceLocation.parse(attribute.getRegisteredName());
        this.primaryAttribute = attribute;
        this.effects = effects;

        // Capture all attribute + operation combinations involved in this stat
        Set<AttributeModifierType> allModifierTypes = effects.stream()
                .flatMap(x -> x.modifiers.stream())
                .map(x -> new AttributeModifierType(id, x.attribute, x.operation))
                .collect(Collectors.toUnmodifiableSet());
        int maxLevel = effects.stream().mapToInt(x -> x.from + x.count - 1).max().orElse(0);

        // Determine level increments.
        var incrementsPerLevel = IntStream.range(0, maxLevel + 1).map(level ->
            effects.stream()
                    .filter(effect -> effect.from >= level && effect.from + effect.count < level)
                    .flatmap(effect -> effect.modifiers.stream()).toList()
        ).toList();

        // Accumulate final per level effects
        levels = new StatLevel[maxLevel + 1];
        levels[0] = StatLevel.create(id, incrementsPerLevel.getFirst(), allModifierTypes);
        for (int level = 1; level <= maxLevel; level++) {
            levels[level] = StatLevel.create(id, levels[level - 1], incrementsPerLevel.get(level));
        }
    }

    public static Component displayName(Holder<PrimaryStatistic> stat) {
        ResourceLocation statId = ResourceLocation.parse(stat.value().primaryAttribute.getRegisteredName());
        return Component.translatable(statId.toLanguageKey("attribute"));
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

    /**
     * @return The attribute that tracks this statistic
     */
    public Holder<Attribute> attribute() {
        return primaryAttribute;
    }

    public List<StatisticRangeDefinition> effects() {
        return effects;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PrimaryStatistic other) {
            return attribute().equals(other.attribute());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryAttribute);
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
     * @param from
     * @param count
     * @param cost
     * @param modifiers
     */
    public record StatisticRangeDefinition(int from, int count, int cost, List<StatisticModifier> modifiers) {
        public static final Codec<StatisticRangeDefinition> CODEC = RecordCodecBuilder
                .create(instance -> instance.group(
                        Codec.INT.fieldOf("from").forGetter(StatisticRangeDefinition::from),
                        Codec.INT.optionalFieldOf("count", 1).forGetter(StatisticRangeDefinition::count),
                        Codec.INT.optionalFieldOf("cost", 1).forGetter(StatisticRangeDefinition::cost),
                        StatisticModifier.CODEC.listOf()
                                .fieldOf("modifiers")
                                .forGetter(StatisticRangeDefinition::modifiers)
                ).apply(instance, StatisticRangeDefinition::new));
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
