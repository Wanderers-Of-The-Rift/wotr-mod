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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//TODO: Get rid of literals
//TODO: Fix the getWidth() method
//TODO: Cleanup
//TODO: Icons for modifiers
@SuppressWarnings("DataFlowIssue")
public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    private static final int SOCKET_LINE_HEIGHT = 10;
    private static final Map<RunegemShape, ResourceLocation> SHAPE_RESOURCE_LOCATION_MAP_COLOR = Map.of(
            RunegemShape.CIRCLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/circle.png"),
            RunegemShape.DIAMOND, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/diamond.png"),
            RunegemShape.HEART, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/heart.png"),
            RunegemShape.PENTAGON, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/pentagon.png"),
            RunegemShape.SQUARE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/square.png"),
            RunegemShape.TRIANGLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/color/triangle.png")
    );

    private static final Map<RunegemShape, ResourceLocation> SHAPE_RESOURCE_LOCATION_MAP_GRAYSCALE = Map.of(
            RunegemShape.CIRCLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/circle.png"),
            RunegemShape.DIAMOND, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/diamond.png"),
            RunegemShape.HEART, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/heart.png"),
            RunegemShape.PENTAGON, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/pentagon.png"),
            RunegemShape.SQUARE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/square.png"),
            RunegemShape.TRIANGLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/grayscale/triangle.png")
    );

    private final GearSocketComponent component;

    public GearSocketTooltipRenderer(GearSocketComponent component) {
        this.component = component;
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int height = (this.getSockets().size() * SOCKET_LINE_HEIGHT) + font.lineHeight;

        for (GearSocket gearSocket : this.getSockets()) {
            if (gearSocket.modifier().isPresent()) {
                height += SOCKET_LINE_HEIGHT * (getLineCount(gearSocket) - 1);
            }
        }
        return height;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        // simulate renderText and renderImage to get the width
        int width = font.width(getSocketDesc()) + 10; // 10 for the padding
        for (GearSocket socket : this.getSockets()) {
            if (socket.modifier().isPresent()) {
                width += 10; // 10 for the icon
            }
        }
        return width;
    }

    @Override
    public void renderText(
            @NotNull Font pFont,
            int x,
            int y,
            @NotNull Matrix4f pMatrix4f,
            MultiBufferSource.@NotNull BufferSource pBufferSource) {
        int usedSockets = (int) this.component.gearSocket()
                .stream()
                .filter(socket -> socket.runegem().isPresent())
                .count();
        int totalSockets = this.component.gearSocket().size();
        MutableComponent socketsDescriptionComponent = getSocketDesc().copy().withStyle(ChatFormatting.GRAY);
        pFont.drawInBatch(socketsDescriptionComponent, x, y, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f,
                pBufferSource, Font.DisplayMode.NORMAL, 0, 0x00F000F0);

        if (getIsShiftDown()) {
            MutableComponent socketCountComponent = Component.literal("[" + usedSockets + "/" + totalSockets + "]")
                    .withStyle(ChatFormatting.DARK_GRAY);
            pFont.drawInBatch(socketCountComponent, x + pFont.width(getSocketDesc()) + totalSockets * 10, y,
                    ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0,
                    0x00F000F0);
        }

        y += SOCKET_LINE_HEIGHT + 2;

        for (GearSocket gearSocket : this.getSockets()) {
            List<AbstractModifierEffect> effects = getModifierEffects(gearSocket);

            if (gearSocket.isEmpty()) {
                pFont.drawInBatch(Component.literal("(Empty slot)"), x + 10, y - 1, 0x00555555, true, pMatrix4f,
                        pBufferSource, Font.DisplayMode.NORMAL, 0, 0x00F000F0);
                y += SOCKET_LINE_HEIGHT;
                continue;
            }

            if (effects.isEmpty()) {
                pFont.drawInBatch(Component.literal("???"), x + 10, y - 1, 0x00555555, true, pMatrix4f, pBufferSource,
                        Font.DisplayMode.NORMAL, 0, 0x00F000F0);
                y += SOCKET_LINE_HEIGHT;
                continue;
            }

            int modifierTier = gearSocket.modifier().map(m -> m.modifier().value().getTier()).orElse(0);

            for (int i = 0; i < effects.size(); i++) {
                AbstractModifierEffect effect = effects.get(i);

                MutableComponent lineComponent = Component.empty();
                TooltipComponent tooltipComponent = effect.getTooltipComponent(ItemStack.EMPTY,
                        gearSocket.modifier().map(ModifierInstance::roll).orElse(0.0F), ChatFormatting.AQUA);

                if (tooltipComponent instanceof ImageComponent img) {
                    lineComponent.append(Component.literal(img.base().getString()));
                }

                if (getIsShiftDown()) {
                    if (effect instanceof AttributeModifierEffect attributeEffect) {
                        if (attributeEffect.getOperation() == AttributeModifier.Operation.ADD_VALUE) {
                            lineComponent.append(Component
                                    .literal(" (T" + modifierTier + " : " + attributeEffect.getMinimumRoll() + " - "
                                            + attributeEffect.getMaximumRoll() + ")")
                                    .withStyle(ChatFormatting.DARK_GRAY));
                        } else if (attributeEffect.getOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                || attributeEffect.getOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                            lineComponent.append(Component
                                    .literal(" (T" + modifierTier + " : "
                                            + (int) (attributeEffect.getMinimumRoll() * 100) + "% - "
                                            + (int) (attributeEffect.getMaximumRoll() * 100) + "%)")
                                    .withStyle(ChatFormatting.DARK_GRAY));
                        }
                    } else {
                        lineComponent.append(
                                Component.literal(" (T " + modifierTier + ")").withStyle(ChatFormatting.DARK_GRAY));
                    }
                }

                pFont.drawInBatch(lineComponent, x + 10, y - 1, ChatFormatting.GREEN.getColor(), true, pMatrix4f,
                        pBufferSource, Font.DisplayMode.NORMAL, 0, 0x00F000F0);
                y += SOCKET_LINE_HEIGHT;
            }
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        int startX = x;
        x += font.width(getSocketDesc());
        for (GearSocket socket : this.component.gearSocket()) {
            renderSocketIcon(guiGraphics, pose, x, y, socket);

            x += 10;
        }
        x = startX; // Reset x to the original position

        y += SOCKET_LINE_HEIGHT;

        for (GearSocket socket : this.getSockets()) {
            List<AbstractModifierEffect> modifiers = getModifierEffects(socket);
            int val = modifiers.size();

            renderSocketIcon(guiGraphics, pose, x, y, socket);

            y += SOCKET_LINE_HEIGHT;

            for (int i = 1; i < val; i++) {
                y += SOCKET_LINE_HEIGHT;
            }
        }
    }

    private void renderSocketIcon(GuiGraphics guiGraphics, PoseStack pose, int x, int y, GearSocket socket) {
        pose.pushPose();
        pose.translate(x, y, 0);
        if (socket.modifier().isPresent()) {
            guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_RESOURCE_LOCATION_MAP_COLOR.get(socket.shape()), 0, 0, 0, 0,
                    8, 8, 8, 8
            );
        } else {
            guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_RESOURCE_LOCATION_MAP_GRAYSCALE.get(socket.shape()), 0, 0,
                    0, 0, 8, 8, 8, 8
            );
        }
        pose.popPose();
    }

    private List<GearSocket> getSockets() {
        List<GearSocket> sockets;
        if (getIsShiftDown()) {
            sockets = this.component.gearSocket();
        } else {
            sockets = this.component.getFilteredSockets();
        }
        return sockets;
    }

    public static Component getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }

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

    private boolean getIsShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    private boolean getIsAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);
    }

    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {
        public List<GearSocket> getFilteredSockets() {
            return this.gearSocket.stream().filter(socket -> socket.runegem().isPresent()).collect(Collectors.toList());
        }
    }
}
