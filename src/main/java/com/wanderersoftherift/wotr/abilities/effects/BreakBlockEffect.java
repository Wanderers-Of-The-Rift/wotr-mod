package com.wanderersoftherift.wotr.abilities.effects;

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

public record BreakBlockEffect(DropMode dropMode, ItemStack asTool) implements AbilityEffect {

    public static final MapCodec<BreakBlockEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DropMode.CODEC.optionalFieldOf("drops", DropMode.COLLATE).forGetter(BreakBlockEffect::dropMode),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("as_tool", ItemStack.EMPTY).forGetter(BreakBlockEffect::asTool)
    ).apply(instance, BreakBlockEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        List<ItemStack> drops = new ArrayList<>();
        targetInfo.targetBlocks().forEach(pos -> {
            BlockState blockState = context.level().getBlockState(pos);
            if (blockState.canEntityDestroy(context.level(), pos, context.caster())
                    && blockState.getBlock().defaultDestroyTime() > -1) {
                boolean drop = false;
                if (blockState.hasBlockEntity()) {
                    drop = true;
                } else if (dropMode == DropMode.COLLATE) {
                    drops.addAll(Block.getDrops(blockState, (ServerLevel) context.level(), pos, null, context.caster(),
                            asTool));
                } else {
                    drop = dropMode == DropMode.NORMAL;
                }
                if (drop) {
                    BlockEntity blockEntity;
                    if (blockState.hasBlockEntity()) {
                        blockEntity = context.level().getBlockEntity(pos);
                    } else {
                        blockEntity = null;
                    }
                    Block.dropResources(blockState, context.level(), pos, blockEntity, context.caster(), asTool);
                }
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
}
