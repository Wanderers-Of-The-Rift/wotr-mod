package com.wanderersoftherift.wotr.loot.functions;

import com.wanderersoftherift.wotr.core.quest.RewardProvider;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.loot.RewardInstantLoot;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.function.Supplier;

public record RewardInstantLootFunction(RewardProvider reward) implements LootItemFunction {
    public static final LootItemFunctionType<RewardInstantLootFunction> TYPE = new LootItemFunctionType<>(
            RewardProvider.DIRECT_CODEC.fieldOf("reward")
                    .xmap(RewardInstantLootFunction::new, RewardInstantLootFunction::reward)
    );

    public static LootItemFunction.Builder builder(Supplier<RewardProvider> reward) {
        return WrappingConditionalLootFunction.wrappingBuilder(() -> new RewardInstantLootFunction(reward.get()));
    }

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return TYPE;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        var instantLootComponent = new RewardInstantLoot(reward.generateReward(lootContext));
        itemStack.applyComponents(DataComponentPatch.builder()
                .set(WotrDataComponentType.INSTANT_LOOT.get(), instantLootComponent)
                .build());
        return itemStack;
    }
}
