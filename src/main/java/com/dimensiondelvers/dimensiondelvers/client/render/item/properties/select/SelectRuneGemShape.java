package com.dimensiondelvers.dimensiondelvers.client.render.item.properties.select;

import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectRuneGemShape implements SelectItemModelProperty<RunegemShape> {
    public static final SelectItemModelProperty.Type<SelectRuneGemShape, RunegemShape> TYPE = Type.create(MapCodec.unit(new SelectRuneGemShape()), RunegemShape.CODEC);

    @Override
    public @Nullable RunegemShape get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        RunegemData data = itemStack.get(ModDataComponentType.RUNEGEM_DATA);
        return (data != null && data.shape() != null) ? data.shape() : RunegemShape.CIRCLE;
    }

    @Override
    public @NotNull Type<? extends SelectItemModelProperty<RunegemShape>, RunegemShape> type() {
        return TYPE;
    }
}

