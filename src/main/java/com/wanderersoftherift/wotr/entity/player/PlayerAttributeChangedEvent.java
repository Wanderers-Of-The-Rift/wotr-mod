package com.wanderersoftherift.wotr.entity.player;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerAttributeChangedEvent extends PlayerEvent {
    private final Holder<Attribute> attribute;

    public PlayerAttributeChangedEvent(Player player, Holder<Attribute> attribute) {
        super(player);
        this.attribute = attribute;
    }

    public Holder<Attribute> getAttribute() {
        return attribute;
    }
}