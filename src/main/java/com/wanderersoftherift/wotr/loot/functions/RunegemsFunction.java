package com.wanderersoftherift.wotr.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModDatapackRegistries;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
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

import static com.wanderersoftherift.wotr.init.ModLootItemFunctionTypes.RUNEGEMS_FUNCTION;
import static com.wanderersoftherift.wotr.init.ModTags.Runegems.RAW;


public class RunegemsFunction extends LootItemConditionalFunction {
    public static final MapCodec<RunegemsFunction> CODEC = RecordCodecBuilder.mapCodec(
            inst -> commonFields(inst)

                    .apply(inst, RunegemsFunction::new)
    );

    protected RunegemsFunction(List<LootItemCondition> predicates) {
        super(predicates);
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
        itemStack.set(ModDataComponentType.RUNEGEM_DATA, getRandomRunegem(level, RAW));
        return itemStack;
    }

    public RunegemData getRandomRunegem(Level level, TagKey<RunegemData> tag) {
        return level.registryAccess().lookupOrThrow(ModDatapackRegistries.RUNEGEM_DATA_KEY)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(level.random))
                .orElse(Holder.direct(new RunegemData (RunegemShape.CIRCLE, WanderersOfTheRift.tagId(ModDatapackRegistries.MODIFIER_KEY, "raw_attack_rune"), RunegemTier.RAW)))
                .value();
    }
}
