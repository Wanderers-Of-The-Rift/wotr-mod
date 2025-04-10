package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes.RUNEGEMS_FUNCTION;


public class RunegemsFunction extends LootItemConditionalFunction {
    public static final MapCodec<RunegemsFunction> CODEC = RecordCodecBuilder.mapCodec(
            inst -> commonFields(inst).and(
                            RunegemTier.CODEC.fieldOf("tier").forGetter(RunegemsFunction::getRunegemTier)
                    )
                    .apply(inst, RunegemsFunction::new)
    );

    private RunegemTier runegemTier;

    protected RunegemsFunction(List<LootItemCondition> predicates, RunegemTier runegemTier) {
        super(predicates);
        this.runegemTier = runegemTier;
    }

    public RunegemTier getRunegemTier() {
        return runegemTier;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return RUNEGEMS_FUNCTION.get();
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        return generateItemStack(itemStack, lootContext.getLevel(), lootContext.getRandom());
    }

    private @NotNull ItemStack generateItemStack(ItemStack itemStack, ServerLevel level, RandomSource random) {
        Optional<Holder<RunegemData>> randomRunegem = getRandomRunegem(level, runegemTier.getTagKey(), random);
        randomRunegem.ifPresent(runegemDataHolder -> itemStack.set(ModDataComponentType.RUNEGEM_DATA, runegemDataHolder.value()));
        return itemStack;
    }

    public Optional<Holder<RunegemData>> getRandomRunegem(Level level, TagKey<RunegemData> tag, RandomSource random) {
        return level.registryAccess().lookupOrThrow(ModDatapackRegistries.RUNEGEM_DATA_KEY)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(random));
    }

    public static LootItemConditionalFunction.Builder<?> setTier(RunegemTier tier) {
        return simpleBuilder((p_298130_) -> {
            return new RunegemsFunction(p_298130_, tier);
        });
    }
}
