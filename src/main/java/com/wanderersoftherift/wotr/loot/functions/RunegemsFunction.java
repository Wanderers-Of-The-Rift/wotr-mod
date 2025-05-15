package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
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
            inst -> commonFields(inst).and(RegistryCodecs.homogeneousList(ModDatapackRegistries.RUNEGEM_DATA_KEY)
                    .fieldOf("runegems")
                    .forGetter(RunegemsFunction::getRunegems)).apply(inst, RunegemsFunction::new));

    private final HolderSet<RunegemData> runegems;

    protected RunegemsFunction(List<LootItemCondition> predicates, HolderSet<RunegemData> runegems) {
        super(predicates);
        this.runegems = runegems;
    }

    public HolderSet<RunegemData> getRunegems() {
        return runegems;
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
        Optional<Holder<RunegemData>> randomRunegem = getRandomRunegem(level, runegems, random);
        randomRunegem.ifPresent(
                runegemDataHolder -> itemStack.set(ModDataComponentType.RUNEGEM_DATA, runegemDataHolder.value()));
        return itemStack;
    }

    public Optional<Holder<RunegemData>> getRandomRunegem(
            Level level,
            HolderSet<RunegemData> runegems,
            RandomSource random) {
        return runegems.getRandomElement(random);
    }

    public static LootItemConditionalFunction.Builder<?> setRunegemOptions(HolderSet<RunegemData> runegems) {
        return simpleBuilder((conditions) -> {
            return new RunegemsFunction(conditions, runegems);
        });
    }
}
