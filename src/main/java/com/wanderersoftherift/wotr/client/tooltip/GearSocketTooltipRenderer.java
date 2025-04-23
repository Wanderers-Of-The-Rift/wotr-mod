package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    public static final Map<RunegemShape, ResourceLocation> SHAPE_RESOURCE_LOCATION_MAP = Map.of(RunegemShape.CIRCLE,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/circle.png"), RunegemShape.DIAMOND,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/diamond.png"), RunegemShape.HEART,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/heart.png"), RunegemShape.PENTAGON,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/pentagon.png"), RunegemShape.SQUARE,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/square.png"), RunegemShape.TRIANGLE,
            WanderersOfTheRift.id("textures/tooltip/runegem/shape/triangle.png"));
    private final int spacing = Minecraft.getInstance().font.lineHeight + 2;
    private final GearSocketComponent cmp;

    public GearSocketTooltipRenderer(GearSocketComponent cmp) {
        this.cmp = cmp;
    }

    @Override
    public int getHeight(@NotNull Font font) {
        return font.lineHeight + 2;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int maxWidth = 0;
        for (GearSocket socket : this.cmp.gearSocket) {
            maxWidth += 20; // For each available socket +16 w/ 4px between each
        }
        return maxWidth + font.width(getSocketDesc());
    }

    @Override
    public void renderText(Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f,
            MultiBufferSource.@NotNull BufferSource pBufferSource) {
        pFont.drawInBatch(getSocketDesc(), pX, pY, ChatFormatting.GRAY.getColor(), true, pMatrix4f, pBufferSource,
                Font.DisplayMode.NORMAL, 0, 15_728_880);
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        float scaleFactor = 0.5F;

        x += font.width(getSocketDesc());
        for (GearSocket socket : this.cmp.gearSocket) {
            pose.pushPose();
            pose.scale(scaleFactor, scaleFactor, 1); // Apply scaling

            int scaledX = (int) (x / scaleFactor);
            int scaledY = (int) (y / scaleFactor);

            if (socket.modifier().isPresent()) {
                RunegemData data = new RunegemData(socket.shape(), null, RunegemTier.RAW);
                ItemStack fakeStack = new ItemStack(ModItems.RUNEGEM.get());
                fakeStack.set(ModDataComponentType.RUNEGEM_DATA, data);

                guiGraphics.renderFakeItem(fakeStack, scaledX, scaledY - 1);
            } else {
                guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_RESOURCE_LOCATION_MAP.get(socket.shape()), scaledX,
                        scaledY - 1, 0, 0, 16, 16, 16, 16);
            }

            pose.popPose(); // Restore position

            // Move x forward (accounting for the scaling)
            x += 10; // Adjust spacing to fit the scaled size
        }
    }

    public static Component getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }

    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {
    }
}
