package com.wanderersoftherift.wotr.core.goal.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.goal.Goal;
import com.wanderersoftherift.wotr.core.goal.GoalManager;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

@EventBusSubscriber
public record TossItemGoal(Ingredient item, int count) implements ItemGoal {

    public static final MapCodec<TossItemGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(TossItemGoal::item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(TossItemGoal::count)
            ).apply(instance, TossItemGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TossItemGoal> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, TossItemGoal::item, ByteBufCodecs.INT, TossItemGoal::count,
            TossItemGoal::new
    );

    public static final DualCodec<TossItemGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<? extends Goal> getType() {
        return TYPE;
    }

    @SubscribeEvent
    public static void onTossItem(ItemTossEvent event) {
        GoalManager.getGoalStates(event.getPlayer(), TossItemGoal.class).forEach(goalState -> {
            if (!goalState.getGoal().item().acceptsItem(event.getEntity().getItem().getItemHolder())
                    || event.isCanceled()) {
                return;
            }
            int count = event.getEntity().getItem().getCount();
            int residual = goalState.getGoal().count - goalState.getProgress();
            if (residual < count) {
                event.getEntity().getItem().setCount(count - residual);
            } else {
                event.setCanceled(true);
            }
            goalState.setProgress(event.getPlayer(), goalState.getProgress() + count);
        });
    }

}
