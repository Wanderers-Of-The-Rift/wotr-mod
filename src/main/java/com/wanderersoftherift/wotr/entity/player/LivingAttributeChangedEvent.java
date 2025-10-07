package com.wanderersoftherift.wotr.entity.player;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

/**
 * Event for when an attribute of a player changes
 */
public class LivingAttributeChangedEvent extends LivingEvent {
    private final Holder<Attribute> attribute;

    public LivingAttributeChangedEvent(LivingEntity player, Holder<Attribute> attribute) {
        super(player);
        this.attribute = attribute;
    }

    /**
     * @return The attribute that has changed
     */
    public Holder<Attribute> getAttribute() {
        return attribute;
    }
}