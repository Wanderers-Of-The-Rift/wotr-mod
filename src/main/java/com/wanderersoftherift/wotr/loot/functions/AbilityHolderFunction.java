package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.commands.AbilityCommands;
import com.wanderersoftherift.wotr.init.RegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes.ABILITY_HOLDER_FUNCTION;

public class AbilityHolderFunction extends LootItemConditionalFunction {
    public static final MapCodec<AbilityHolderFunction> CODEC = RecordCodecBuilder.mapCodec(
            inst -> commonFields(inst).and(Codec.INT.fieldOf("min_level").forGetter(AbilityHolderFunction::getMinLevel))
                    .and(Codec.INT.fieldOf("max_level").forGetter(AbilityHolderFunction::getMaxLevel))
                    .and(RegistryCodecs.homogeneousList(RegistryEvents.ABILITY_REGISTRY)
                            .fieldOf("abilities")
                            .forGetter(AbilityHolderFunction::getAbilities))
                    .apply(inst, AbilityHolderFunction::new));

    private final int minLevel;
    private final int maxLevel;
    private final HolderSet<AbstractAbility> abilities;

    protected AbilityHolderFunction(List<LootItemCondition> predicates, int minLevel, int maxLevel,
            HolderSet<AbstractAbility> abilities) {
        super(predicates);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.abilities = abilities;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public HolderSet<AbstractAbility> getAbilities() {
        return abilities;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return ABILITY_HOLDER_FUNCTION.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        RandomSource random = lootContext.getRandom();
        Optional<Holder<AbstractAbility>> randomElement = abilities.getRandomElement(random);
        if (randomElement.isEmpty()) {
            return itemStack;
        }
        int choices = random.nextInt(maxLevel - minLevel + 1) + minLevel;
        return AbilityCommands.generateAbilityItem(itemStack, randomElement.get(), choices, lootContext.getLevel());
    }

    public static Builder<?> setAbilityOptions(int minLevel, int maxLevel, HolderSet<AbstractAbility> abilities) {
        return simpleBuilder(
                (lootItemConditions) -> new AbilityHolderFunction(lootItemConditions, minLevel, maxLevel, abilities));
    }
}
