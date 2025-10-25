package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

//TODO: Icons for modifiers
@SuppressWarnings("DataFlowIssue")
public record GearSocketTooltipRenderer(GearSocketComponent socketComponent) implements ClientTooltipComponent {
    private static final int SOCKET_LINE_HEIGHT = 20;
    private static final int MODIFIER_DESCRIPTION_HORIZONTAL_OFFSET = 30;
    private static final Map<RunegemShape, ResourceLocation> SHAPE_TEXTURES = Map.of(
            RunegemShape.CIRCLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/circle.png"),
            RunegemShape.DIAMOND, WanderersOfTheRift.id("textures/tooltip/runegem/shape/diamond.png"),
            RunegemShape.HEART, WanderersOfTheRift.id("textures/tooltip/runegem/shape/heart.png"),
            RunegemShape.PENTAGON, WanderersOfTheRift.id("textures/tooltip/runegem/shape/pentagon.png"),
            RunegemShape.SQUARE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/square.png"),
            RunegemShape.TRIANGLE, WanderersOfTheRift.id("textures/tooltip/runegem/shape/triangle.png")
    );

    @Override
    public int getHeight(@NotNull Font font) {
        int baseHeight = font.lineHeight + 2;

        var isKeyDown = ModifierRenderHelper.isKeyDown();
        if (!isKeyDown) {
            baseHeight += font.lineHeight + 2;
        }

        int contentHeight = socketComponent.gearSocket().stream().mapToInt(socket -> {
            var socketModifier = socket.modifier();
            if (socketModifier.isPresent() && socket.runegem().isPresent()) {
                return SOCKET_LINE_HEIGHT + 12 * ModifierRenderHelper.countTooltips(socketModifier.get(), isKeyDown);
            } else {
                return SOCKET_LINE_HEIGHT;
            }
        }).sum();
        return baseHeight + contentHeight;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        boolean isShiftDown = ModifierRenderHelper.isKeyDown();
        int used = (int) socketComponent.gearSocket().stream().filter(s -> s.runegem().isPresent()).count();

        int maxWidth = font
                .width(getSocketDesc().getString() + "[" + used + "/" + socketComponent.gearSocket().size() + "]");
        maxWidth = Math.max(maxWidth,
                font.width(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info",
                        WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName())));

        for (GearSocket socket : socketComponent.gearSocket()) {
            var modifier = socket.modifier();
            int socketWidth;
            if (modifier.isPresent() && socket.runegem().isPresent()) {
                socketWidth = ModifierRenderHelper.modifierTextWidth(modifier.get(), isShiftDown, font);
            } else {
                socketWidth = font
                        .width(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".empty_socket"));
            }
            maxWidth = Math.max(maxWidth, MODIFIER_DESCRIPTION_HORIZONTAL_OFFSET + socketWidth + 10);
        }
        return maxWidth;
    }

    @Override
    public void renderText(
            Font font,
            int pX,
            int pY,
            @NotNull Matrix4f transform,
            MultiBufferSource.@NotNull BufferSource buffer) {

        boolean isKeyDown = ModifierRenderHelper.isKeyDown();

        if (!isKeyDown) {
            font.drawInBatch(
                    Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info",
                            WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName().getString()),
                    pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, transform, buffer, Font.DisplayMode.NORMAL, 0,
                    LightTexture.FULL_BRIGHT);
            pY += font.lineHeight + 2;
        }

        var used = new ArrayList<GearSocket>(socketComponent.gearSocket().size());
        var unused = new ArrayList<GearSocket>(socketComponent.gearSocket().size());

        for (var socket : socketComponent.gearSocket()) {
            if (socket.modifier().isPresent() && socket.runegem().isPresent()) {
                used.add(socket);
            } else {
                unused.add(socket);
            }
        }

        font.drawInBatch(
                getSocketDesc().withStyle(ChatFormatting.GRAY)
                        .append(Component
                                .literal(MessageFormat.format("[{0}/{1}]", used.size(),
                                        socketComponent.gearSocket().size()))
                                .withStyle(ChatFormatting.DARK_GRAY)),
                pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, transform, buffer, Font.DisplayMode.NORMAL, 0,
                LightTexture.FULL_BRIGHT);
        pY += 15;

        sortSockets(used);
        for (GearSocket socket : used) {
            var modifierInstance = socket.modifier().get();

            int tooltipCount = ModifierRenderHelper.countTooltips(modifierInstance, isKeyDown);
            for (int i = 0; i < tooltipCount; i++) {
                font.drawInBatch(Component.literal(">"), pX + 20, pY + 12 * i - 1, ChatFormatting.DARK_GRAY.getColor(),
                        true, transform, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            }

            ModifierRenderHelper.renderModifierEffectDescriptions(modifierInstance, isKeyDown, font,
                    pX + MODIFIER_DESCRIPTION_HORIZONTAL_OFFSET, pY - 1, 12, transform, buffer);

            pY += 12 * tooltipCount + SOCKET_LINE_HEIGHT - 12;
        }

        for (GearSocket ignored : unused) {
            font.drawInBatch(Component.literal(">"), pX + 20, pY - 1, 0x555555, true, transform, buffer,
                    Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            Component display;
            if (isKeyDown) {
                display = Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".empty_socket")
                        .withColor(0x555555);
            } else {
                display = Component.literal("-").withColor(0x19191a);
            }
            font.drawInBatch(display, pX + MODIFIER_DESCRIPTION_HORIZONTAL_OFFSET, pY - 1, 0, true, transform, buffer,
                    Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            pY += SOCKET_LINE_HEIGHT;
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        boolean isKeyDown = ModifierRenderHelper.isKeyDown();
        PoseStack pose = guiGraphics.pose();
        y += 10;

        if (!isKeyDown) {
            y += font.lineHeight + 2;
        }

        var used = new ArrayList<GearSocket>(socketComponent.gearSocket().size());
        var unused = new ArrayList<GearSocket>(socketComponent.gearSocket().size());

        for (var socket : socketComponent.gearSocket()) {
            if (socket.modifier().isPresent() && socket.runegem().isPresent()) {
                used.add(socket);
            } else {
                unused.add(socket);
            }
        }

        sortSockets(used);

        boolean painted = false;
        for (GearSocket socket : used) {
            var modifierInstance = socket.modifier().get();
            ItemStack fakeStack = new ItemStack(WotrItems.RUNEGEM.get());
            fakeStack.set(WotrDataComponentType.RUNEGEM_DATA, socket.runegem().orElse(null));
            fakeStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            pose.pushPose();
            pose.translate(x, y, 0);
            guiGraphics.renderFakeItem(fakeStack, 0, 0);
            pose.popPose();

            if (painted) {
                drawSocketDividerLine(guiGraphics, x, y, width);
            }

            painted = true;

            ModifierRenderHelper.renderModifierEffectIcons(modifierInstance, isKeyDown, font,
                    x + MODIFIER_DESCRIPTION_HORIZONTAL_OFFSET, y + 4, 12, guiGraphics);

            int tooltipCount = ModifierRenderHelper.countTooltips(modifierInstance, isKeyDown);
            y += 12 * tooltipCount;
            y += SOCKET_LINE_HEIGHT - 12;
        }

        painted = false;

        for (GearSocket socket : unused) {
            guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_TEXTURES.get(socket.shape()), x, y, 0, 0, 16, 16, 16, 16);
            if (painted) {
                drawSocketDividerLine(guiGraphics, x, y, width);
            }
            painted = true;
            y += SOCKET_LINE_HEIGHT;
        }
    }

    /*------ Helpers ------*/

    public static MutableComponent getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }

    private static void drawSocketDividerLine(GuiGraphics gui, int x, int y, int width) {
        gui.fill(x + 20, y - 3, x + width - 10, y - 2, 0xFF383838);
        gui.fill(x + 21, y - 2, x + width - 9, y - 1, 0x4019191a);
    }

    private static int getModifierTierCount(GearSocket socket) {
        var modifier = socket.modifier();
        if (modifier.isEmpty()) {
            return 0;
        }

        return modifier.get().modifier().value().getModifierTierList().size();
    }

    private static void sortSockets(List<GearSocket> sockets) {
        sockets.sort(Comparator.comparingInt(GearSocketTooltipRenderer::getModifierTierCount).reversed());
    }

    public record GearSocketComponent(List<GearSocket> gearSocket) implements TooltipComponent {
    }
}
