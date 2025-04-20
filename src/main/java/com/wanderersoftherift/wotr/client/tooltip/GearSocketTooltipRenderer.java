package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.upgrade.AbilityUpgradePool;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final GearSocketComponent cmp;
    private static final int SOCKET_LINE_HEIGHT = 20;


    public GearSocketTooltipRenderer(GearSocketComponent cmp) {
        this.cmp = cmp;
    }

    @Override
    public int getHeight(@NotNull Font font) {
        int e = font.lineHeight + 2 + (this.cmp.gearSocket.size() * 20);

        for (GearSocket s : this.cmp.gearSocket) {
            if(s.modifier().isPresent()) {
                int v = s.modifier().get().modifier().value().getModifierEffects().size();

                for (int i = 1; i < v; i++) {
                    e+= SOCKET_LINE_HEIGHT;
                }
            }

        }

        return e;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        int maxWidth = 0;
        for (GearSocket socket : this.cmp.gearSocket) {
            maxWidth += 20; // For each available socket +16 w/ 4px between each

        }
        return maxWidth + font.width(getSocketDesc()) + 90; //todo: temp
    }

    @Override
    public void renderText(@NotNull Font pFont, int pX, int pY, @NotNull Matrix4f pMatrix4f, MultiBufferSource.@NotNull BufferSource pBufferSource) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
        boolean isAltDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT);

        if (!isShiftDown) {
            pFont.drawInBatch(getSocketDesc(), pX, pY, ChatFormatting.GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

        } else {
            int count = (int) this.cmp.gearSocket().stream()
                    .filter(socket -> socket.runegem().isPresent())
                    .count();
            int total = this.cmp.gearSocket().size();

            pFont.drawInBatch(getSocketDesc().copy().append(" (" + count + "/" + total + ")"), pX, pY, ChatFormatting.GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        }

        pY += 15;

        Map<Boolean, List<GearSocket>> partitioned = this.cmp.gearSocket().stream()
                .collect(Collectors.partitioningBy(socket -> socket.modifier().isPresent() && socket.runegem().isPresent()));

        for (GearSocket socket : partitioned.get(true)) {
            int i = 0; //temp
            for(AbstractModifierEffect eff : socket.modifier().get().modifier().value().getModifierEffects()) {

                char symb = i > 0 ? '↳' : '→';
                pFont.drawInBatch(Component.literal(String.valueOf(symb)), pX + 20, pY-1, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                MutableComponent cmp = Component.literal("");

                TooltipComponent c = eff.getTooltipComponent(ItemStack.EMPTY, socket.modifier().get().roll(), ChatFormatting.AQUA); //lmao

                if(c instanceof ImageComponent img) {
                    cmp.append(img.base());
                }

                if(isShiftDown) {
                    if(eff instanceof AttributeModifierEffect attr) {
                        cmp.append(Component.literal(" (Tier ?: " + attr.getMinimumRoll() + " - " + attr.getMaximumRoll() + ")").withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        cmp.append(Component.literal(" (Tier ?)").withStyle(ChatFormatting.DARK_GRAY));
                    }
                }

                pFont.drawInBatch(cmp, pX + 30, pY-1, ChatFormatting.GREEN.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY+= 20;

                i++;
            }
        }

        for (GearSocket ignored : partitioned.get(false)) {
            pFont.drawInBatch(Component.literal("(Empty slot)"), pX + 22, pY-1, 5592405, true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
            pY+= 20;
        }

    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();

        y+= 10;

        Map<Boolean, List<GearSocket>> partitioned = this.cmp.gearSocket().stream()
                .collect(Collectors.partitioningBy(socket -> socket.modifier().isPresent() && socket.runegem().isPresent()));

        for (GearSocket socket : partitioned.get(true)) {
            pose.pushPose();
            pose.translate(x, y, 0);

            ItemStack fakeStack = new ItemStack(ModItems.RUNEGEM.get());
            fakeStack.set(ModDataComponentType.RUNEGEM_DATA, socket.runegem().isPresent() ? socket.runegem().get() : null); // why ij, it's always present
            fakeStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            guiGraphics.renderFakeItem(fakeStack, 0,0);
            pose.popPose();
            y+= SOCKET_LINE_HEIGHT;

            int v = socket.modifier().get().modifier().value().getModifierEffects().size();

            for (int i = 1; i < v; i++) {
                y+= SOCKET_LINE_HEIGHT;
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
        return Component.translatable("tooltip."+ WanderersOfTheRift.MODID +".socket");
    }



    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {}
}
