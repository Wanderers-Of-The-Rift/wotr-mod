package com.wanderersoftherift.wotr.client.render.item.emblem;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Special renderer for rendering an emblem (icon) on an item
 */
public final class EmblemSpecialRenderer implements SpecialModelRenderer<ResourceLocation> {
    private final Holder<Item> baseItem;
    private final float scale;
    private final Vector3fc offset;
    private final EmblemProvider emblemProvider;
    private final Map<ResourceLocation, RenderType> renderTypes = new LinkedHashMap<>();

    /**
     * @param baseItem An item that is rendered for the non-ability portion
     */
    public EmblemSpecialRenderer(Holder<Item> baseItem, EmblemProvider emblemProvider, float scale, Vector3fc offset) {
        this.baseItem = baseItem;
        this.scale = scale;
        this.offset = offset;
        this.emblemProvider = emblemProvider;
    }

    @Override
    public void render(
            ResourceLocation icon,
            @NotNull ItemDisplayContext displayContext,
            PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            boolean hasFoilType) {

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.rotateAround(new Quaternionf().rotationZYX(0, Math.PI_f, 0), 0, 0, 0);

        // We render the base item with the FIXED content, because this item is already being rendered with the display
        // context
        ItemStackRenderState renderState = new ItemStackRenderState();
        Minecraft.getInstance()
                .getItemModelResolver()
                .updateForTopItem(renderState, baseItem.value().getDefaultInstance(), ItemDisplayContext.FIXED, false,
                        null, null, 0);
        renderState.render(poseStack, bufferSource, packedLight, packedOverlay);

        if (icon != null) {
            poseStack.translate(offset.x(), offset.y(), offset.z());
            VertexConsumer consumer = bufferSource.getBuffer(getRenderType(icon));
            PoseStack.Pose pose = poseStack.last();
            vertex(consumer, pose, packedLight, -0.5f * scale, -0.5f * scale, 0.0f, 0, 1);
            vertex(consumer, pose, packedLight, 0.5f * scale, -0.5f * scale, 0.0f, 1, 1);
            vertex(consumer, pose, packedLight, 0.5f * scale, 0.5f * scale, 0.0f, 1, 0);
            vertex(consumer, pose, packedLight, -0.5f * scale, 0.5f * scale, 0.0f, 0, 0);
        }
        poseStack.popPose();
    }

    private RenderType getRenderType(ResourceLocation icon) {
        return renderTypes.computeIfAbsent(icon,
                resourceLocation -> RenderType.create("emblem_" + icon.getNamespace() + "__" + icon.getPath(),
                        DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786_432, true, false,
                        RenderType.CompositeState.builder()
                                .setLightmapState(RenderStateShard.LIGHTMAP)
                                .setShaderState(RenderStateShard.RENDERTYPE_CUTOUT_SHADER)
                                .setTextureState(
                                        new RenderStateShard.TextureStateShard(icon, TriState.FALSE, false))
                                .setCullState(RenderStateShard.NO_CULL)
                                .createCompositeState(true)));
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            int packedLight,
            float x,
            float y,
            float z,
            int u,
            int v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv((float) u, (float) v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @Nullable ResourceLocation extractArgument(@NotNull ItemStack stack) {
        return emblemProvider.getIcon(stack);
    }

    public Holder<Item> baseItem() {
        return baseItem;
    }

    public float scale() {
        return scale;
    }

    public Vector3fc offset() {
        return offset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EmblemSpecialRenderer other) {
            return Objects.equals(this.baseItem, other.baseItem)
                    && Float.floatToIntBits(this.scale) == Float.floatToIntBits(other.scale)
                    && Objects.equals(this.offset, other.offset)
                    && Objects.equals(this.emblemProvider, other.emblemProvider);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseItem, scale, offset);
    }

    @Override
    public String toString() {
        return "EmblemSpecialRenderer[" + "baseItem=" + baseItem + ", " + "scale=" + scale + ", " + "offset=" + offset
                + ']';
    }

    public record Unbaked(Holder<Item> base, EmblemProvider emblemProvider, float scale, float xOffset, float yOffset,
            float zOffset) implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Item.CODEC.fieldOf("base_item").forGetter(Unbaked::base),
                EmblemProvider.CODEC.fieldOf("emblemProvider").forGetter(Unbaked::emblemProvider),
                Codec.FLOAT.optionalFieldOf("scale", 0.5f).forGetter(Unbaked::scale),
                Codec.FLOAT.optionalFieldOf("x_offset", 0f).forGetter(Unbaked::xOffset),
                Codec.FLOAT.optionalFieldOf("y_offset", 0f).forGetter(Unbaked::yOffset),
                Codec.FLOAT.optionalFieldOf("z_offset", 0.05f).forGetter(Unbaked::zOffset)
        ).apply(instance, Unbaked::new));

        @Override
        public @NotNull MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(@NotNull EntityModelSet modelSet) {
            return new EmblemSpecialRenderer(base, emblemProvider, scale, new Vector3f(xOffset, yOffset, zOffset));
        }
    }
}
