package com.wanderersoftherift.wotr.client.render.item.properties.select;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.runegem.RuneGemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectRuneGemShape implements SelectItemModelProperty<RuneGemShape> {
    public static final SelectItemModelProperty.Type<SelectRuneGemShape, RuneGemShape> TYPE = Type.create(MapCodec.unit(new SelectRuneGemShape()), RuneGemShape.CODEC);

    @Override
    public @Nullable RuneGemShape get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        RunegemData data = itemStack.get(ModDataComponentType.RUNEGEM_DATA);
        return (data != null && data.shape() != null) ? data.shape() : RuneGemShape.CIRCLE;
    }

    @Override
    public @NotNull Type<? extends SelectItemModelProperty<RuneGemShape>, RuneGemShape> type() {
        return TYPE;
    }
}

