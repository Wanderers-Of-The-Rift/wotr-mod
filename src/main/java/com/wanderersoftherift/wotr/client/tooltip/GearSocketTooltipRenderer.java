package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

//TODO: Get rid of literals
//TODO: Fix the getWidth() method
//TODO: Cleanup
//TODO: Icons for modifiers
@SuppressWarnings("DataFlowIssue")
public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    private static final int SOCKET_LINE_HEIGHT = 10;
    private static final Map<RunegemShape, ResourceLocation> SHAPE_RESOURCE_LOCATION_MAP =
            Map.of(
                    RunegemShape.CIRCLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/circle.png"),
                    RunegemShape.DIAMOND, WanderersOfTheRift.id("textures/tooltip/runegem/shape/diamond.png"),
                    RunegemShape.HEART, WanderersOfTheRift.id("textures/tooltip/runegem/shape/heart.png"),
                    RunegemShape.PENTAGON, WanderersOfTheRift.id("textures/tooltip/runegem/shape/pentagon.png"),
                    RunegemShape.SQUARE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/square.png"),
                    RunegemShape.TRIANGLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/triangle.png")
            );

    private final GearSocketComponent cmp;

    public GearSocketTooltipRenderer(GearSocketComponent cmp) {
        this.cmp = cmp;
    }


    @Override
    public int getHeight(@NotNull Font font) {
        int height = font.lineHeight + 2 + (this.cmp.gearSocket.size() * SOCKET_LINE_HEIGHT);

        for (GearSocket s : this.cmp.gearSocket) {
            if (s.modifier().isPresent()) {
                height += SOCKET_LINE_HEIGHT * (getLineCount(s) - 1);
            }
        }
        return height;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean isAltDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);
        int maxWidth = this.cmp.gearSocket.size() * 10 - 2;
        return maxWidth + font.width(getSocketDesc());
    }

    @Override
    public void renderText(@NotNull Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f, MultiBufferSource.@NotNull BufferSource pBufferSource) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean isAltDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);

        int usedSockets = (int) this.cmp.gearSocket().stream().filter(socket -> socket.runegem().isPresent()).count();
        int totalSockets = this.cmp.gearSocket().size();
        MutableComponent comp = getSocketDesc().copy().withStyle(ChatFormatting.GRAY);
        pFont.drawInBatch(comp, pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        if (isShiftDown) {
            MutableComponent comp1 = Component.literal("[" + usedSockets + "/" + totalSockets + "]").withStyle(ChatFormatting.DARK_GRAY);
            int comp1w = pFont.width(comp1);
            pFont.drawInBatch(comp1, pX + pFont.width(getSocketDesc()) + totalSockets * 10, pY, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        pY += SOCKET_LINE_HEIGHT + 2;

        for (GearSocket socket : this.cmp.gearSocket()) {
            List<AbstractModifierEffect> effects = getModifierEffects(socket);

            if (socket.isEmpty()) {
                pFont.drawInBatch(Component.literal("(Empty slot)"), pX + 10, pY - 1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY += SOCKET_LINE_HEIGHT;
                continue;
            }

            if (effects.isEmpty()) {
                pFont.drawInBatch(Component.literal("???"), pX + 10, pY - 1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY += SOCKET_LINE_HEIGHT;
                continue;
            }

            int modifierTier = socket.modifier().map(m -> m.modifier().value().getTier()).orElse(0);

            for (int i = 0; i < effects.size(); i++) {
                AbstractModifierEffect eff = effects.get(i);

                MutableComponent cmp = Component.literal("");
                TooltipComponent c = eff.getTooltipComponent(ItemStack.EMPTY, socket.modifier().map(ModifierInstance::roll).orElse(0.0F), ChatFormatting.AQUA);

                if (c instanceof ImageComponent img) {
                    cmp.append(Component.literal(img.base().getString()));
                }

                if (isShiftDown) {
                    if (eff instanceof AttributeModifierEffect attr) {
                        cmp.append(Component.literal(" (T" + modifierTier + " : " + attr.getMinimumRoll() + " - " + attr.getMaximumRoll() + ")").withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        cmp.append(Component.literal(" (T " + modifierTier + ")").withStyle(ChatFormatting.DARK_GRAY));
                    }
                }

                pFont.drawInBatch(cmp, pX + 10, pY - 1, ChatFormatting.GREEN.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                boolean isLast = (i == effects.size() - 1);
                pY += SOCKET_LINE_HEIGHT;
            }
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean isAltDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);

        PoseStack pose = guiGraphics.pose();

        int startX = x;
        x += font.width(getSocketDesc());
        for (GearSocket socket : this.cmp.gearSocket) {
            pose.pushPose();
            pose.translate(x, y, 0);

            if (socket.modifier().isPresent()) {
                guiGraphics.blit(RenderType.GUI_TEXTURED,
                        WanderersOfTheRift.id("textures/tooltip/runegem/shape/small" + socket.shape().getName() + ".png"),
                        0, 0,
                        0, 0,
                        8, 8,
                        8, 8
                );
            } else {
                pose.scale(0.5F, 0.5F, 1); // Apply scaling
                guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_RESOURCE_LOCATION_MAP.get(socket.shape()), 0,
                        0, 0, 0, 16, 16, 16, 16);
            }

            pose.popPose(); // Restore position

            // Move x forward (accounting for the scaling)
            x += 10; // Adjust spacing to fit the scaled size
        }
        x = startX; // Reset x to the original position

        y += SOCKET_LINE_HEIGHT;

        for (GearSocket socket : this.cmp.gearSocket()) {
            List<AbstractModifierEffect> modifiers = getModifierEffects(socket);
            int val = modifiers.size();

            pose.pushPose();
            pose.translate(x, y, 0);
            if (socket.modifier().isPresent()) {
                guiGraphics.blit(RenderType.GUI_TEXTURED,
                        WanderersOfTheRift.id("textures/tooltip/runegem/shape/small" + socket.shape().getName() + ".png"),
                        0, 0,
                        0, 0,
                        8, 8,
                        8, 8
                );
            } else {
                pose.scale(0.5F, 0.5F, 1); // Apply scaling
                guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_RESOURCE_LOCATION_MAP.get(socket.shape()), 0,
                        0, 0, 0, 16, 16, 16, 16);
            }
            pose.popPose();

            y += SOCKET_LINE_HEIGHT;

            for (int i = 1; i < val; i++) {
                y += SOCKET_LINE_HEIGHT;
            }
        }
    }

    public static Component getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }


    /*------ Helpers ------*/

    // Get all ModifierEffects of a GearSocket
    private List<AbstractModifierEffect> getModifierEffects(GearSocket socket) {
        return socket.modifier().map(m -> m.modifier().value().getModifierEffects()).orElse(List.of());
    }

    private int getLineCount(GearSocket socket) {
        return socket.modifier().map(m -> {
            int e = m.modifier().value().getModifierEffects().size();
            if (e == 0) {
                return 1;
            }
            return e;
        }).orElse(0);
    }

    // Sort them based on the amount of modifiers a socket has
    private List<GearSocket> getSortedSockets(List<GearSocket> sockets) {
        return sockets.stream()
                .sorted(Comparator.comparingInt(socket -> getModifierEffects((GearSocket) socket).size()).reversed())
                .toList();
    }


    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {
    }
}
