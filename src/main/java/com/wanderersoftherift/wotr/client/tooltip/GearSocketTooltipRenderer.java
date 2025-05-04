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
        int maxWidth = 0;

        // Calculate width of the header line (sockets description + icons + count)
        int headerWidth = font.width(getSocketsDescriptionComponent()) + 4;
        headerWidth += this.component.gearSocket().size() * 10; // Socket icons (8px wide + 2px spacing)

        if (getIsShiftDown()) {
            // Add the socket count text
            int usedSockets = (int) this.component.gearSocket()
                    .stream()
                    .filter(socket -> socket.runegem().isPresent())
                    .count();
            int totalSockets = this.component.gearSocket().size();
            MutableComponent socketCountComponent = getSocketCountComponent(usedSockets, totalSockets);
            headerWidth += 4 + font.width(socketCountComponent); // 4px spacing + text width
        }

        maxWidth = Math.max(maxWidth, headerWidth);

        // Calculate width for each socket line and its modifiers
        for (GearSocket gearSocket : this.getSockets()) {
            int socketLineWidth = 10; // Icon width + margin

            if (gearSocket.isEmpty()) {
                socketLineWidth += font.width(getEmptySocketComponent());
                maxWidth = Math.max(maxWidth, socketLineWidth);
                continue;
            }

            List<AbstractModifierEffect> effects = getModifierEffects(gearSocket);

            if (effects.isEmpty()) {
                socketLineWidth += font.width(getUnknownSocketComponent());
                maxWidth = Math.max(maxWidth, socketLineWidth);
                continue;
            }

            // Calculate width for each effect line
            int modifierTier = gearSocket.modifier().map(m -> m.modifier().value().getTier()).orElse(0);

            for (AbstractModifierEffect effect : effects) {
                MutableComponent lineComponent = Component.empty();
                TooltipComponent tooltipComponent = effect.getTooltipComponent(ItemStack.EMPTY,
                        gearSocket.modifier().map(ModifierInstance::roll).orElse(0.0F), ChatFormatting.AQUA);

                if (tooltipComponent instanceof ImageComponent img) {
                    lineComponent.append(Component.literal(img.base().getString()));
                }

                int effectWidth = font.width(lineComponent);

                if (getIsShiftDown()) {
                    MutableComponent detailsComponent = Component.empty();
                    if (effect instanceof AttributeModifierEffect attributeEffect) {
                        if (attributeEffect.getOperation() == AttributeModifier.Operation.ADD_VALUE) {
                            detailsComponent = Component
                                    .literal(" (T" + modifierTier + " : " + attributeEffect.getMinimumRoll() + " - "
                                            + attributeEffect.getMaximumRoll() + ")")
                                    .withStyle(ChatFormatting.DARK_GRAY);
                        } else if (attributeEffect.getOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                || attributeEffect.getOperation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                            detailsComponent = Component
                                    .literal(" (T" + modifierTier + " : "
                                            + (int) (attributeEffect.getMinimumRoll() * 100) + "% - "
                                            + (int) (attributeEffect.getMaximumRoll() * 100) + "%)")
                                    .withStyle(ChatFormatting.DARK_GRAY);
                        }
                    } else {
                        detailsComponent = Component.literal(" (T " + modifierTier + ")")
                                .withStyle(ChatFormatting.DARK_GRAY);
                    }
                    effectWidth += font.width(detailsComponent);
                }

                socketLineWidth = Math.max(socketLineWidth, 10 + effectWidth); // 10px for icon + effect text width
                maxWidth = Math.max(maxWidth, socketLineWidth);
            }
        }

        return maxWidth;
    }

    @Override
    public void renderText(
            @NotNull Font font,
            int x,
            int y,
            @NotNull Matrix4f matrix4f,
            MultiBufferSource.@NotNull BufferSource bufferSource) {
        int usedSockets = (int) this.component.gearSocket()
                .stream()
                .filter(socket -> socket.runegem().isPresent())
                .count();
        int totalSockets = this.component.gearSocket().size();
        MutableComponent socketsDescriptionComponent = getSocketsDescriptionComponent();
        font.drawInBatch(socketsDescriptionComponent, x, y, ChatFormatting.DARK_GRAY.getColor(), true, matrix4f,
                bufferSource, Font.DisplayMode.NORMAL, ChatFormatting.BLACK.getColor(), 0x00F000F0);

        if (getIsShiftDown()) {
            MutableComponent socketCountComponent = getSocketCountComponent(usedSockets, totalSockets);
            font.drawInBatch(socketCountComponent,
                    x + font.width(getSocketsDescriptionComponent()) + 4 + (totalSockets * 10 - 2) + 4, y,
                    ChatFormatting.DARK_GRAY.getColor(), true, matrix4f, bufferSource, Font.DisplayMode.NORMAL,
                    ChatFormatting.BLACK.getColor(), 0x00F000F0);
        }

        y += SOCKET_LINE_HEIGHT + 2;

        for (GearSocket gearSocket : this.getSockets()) {
            List<AbstractModifierEffect> effects = getModifierEffects(gearSocket);

            if (gearSocket.isEmpty()) {
                font.drawInBatch(getEmptySocketComponent(), x + 10, y - 2, ChatFormatting.DARK_GRAY.getColor(), true,
                        matrix4f, bufferSource, Font.DisplayMode.NORMAL, ChatFormatting.BLACK.getColor(), 0x00F000F0);
                y += SOCKET_LINE_HEIGHT;
                continue;
            }

            if (effects.isEmpty()) {
                font.drawInBatch(getUnknownSocketComponent(), x + 10, y - 2, ChatFormatting.DARK_GRAY.getColor(), true,
                        matrix4f, bufferSource, Font.DisplayMode.NORMAL, ChatFormatting.BLACK.getColor(), 0x00F000F0);
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

                font.drawInBatch(lineComponent, x + 10, y - 2, ChatFormatting.GREEN.getColor(), true, matrix4f,
                        bufferSource, Font.DisplayMode.NORMAL, ChatFormatting.BLACK.getColor(), 0x00F000F0);
                y += SOCKET_LINE_HEIGHT;
            }
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        int startX = x;
        x += font.width(getSocketsDescriptionComponent()) + 4;
        for (GearSocket socket : this.component.gearSocket()) {
            renderSocketIcon(guiGraphics, pose, x, y, socket);

            x += 10;
        }
        x = startX; // Reset x to the original position

        y += SOCKET_LINE_HEIGHT;

        for (GearSocket gearSocket : this.getSockets()) {
            List<AbstractModifierEffect> modifiers = getModifierEffects(gearSocket);
            //TODO: Icons for modifiers

            renderSocketIcon(guiGraphics, pose, x, y, gearSocket);

            y += SOCKET_LINE_HEIGHT;

            for (int i = 1; i < modifiers.size(); i++) {
                y += SOCKET_LINE_HEIGHT;
            }
        }
    }

    private void renderSocketIcon(GuiGraphics guiGraphics, PoseStack pose, int x, int y, GearSocket gearSocket) {
        pose.pushPose();
        pose.translate(x, y, 0);

        ResourceLocation texture;
        if (gearSocket.modifier().isPresent()) {
            texture = SHAPE_RESOURCE_LOCATION_MAP_COLOR.get(gearSocket.shape());
        } else {
            texture = SHAPE_RESOURCE_LOCATION_MAP_GRAYSCALE.get(gearSocket.shape());
        }

        guiGraphics.blit(RenderType.GUI_TEXTURED, texture, 0, 0, 0, 0, 8, 8, 8, 8
        );

        pose.popPose();
    }

    private List<GearSocket> getSockets() {
        List<GearSocket> gearSockets;
        if (getIsShiftDown()) {
            gearSockets = this.component.gearSocket();
        } else {
            gearSockets = this.component.getFilteredSockets();
        }
        return gearSockets;
    }

    public static MutableComponent getSocketsDescriptionComponent() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket").withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent getEmptySocketComponent() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket.empty")
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static MutableComponent getUnknownSocketComponent() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket.unknown")
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static MutableComponent getSocketCountComponent(int used, int total) {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket.count", used, total)
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    private List<AbstractModifierEffect> getModifierEffects(GearSocket gearSocket) {
        return gearSocket.modifier()
                .map(modifierInstance -> modifierInstance.modifier().value().getModifierEffects())
                .orElse(List.of());
    }

    private int getLineCount(GearSocket gearSocket) {
        return gearSocket.modifier().map(modifierInstance -> {
            int effectCount = modifierInstance.modifier().value().getModifierEffects().size();
            if (effectCount == 0) {
                return 1;
            }
            return effectCount;
        }).orElse(0);
    }

    private boolean getIsShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    private boolean getIsAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);
    }

    public record GearSocketComponent(ItemStack itemStack, List<GearSocket> gearSocket) implements TooltipComponent {
        public List<GearSocket> getFilteredSockets() {
            return this.gearSocket.stream().filter(socket -> socket.runegem().isPresent()).collect(Collectors.toList());
        }
    }
}
