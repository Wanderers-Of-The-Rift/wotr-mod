package com.wanderersoftherift.wotr.core.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.quest.Goal;
import com.wanderersoftherift.wotr.serialization.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * This goal requires items to be handed in to complete.
 * 
 * @param item  The item (or set of items) that the quest requires
 * @param count How many total items need to be provided
 */
public record GiveItemGoal(Ingredient item, int count) implements Goal {

    public static final MapCodec<GiveItemGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(GiveItemGoal::item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(GiveItemGoal::count)
            ).apply(instance, GiveItemGoal::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GiveItemGoal> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, GiveItemGoal::item, ByteBufCodecs.INT, GiveItemGoal::count,
            GiveItemGoal::new
    );

    public static final DualCodec<GiveItemGoal> TYPE = new DualCodec<>(CODEC, STREAM_CODEC);

    @Override
    public DualCodec<GiveItemGoal> getType() {
        return TYPE;
    }

}
