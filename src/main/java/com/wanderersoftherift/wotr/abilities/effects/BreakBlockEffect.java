package com.wanderersoftherift.wotr.abilities.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// TODO: trigger item ToolSource
// TODO: option to count for stat
public record BreakBlockEffect(DropMode dropMode, ToolSource asTool, boolean awardMineStat) implements AbilityEffect {

    public static final ToolSource NO_TOOL = (context) -> ItemStack.EMPTY;
    public static final ToolSource ABILITY_ITEM = AbilityContext::abilityItem;

    // spotless:off
    public static final BiMap<String, ToolSource> SIMPLE_TOOL_SOURCES = ImmutableBiMap.of(
            "none", NO_TOOL,
            "ability_item", ABILITY_ITEM
    );
    // spotless:on

    public static final MapCodec<BreakBlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DropMode.CODEC.optionalFieldOf("drops", DropMode.COLLATE).forGetter(BreakBlockEffect::dropMode),
            Codec.either(
                    Codec.STRING.xmap(SIMPLE_TOOL_SOURCES::get, val -> SIMPLE_TOOL_SOURCES.inverse().get(val)),
                    ItemStack.OPTIONAL_CODEC.xmap(FixedToolSource::new, FixedToolSource::tool))
                    .xmap(either -> either.left().orElseGet(() -> either.right().get()), source -> {
                        if (source instanceof FixedToolSource fixed) {
                            return Either.right(fixed);
                        }
                        return Either.left(source);
                    })
                    .optionalFieldOf("as_tool", NO_TOOL)
                    .forGetter(BreakBlockEffect::asTool),
            Codec.BOOL.optionalFieldOf("reward_mine_state", false).forGetter(BreakBlockEffect::awardMineStat)
    ).apply(instance, BreakBlockEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        ItemStack tool = asTool.getTool(context);
        List<ItemStack> drops = new ArrayList<>();
        targetInfo.targetBlocks().forEach(pos -> {
            BlockState blockState = context.level().getBlockState(pos);
            if (blockState.canEntityDestroy(context.level(), pos, context.caster())
                    && blockState.getBlock().defaultDestroyTime() > -1) {
                boolean canDrop = dropMode != DropMode.NONE
                        && (!blockState.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(blockState));
                boolean dropFromBreak;
                if (canDrop && dropMode == DropMode.COLLATE && !blockState.hasBlockEntity()) {
                    drops.addAll(Block.getDrops(blockState, (ServerLevel) context.level(), pos, null, context.caster(),
                            tool));
                    dropFromBreak = false;
                } else {
                    dropFromBreak = canDrop;
                }

                if (dropFromBreak) {
                    BlockEntity blockEntity;
                    if (blockState.hasBlockEntity()) {
                        blockEntity = context.level().getBlockEntity(pos);
                    } else {
                        blockEntity = null;
                    }
                    Block.dropResources(blockState, context.level(), pos, blockEntity, context.caster(), tool);
                }
                // We don't use the dropBlock option of destroyBlock because it doesn't pass the tool on to the
                // loot table.
                context.level().destroyBlock(pos, false, context.caster());
            }
        });
        ItemUtil.condense(drops)
                .forEach(itemStack -> Block.popResource(context.level(),
                        BlockPos.containing(targetInfo.source().getLocation()), itemStack));
    }

    public enum DropMode implements StringRepresentable {
        NORMAL("normal"),
        COLLATE("collate"),
        NONE("none");

        public static final StringRepresentable.StringRepresentableCodec<DropMode> CODEC = StringRepresentable
                .fromEnum(DropMode::values);

        private final String name;

        DropMode(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }

    public interface ToolSource {
        ItemStack getTool(AbilityContext context);
    }

    public record FixedToolSource(ItemStack tool) implements ToolSource {
        @Override
        public ItemStack getTool(AbilityContext context) {
            return tool;
        }
    }
}
