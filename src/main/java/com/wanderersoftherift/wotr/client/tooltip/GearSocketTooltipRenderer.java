package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

//TODO: Get rid of literals
//TODO: Fix the getWidth() method
//TODO: Cleanup
//TODO: Icons for modifiers
@SuppressWarnings("DataFlowIssue")
public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    private static final int SOCKET_LINE_HEIGHT = 20;
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
            if(s.modifier().isPresent()) {
                height += SOCKET_LINE_HEIGHT * (getModifierEffects(s).size() - 1);
            }
        }
        return height;
    }

    @Override // TODO: check with final version
    public int getWidth(@NotNull Font font) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);

        int baseWidth = font.width(getSocketDesc().getString() + "[" +
                this.cmp.gearSocket().stream().filter(socket -> socket.runegem().isPresent()).count() +
                "/" + this.cmp.gearSocket().size() + "]");

        int maxWidth = Math.max(0, baseWidth);
        for (GearSocket socket : this.cmp.gearSocket()) {
            int modifierTier = socket.modifier().map(m -> m.modifier().value().getTier()).orElse(0);

            if (socket.modifier().isPresent() && socket.runegem().isPresent()) {
                List<AbstractModifierEffect> effects = socket.modifier().get().modifier().value().getModifierEffects();

                for (AbstractModifierEffect effect : effects) {
                    TooltipComponent tooltip = effect.getTooltipComponent(ItemStack.EMPTY, socket.modifier().get().roll(), ChatFormatting.AQUA);

                    String effectText = "";

                    if (tooltip instanceof ImageComponent img) {
                        effectText = img.base().getString();
                    }
                    int width;

                    if(isShiftDown) {
                        if (effect instanceof AttributeModifierEffect attr) {
                            effectText = effectText.concat(" (T" + modifierTier + " : " + attr.getMinimumRoll() + " - " + attr.getMaximumRoll() + ")");
                        } else {
                            effectText = effectText.concat(" (T " + modifierTier + ")");
                        }
                        width = font.width("> " + effectText);
                    } else {
                        width = font.width("> " + effectText);
                    }

                    maxWidth = Math.max(maxWidth, width + 30);
                }
            } else {
                int width = font.width("> (Empty slot)");
                maxWidth = Math.max(maxWidth, width + 30);
            }
        }

        return maxWidth;
    }

    @Override
    public void renderText(@NotNull Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f, MultiBufferSource.@NotNull BufferSource pBufferSource) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean isAltDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);

        int usedSockets = (int) this.cmp.gearSocket().stream().filter(socket -> socket.runegem().isPresent()).count();
        int totalSockets = this.cmp.gearSocket().size();
        pFont.drawInBatch(getSocketDesc().copy().withStyle(ChatFormatting.GRAY).append(Component.literal("[" + usedSockets + "/" + totalSockets + "]").withStyle(ChatFormatting.DARK_GRAY)), pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        pY += 15;

        // Sort by modifiers applied/not
        Map<Boolean, List<GearSocket>> partitioned = this.cmp.gearSocket().stream()
                .collect(Collectors.partitioningBy(socket -> socket.modifier().isPresent() && socket.runegem().isPresent()));


        List<GearSocket> sortedSockets = getSortedSockets(partitioned.get(true));

        for (GearSocket socket : sortedSockets) {
            List<AbstractModifierEffect> effects = getModifierEffects(socket);
            int modifierTier = socket.modifier().map(m -> m.modifier().value().getTier()).orElse(0);

            for (int i = 0; i < effects.size(); i++) {
                AbstractModifierEffect eff = effects.get(i);

                pFont.drawInBatch(Component.literal(">"), pX + 20, pY - 1, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

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

                pFont.drawInBatch(cmp, pX + 30, pY - 1, ChatFormatting.GREEN.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                boolean isLast = (i == effects.size() - 1);
                pY += isLast ? 20 : 12;
            }
        }


        for (GearSocket ignored : partitioned.get(false)) {
            pFont.drawInBatch(Component.literal(">"), pX + 20, pY - 1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

            pFont.drawInBatch(Component.literal("(Empty slot)"), pX + 30, pY-1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            pY+= 20;
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        y+= 10;

        Map<Boolean, List<GearSocket>> partitioned = this.cmp.gearSocket().stream()
                .collect(Collectors.partitioningBy(socket -> socket.modifier().isPresent() && socket.runegem().isPresent()));

        List<GearSocket> sortedSockets = getSortedSockets(partitioned.get(true));

        for (GearSocket socket : sortedSockets) {
            List<AbstractModifierEffect> modifiers = getModifierEffects(socket);
            int val = modifiers.size();

            pose.pushPose();
            pose.translate(x, y, 0);

            ItemStack fakeStack = new ItemStack(ModItems.RUNEGEM.get());
            fakeStack.set(ModDataComponentType.RUNEGEM_DATA, socket.runegem().isPresent() ? socket.runegem().get() : null); // why ij, it's always present
            fakeStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            guiGraphics.renderFakeItem(fakeStack, 0,0);
            pose.popPose();

            y+= SOCKET_LINE_HEIGHT;

            for (int i = 1; i < val; i++) {
                y+= 12;
            }
        }

        for (GearSocket socket : partitioned.get(false)) {
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


    /*------ Helpers ------*/

    // Get all ModifierEffects of a GearSocket
    private List<AbstractModifierEffect> getModifierEffects(GearSocket socket) {
        return socket.modifier().map(m -> m.modifier().value().getModifierEffects()).orElse(List.of());
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
