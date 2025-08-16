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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class PrimaryStatistic {
    public static final Codec<PrimaryStatistic> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(PrimaryStatistic::attribute),
            StatisticRangeDefinition.CODEC.listOf().fieldOf("effects").forGetter(PrimaryStatistic::effects)
    ).apply(instance, PrimaryStatistic::new));

    private final ResourceLocation id;
    private final Holder<Attribute> primaryAttribute;
    private final List<StatisticRangeDefinition> effects;

    private final StatLevel[] levels;

    public PrimaryStatistic(Holder<Attribute> attribute, List<StatisticRangeDefinition> effects) {
        this.id = ResourceLocation.parse(attribute.getRegisteredName());
        this.primaryAttribute = attribute;
        this.effects = effects;

        Set<AttributeModifierType> allModifierTypes = effects.stream()
                .flatMap(x -> x.modifiers.stream())
                .map(x -> new AttributeModifierType(id, x.attribute, x.operation))
                .collect(Collectors.toUnmodifiableSet());
        int maxLevel = effects.stream().mapToInt(x -> x.from + x.count - 1).max().orElse(0);

        List<List<StatisticModifier>> perLevel = new ArrayList<>();
        for (int i = 0; i <= maxLevel; i++) {
            perLevel.add(new ArrayList<>());
        }
        for (var effect : effects) {
            for (int level = effect.from; level < effect.from + effect.count; level++) {
                perLevel.get(level).addAll(effect.modifiers);
            }
        }

        levels = new StatLevel[maxLevel + 1];
        levels[0] = StatLevel.create(id, perLevel.getFirst(), allModifierTypes);
        for (int level = 1; level <= maxLevel; level++) {
            levels[level] = StatLevel.create(id, levels[level - 1], perLevel.get(level));
        }

    }

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

    public Holder<Attribute> attribute() {
        return primaryAttribute;
    }

    public List<StatisticRangeDefinition> effects() {
        return effects;
    }

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

    public record StatisticModifier(Holder<Attribute> attribute, AttributeModifier.Operation operation, double value) {
        public static final Codec<StatisticModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(StatisticModifier::attribute),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(StatisticModifier::operation),
                Codec.DOUBLE.fieldOf("value").forGetter(StatisticModifier::value)
        ).apply(instance, StatisticModifier::new));
    }

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

        @Override
        public String toString() {
            return "AttributeModifierType[" + "attribute=" + attribute + ", " + "operation=" + operation + ']';
        }

    }
}
