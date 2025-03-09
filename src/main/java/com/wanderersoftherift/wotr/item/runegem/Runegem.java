package com.wanderersoftherift.wotr.item.runegem;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class Runegem extends Item {
    public Runegem(Properties properties) {
        super(properties);
    }

    private Holder<Enchantment> getRandomModifier(ServerLevel serverLevel, TagKey<Enchantment> tag) {
        return serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(serverLevel.random))
                .orElse(null);
    }

}
