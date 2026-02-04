package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import com.wanderersoftherift.wotr.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// TODO: trigger item ToolSource
public record BreakBlockEffect(DropMode dropMode, ToolSource asTool, boolean awardMineStat) implements AbilityEffect {

    public static final MapCodec<BreakBlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DropMode.CODEC.optionalFieldOf("drops", DropMode.COLLATE).forGetter(BreakBlockEffect::dropMode),
            Codec.either(
                    StringRepresentable.fromEnum(SimpleToolSource::values),
                    ItemStack.OPTIONAL_CODEC.xmap(FixedToolSource::new, FixedToolSource::tool))
                    .xmap(either -> either.left().isPresent() ? either.left().get() : either.right().get(), source -> {
                        if (source instanceof FixedToolSource fixed) {
                            return Either.right(fixed);
                        }
                        return Either.left((SimpleToolSource) source);
                    })
                    .optionalFieldOf("as_tool", SimpleToolSource.NONE)
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
        List<ItemStack> dropsToCondense = new ArrayList<>();
        targetInfo.targetBlocks().forEach(pos -> {
            BlockState blockState = context.level().getBlockState(pos);
            if (blockState.canEntityDestroy(context.level(), pos, context.caster())
                    && blockState.getBlock().defaultDestroyTime() > -1) {
                breakBlock(pos, blockState, tool, dropsToCondense, context);
            }
        });
        ItemUtil.condense(dropsToCondense)
                .forEach(itemStack -> Block.popResource(context.level(),
                        BlockPos.containing(targetInfo.source().getLocation()), itemStack));
    }

    private void breakBlock(
            BlockPos pos,
            BlockState blockState,
            ItemStack tool,
            List<ItemStack> drops,
            AbilityContext context) {
        boolean canDrop = dropMode != DropMode.NONE
                && (!blockState.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(blockState));
        boolean dropFromBreak = canDrop;
        if (canDrop && dropMode == DropMode.COLLATE && !blockState.hasBlockEntity()) {
            drops.addAll(Block.getDrops(blockState, (ServerLevel) context.level(), pos, null, context.caster(), tool));
            dropFromBreak = false;
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
        if (awardMineStat && context.caster() instanceof Player player) {
            player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
        }
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

    public sealed interface ToolSource {
        ItemStack getTool(AbilityContext context);
    }

    public enum SimpleToolSource implements ToolSource, StringRepresentable {
        NONE("none") {
            @Override
            public ItemStack getTool(AbilityContext context) {
                return ItemStack.EMPTY;
            }
        },
        ABILITY_ITEM("ability_item") {
            @Override
            public ItemStack getTool(AbilityContext context) {
                return context.abilityItem();
            }
        },
        HELD_ITEM("held_item") {
            @Override
            public ItemStack getTool(AbilityContext context) {
                return context.caster().getMainHandItem();
            }
        };

        private final String id;

        SimpleToolSource(String id) {
            this.id = id;
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }
    }

    public record FixedToolSource(ItemStack tool) implements ToolSource {
        @Override
        public ItemStack getTool(AbilityContext context) {
            return tool;
        }
    }
}
