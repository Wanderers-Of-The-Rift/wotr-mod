package com.wanderersoftherift.wotr.loot;

import net.minecraft.world.Container;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.Event;

public abstract class LootEvent extends Event {
    private final LootTable lootTable;
    private final LootContext lootContext;

    protected LootEvent(LootTable lootTable, LootContext lootContext) {
        this.lootTable = lootTable;
        this.lootContext = lootContext;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public LootContext getLootContext() {
        return lootContext;
    }

    public static class PlayerOpensChest extends LootEvent {

        private final Container container;

        public PlayerOpensChest(LootTable lootTable, LootContext lootContext, Container container) {
            super(lootTable, lootContext);
            this.container = container;
        }

        public Container getContainer() {
            return container;
        }
    }
}
