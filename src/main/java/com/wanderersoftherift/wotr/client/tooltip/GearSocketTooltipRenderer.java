package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    public static final Map<RunegemShape, ResourceLocation> SHAPE_RESOURCE_LOCATION_MAP =
            Map.of(
                    RunegemShape.CIRCLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/circle.png"),
                    RunegemShape.DIAMOND, WanderersOfTheRift.id("textures/tooltip/runegem/shape/diamond.png"),
                    RunegemShape.HEART, WanderersOfTheRift.id("textures/tooltip/runegem/shape/heart.png"),
                    RunegemShape.PENTAGON, WanderersOfTheRift.id("textures/tooltip/runegem/shape/pentagon.png"),
                    RunegemShape.SQUARE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/square.png"),
                    RunegemShape.TRIANGLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/triangle.png")
            );
    private final int spacing = Minecraft.getInstance().font.lineHeight + 2;
    private final GearSocketComponent cmp;
    private static final int SOCKET_LINE_HEIGHT = 20;


    public GearSocketTooltipRenderer(GearSocketComponent cmp) {
        this.cmp = cmp;
    }


    @Override
    public int getHeight(@NotNull Font font) {
        return font.lineHeight + 2 + (this.cmp.gearSocket.size() * 20);
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
    public void renderText(Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f, MultiBufferSource.@NotNull BufferSource pBufferSource) {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
            pFont.drawInBatch(getSocketDesc(), pX, pY, ChatFormatting.GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        } else {
            pFont.drawInBatch(getSocketDesc(), pX, pY, ChatFormatting.GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        List<GearSocket> toDraw = new ArrayList<>();
        pY += 15;
        for (GearSocket socket : this.cmp.gearSocket) {
            if (socket.modifier().isPresent()) {
                pFont.drawInBatch(Component.literal("-"), pX + 20, pY-1, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                //TODO: figure out the tooltip shenanigans
                pFont.drawInBatch(Component.literal("TODO"), pX + 30, pY-1, ChatFormatting.GREEN.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY+= 20;
            } else {
                toDraw.add(socket);
            }

        }

        for (GearSocket ignored : toDraw) {
            pFont.drawInBatch(Component.literal("(Empty slot)"), pX + 22, pY-1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            pY+= 20;
        }

    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        List<GearSocket> toDraw = new ArrayList<>();
        y+= 10;
        for (GearSocket socket : this.cmp.gearSocket) {
            if (socket.modifier().isPresent() && socket.runegem().isPresent()) {
                pose.pushPose();
                pose.translate(x, y, 0);

                ItemStack fakeStack = new ItemStack(ModItems.RUNEGEM.get());
                fakeStack.set(ModDataComponentType.RUNEGEM_DATA, socket.runegem().get());
                fakeStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                guiGraphics.renderFakeItem(fakeStack, 0,0);
                pose.popPose();
                y+= SOCKET_LINE_HEIGHT;
            } else {
                toDraw.add(socket);
            }
        }

        for (GearSocket socket : toDraw) {
            guiGraphics.blit(RenderType.GUI_TEXTURED,
                    SHAPE_RESOURCE_LOCATION_MAP.get(socket.shape()),
                    x, y,
                    0, 0,
                    16, 16,
                    16, 16
            );
            y+= SOCKET_LINE_HEIGHT;
        }
    }




    public static Component getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }

    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {
    }
}
