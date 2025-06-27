package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.socket.GearSocket;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.ModifierInstance;
import com.wanderersoftherift.wotr.modifier.ModifierTier;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.util.ComponentUtil;
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
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO: Icons for modifiers
@SuppressWarnings("DataFlowIssue")
public class GearSocketTooltipRenderer implements ClientTooltipComponent {
    private static final int SOCKET_LINE_HEIGHT = 20;
    private static final Map<RunegemShape, ResourceLocation> SHAPE_TEXTURES = Map.of(
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
        int baseHeight = font.lineHeight + 2;

        if (!isKeyDown()) {
            baseHeight += font.lineHeight + 2;
        }

        int contentHeight = cmp.gearSocket()
                .stream()
                .mapToInt(socket -> SOCKET_LINE_HEIGHT * getModifierEffectsCount(socket))
                .sum();
        return baseHeight + contentHeight;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        boolean isShiftDown = isKeyDown();
        int used = (int) cmp.gearSocket().stream().filter(s -> s.runegem().isPresent()).count();

        int maxWidth = font.width(getSocketDesc().getString() + "[" + used + "/" + cmp.gearSocket().size() + "]");
        maxWidth = Math
                .max(maxWidth,
                        font.width(Component
                                .translatable("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info",
                                        WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName().getString())
                                .getString()));

        for (GearSocket socket : cmp.gearSocket()) {
            List<ModifierTier> tiers = getModifierTiers(socket);
            if (socket.modifier().isPresent() && socket.runegem().isPresent() && tiers != null) {
                int tier = socket.modifier().get().tier();
                ModifierTier modifier = tiers.get(tier - 1);
                for (AbstractModifierEffect effect : getModifierEffects(modifier)) {
                    String text = getEffectText(effect, tier, isShiftDown, socket.modifier().get().modifier().value());
                    maxWidth = Math.max(maxWidth, font.width("> " + text) + 30);
                }
            } else {
                maxWidth = Math.max(maxWidth, font.width("> "
                        + Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".empty_socket").getString())
                        + 30);
            }
        }
        return maxWidth;
    }

    @Override
    public void renderText(
            Font pFont,
            int pX,
            int pY,
            @NotNull Matrix4f pMatrix4f,
            MultiBufferSource.@NotNull BufferSource pBufferSource) {

        boolean isKeyDown = isKeyDown();

        if (!isKeyDown()) {
            pFont.drawInBatch(
                    Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info",
                            WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName().getString()),
                    pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource,
                    Font.DisplayMode.NORMAL, 0, 15_728_880);
            pY += pFont.lineHeight + 2;
        }

        pFont.drawInBatch(
                getSocketDesc().copy()
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component
                                .literal("[" + cmp.gearSocket().stream().filter(s -> s.runegem().isPresent()).count()
                                        + "/" + cmp.gearSocket().size() + "]")
                                .withStyle(ChatFormatting.DARK_GRAY)),
                pX, pY, ChatFormatting.DARK_GRAY.getColor(), true, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0,
                15_728_880);
        pY += 15;

        Map<Boolean, List<GearSocket>> partitioned = cmp.gearSocket()
                .stream()
                .collect(Collectors.partitioningBy(s -> s.modifier().isPresent() && s.runegem().isPresent()));
        List<GearSocket> sorted = getSortedSockets(partitioned.get(true));

        for (GearSocket socket : sorted) {
            int tier = socket.modifier().get().tier();
            ModifierTier modifier = getModifierTiers(socket).get(tier - 1);
            List<AbstractModifierEffect> effects = getModifierEffects(modifier);

            for (int i = 0; i < effects.size(); i++) {
                AbstractModifierEffect effect = effects.get(i);
                pFont.drawInBatch(Component.literal(">"), pX + 20, pY - 1, ChatFormatting.DARK_GRAY.getColor(), true,
                        pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 15_728_880);

                MutableComponent component = Component.literal("");
                Modifier mod = socket.modifier().get().modifier().value();

                var tooltip = effect.getTooltipComponent(ItemStack.EMPTY, socket.modifier().get().roll(), mod.getStyle()
                );
                if (tooltip instanceof ImageComponent img) {
                    if (tier == getModifierTiers(socket).size()) {
                        component.append(ComponentUtil.wavingComponent(img.base(), mod.getStyle().getColor().getValue(),
                                0.125f, 0.5f));
                    } else {
                        component.append(
                                img.base().copy().withStyle(mod.getStyle()));
                    }

                }
                if (isKeyDown) {
                    component.append(getTierInfo(effect, tier));
                }

                pFont.drawInBatch(component, pX + 30, pY - 1, ChatFormatting.GREEN.getColor(), true, pMatrix4f,
                        pBufferSource, Font.DisplayMode.NORMAL, 0, 15_728_880);
                pY += (i == effects.size() - 1) ? 20 : 12;
            }
        }

        for (GearSocket ignored : partitioned.get(false)) {
            pFont.drawInBatch(Component.literal(">"), pX + 20, pY - 1, 5_592_405, true, pMatrix4f, pBufferSource,
                    Font.DisplayMode.NORMAL, 0, 15_728_880);
            Component display;
            if (isKeyDown) {
                display = Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".empty_socket");
            } else {
                display = Component.literal("-");
            }
            pFont.drawInBatch(display, pX + 30, pY - 1,
                    isKeyDown ? 5_592_405 : TextColor.parseColor("#19191a").getOrThrow().getValue(), true, pMatrix4f,
                    pBufferSource, Font.DisplayMode.NORMAL, 0, 15_728_880);
            pY += 20;
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        y += 10;

        if (!isKeyDown()) {
            y += font.lineHeight + 2;
        }

        Map<Boolean, List<GearSocket>> partitioned = cmp.gearSocket()
                .stream()
                .collect(Collectors.partitioningBy(s -> s.modifier().isPresent() && s.runegem().isPresent()));
        List<GearSocket> sorted = getSortedSockets(partitioned.get(true));

        boolean painted = false;
        for (GearSocket socket : sorted) {
            ItemStack fakeStack = new ItemStack(WotrItems.RUNEGEM.get());
            fakeStack.set(WotrDataComponentType.RUNEGEM_DATA, socket.runegem().orElse(null));
            fakeStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            pose.pushPose();
            pose.translate(x, y, 0);
            guiGraphics.renderFakeItem(fakeStack, 0, 0);
            pose.popPose();

            if (painted) {
                drawSocketLine(guiGraphics, x, y, width);
            }

            painted = true;

            y += SOCKET_LINE_HEIGHT;
            y += 12 * (getModifierEffectsCount(socket) - 1);
        }

        painted = false;

        for (GearSocket socket : partitioned.get(false)) {
            guiGraphics.blit(RenderType.GUI_TEXTURED, SHAPE_TEXTURES.get(socket.shape()), x, y, 0, 0, 16, 16, 16, 16);
            if (painted) {
                drawSocketLine(guiGraphics, x, y, width);
            }
            painted = true;
            y += SOCKET_LINE_HEIGHT;
        }
    }

    public static Component getSocketDesc() {
        return Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".socket");
    }

    /*------ Helpers ------*/

    private static boolean isKeyDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),
                WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getValue());
    }

    private static void drawSocketLine(GuiGraphics gui, int x, int y, int width) {
        gui.fill(x + 20, y - 3, x + width - 10, y - 2, 0xFF383838);
        gui.fill(x + 21, y - 2, x + width - 9, y - 1, 0x4019191a);
    }

    private static List<AbstractModifierEffect> getModifierEffects(ModifierTier tier) {
        return tier.getModifierEffects();
    }

    private static List<ModifierTier> getModifierTiers(GearSocket socket) {
        return socket.modifier().map(inst -> inst.modifier().value().getModifierTierList()).orElse(null);
    }

    private static List<GearSocket> getSortedSockets(List<GearSocket> sockets) {
        if (sockets == null) {
            return List.of();
        } else {
            return sockets.stream()
                    .sorted(Comparator.comparingInt(
                            s -> Optional.ofNullable(getModifierTiers((GearSocket) s)).map(List::size).orElse(0))
                            .reversed())
                    .toList();
        }
    }

    private static int getModifierEffectsCount(GearSocket socket) {
        return Optional.ofNullable(getModifierTiers(socket)).map(tiers -> {
            int tier = socket.modifier().map(ModifierInstance::tier).orElse(0);
            if (tier > 0 && tier <= tiers.size()) {
                return tiers.get(tier - 1).getModifierEffects().size();
            }
            return 1;
        }).orElse(1);
    }

    private static String formatRoll(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static String getEffectText(
            AbstractModifierEffect effect,
            int tier,
            boolean isShiftDown,
            Modifier modifier) {
        String base;
        if (effect.getTooltipComponent(ItemStack.EMPTY, 0.0F, modifier.getStyle()) instanceof ImageComponent img) {
            base = img.base().getString();
        } else {
            base = "";
        }

        if (!isShiftDown) {
            return base;
        }

        return base + getTierInfoString(effect, tier);
    }

    private static String getTierInfoString(AbstractModifierEffect effect, int tier) {
        if (effect instanceof AttributeModifierEffect attr) {
            return " (T" + tier + " : " + formatRoll(attr.getMinimumRoll()) + " - " + formatRoll(attr.getMaximumRoll())
                    + ")";
        }
        return " (T " + tier + ")";
    }

    private static Component getTierInfo(AbstractModifierEffect effect, int tier) {
        return Component.literal(getTierInfoString(effect, tier)).withStyle(ChatFormatting.DARK_GRAY);
    }

    public record GearSocketComponent(ItemStack socketed, List<GearSocket> gearSocket) implements TooltipComponent {
    }
}
