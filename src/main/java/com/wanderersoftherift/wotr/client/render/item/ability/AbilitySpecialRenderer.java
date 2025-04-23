package com.wanderersoftherift.wotr.client.render.item.ability;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Special renderer for rendering the icon of an ability within another item
 * 
 * @param baseItem An item that is rendered for the non-ability portion
 */
public record AbilitySpecialRenderer(Holder<Item> baseItem) implements SpecialModelRenderer<AbstractAbility> {
    @Override
    public void render(AbstractAbility ability, @NotNull ItemDisplayContext displayContext, PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);

        // We render the base item with the FIXED content, because this item is already being rendered with the display
        // context
        ItemStackRenderState renderState = new ItemStackRenderState();
        Minecraft.getInstance()
                .getItemModelResolver()
                .updateForTopItem(renderState, baseItem.value().getDefaultInstance(), ItemDisplayContext.FIXED, false,
                        null, null, 0);
        renderState.render(poseStack, bufferSource, packedLight, packedOverlay);

        if (ability != null && ability.getIcon() != null) {
            poseStack.translate(0.03F, 0.04F, 0);
            // TODO: Cache
            RenderType renderType = RenderType.create("ability_icon", DefaultVertexFormat.BLOCK,
                    VertexFormat.Mode.QUADS, 786_432, true, false,
                    RenderType.CompositeState.builder()
                            .setLightmapState(RenderStateShard.LIGHTMAP)
                            .setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER)
                            .setTextureState(
                                    new RenderStateShard.TextureStateShard(ability.getIcon(), TriState.FALSE, false))
                            .setCullState(RenderStateShard.NO_CULL)
                            .createCompositeState(true));
            VertexConsumer consumer = bufferSource.getBuffer(renderType);

            PoseStack.Pose pose = poseStack.last();
            vertex(consumer, pose, packedLight, -0.35f, -0.35f, 0.0f, 0, 1);
            vertex(consumer, pose, packedLight, 0.35f, -0.35f, 0.0f, 1, 1);
            vertex(consumer, pose, packedLight, 0.35f, 0.35f, 0.0f, 1, 0);
            vertex(consumer, pose, packedLight, -0.35f, 0.35f, 0.0f, 0, 0);
        }
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int packedLight, float x, float y, float z,
            int u, int v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @Nullable AbstractAbility extractArgument(@NotNull ItemStack stack) {
        Holder<AbstractAbility> holder = stack.get(ModDataComponentType.ABILITY);
        if (holder != null) {
            return holder.value();
        }
        return null;
    }

    public record Unbaked(Holder<Item> item) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<AbilitySpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder
                .mapCodec(instance -> instance
                        .group(Item.CODEC.fieldOf("base_item").forGetter(AbilitySpecialRenderer.Unbaked::item))
                        .apply(instance, AbilitySpecialRenderer.Unbaked::new));

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(@NotNull EntityModelSet modelSet) {
            return new AbilitySpecialRenderer(item);
        }
    }
}
