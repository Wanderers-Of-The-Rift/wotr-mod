package com.wanderersoftherift.wotr.core.guild.quest.goal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.guild.quest.Goal;
import net.minecraft.world.item.crafting.Ingredient;

public class GiveItemGoal extends Goal {

    public static final MapCodec<GiveItemGoal> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(GiveItemGoal::item),
                    Codec.INT.optionalFieldOf("quantity", 1).forGetter(GiveItemGoal::quantity)
            ).apply(instance, GiveItemGoal::new));

    private final Ingredient item;
    private final int quantity;

    public GiveItemGoal(Ingredient item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public MapCodec<? extends Goal> getCodec() {
        return null;
    }

    public Ingredient item() {
        return item;
    }

    public int quantity() {
        return quantity;
    }
}
