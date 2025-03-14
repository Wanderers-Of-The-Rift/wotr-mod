package com.wanderersoftherift.wotr.modifier.effect;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttributeModifierEffect extends AbstractModifierEffect {
    public static final MapCodec<AttributeModifierEffect> MODIFIER_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(AttributeModifierEffect::getId),
                    Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierEffect::getAttribute),
                    Codec.INT.fieldOf("min_roll").forGetter(AttributeModifierEffect::getMinimumRoll),
                    Codec.INT.fieldOf("max_roll").forGetter(AttributeModifierEffect::getMaximumRoll),
                    AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeModifierEffect::getOperation)
            ).apply(instance, AttributeModifierEffect::new)
    );

    @Override
    public MapCodec<? extends AbstractModifierEffect> getCodec() {
        return MODIFIER_CODEC;
    }

    private final ResourceLocation id;
    private final Holder<Attribute> attribute;
    private final int minRoll;
    private final int maxRoll;
    private final AttributeModifier.Operation operation;

    public AttributeModifierEffect(ResourceLocation id, Holder<Attribute> attribute, int minRoll, int maxRoll, AttributeModifier.Operation operation) {
        this.id = id;
        this.attribute = attribute;
        this.minRoll = minRoll;
        this.maxRoll = maxRoll;
        this.operation = operation;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Holder<Attribute> getAttribute(){
        return attribute;
    }

    public int getMinimumRoll(){
        return minRoll;
    }

    public int getMaximumRoll(){
        return maxRoll;
    }

    public AttributeModifier.Operation getOperation() {
        return this.operation;
    }

    private ResourceLocation idForSlot(StringRepresentable source) {
        return this.id.withSuffix("/" + source.getSerializedName());
    }

    public AttributeModifier getModifier(float roll, StringRepresentable source) {
        return new AttributeModifier(this.idForSlot(source), calculateModifier(roll) , this.getOperation());
    }

    public double calculateModifier(float roll) {
        return (roll * (maxRoll - minRoll)) + minRoll;
    }

    @Override
    public void enableModifier(float roll, Entity entity, ModifierSource source) {
        if (entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().addTransientAttributeModifiers(this.makeAttributeMap(roll, source));
        }
    }

    @Override
    public void disableModifier(float roll, Entity entity, ModifierSource source) {
        if (entity instanceof LivingEntity livingentity) {
            livingentity.getAttributes().removeAttributeModifiers(this.makeAttributeMap(roll, source));
        }
    }

    @Override
    public void applyModifier() {
        // NOOP
    }

    private HashMultimap<Holder<Attribute>, AttributeModifier> makeAttributeMap(float roll, ModifierSource source) {
        HashMultimap<Holder<Attribute>, AttributeModifier> hashmultimap = HashMultimap.create();
        hashmultimap.put(this.attribute, this.getModifier(roll, source));
        return hashmultimap;
    }
}
