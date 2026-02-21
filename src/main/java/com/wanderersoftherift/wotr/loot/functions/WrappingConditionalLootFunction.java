package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Supplier;

public class WrappingConditionalLootFunction extends LootItemConditionalFunction {
    private static final Codec<LootItemFunction> LOOT_FUNCTION_CODEC = BuiltInRegistries.LOOT_FUNCTION_TYPE
            .byNameCodec()
            .dispatch(LootItemFunction::getType, LootItemFunctionType::codec);
    public static final LootItemFunctionType<WrappingConditionalLootFunction> TYPE = new LootItemFunctionType<>(
            RecordCodecBuilder.mapCodec(
                    instance -> instance.group(
                            LootItemCondition.DIRECT_CODEC.listOf()
                                    .optionalFieldOf("conditions", List.of())
                                    .forGetter(WrappingConditionalLootFunction::getConditions),
                            LOOT_FUNCTION_CODEC.fieldOf("base_function")
                                    .forGetter(WrappingConditionalLootFunction::getBase)
                    ).apply(instance, WrappingConditionalLootFunction::new)
            ));

    private final LootItemFunction base;

    protected WrappingConditionalLootFunction(List<LootItemCondition> predicates, LootItemFunction base) {
        super(predicates);
        this.base = base;
    }

    public static LootItemFunction.Builder wrappingBuilder(Supplier<LootItemFunction> o) {
        return simpleBuilder((conditions) -> new WrappingConditionalLootFunction(conditions, o.get()));
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return TYPE;
    }

    public List<LootItemCondition> getConditions() {
        return predicates;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        return base.apply(itemStack, lootContext);
    }

    public LootItemFunction getBase() {
        return base;
    }
}
