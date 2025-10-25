package com.wanderersoftherift.wotr.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.client.WotrKeyMappings;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import com.wanderersoftherift.wotr.modifier.Modifier;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import com.wanderersoftherift.wotr.util.ColorUtil;
import com.wanderersoftherift.wotr.util.TextureUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RunegemTooltipRenderer implements ClientTooltipComponent {
    private final RunegemComponent cmp;

    // ???
    private final ResourceLocation tierLocation;
    private final ResourceLocation shapeLocation;
    private final Dimension tierDimensions;
    private final Dimension shapeDimensions;

    private static int currentIndex;
    private static int maxIndex = -1;


    private static final int LINE_SPACING = 10;

    public RunegemTooltipRenderer(RunegemComponent cmp) {
        this.cmp = cmp;
        this.tierLocation = getTierResourceLocation(this.cmp.data.tier());
        this.shapeLocation = getShapeResourceLocation(this.cmp.data.shape());
        this.tierDimensions = new Dimension(TextureUtils.getTextureWidthGL(this.tierLocation), TextureUtils.getTextureHeightGL(this.tierLocation));
        this.shapeDimensions = new Dimension(TextureUtils.getTextureWidthGL(this.shapeLocation), TextureUtils.getTextureHeightGL(this.shapeLocation));

        if (maxIndex == -1) {
            maxIndex = getModifierGroups(cmp.runegem).size();
        }
    }


    @Override
    public int getHeight(@NotNull Font font) {
        int height = LINE_SPACING * 3; // base height for things that always display

        // Spacing for the modifiers themselves
        height += this.cmp.data.modifierLists().get(currentIndex).modifiers().size() * LINE_SPACING;

        // Spacing for the scroll visualization
        if (this.cmp.data.modifierLists().size() > 1) {
            height += LINE_SPACING;
        }

        // Spacing for the Info tooltip
        if (!ModifierRenderHelper.isKeyDown()) {
            height += LINE_SPACING;
        }

        return height;
    }

    // TODO: optimize
    @Override
    public int getWidth(@NotNull Font font) {
        int width = 0;

        width = Math.max(width, font.width(Component.translatable(WanderersOfTheRift.translationId("tooltip", "runegem.modifiers"))));
        width = Math.max(width, font.width(Component.translatable("tooltip." + WanderersOfTheRift.MODID + ".show_extra_info", WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName().getString())));
        RunegemData.ModifierGroup group = this.cmp.data.modifierLists().get(currentIndex);

        ResourceLocation socketable = getSocketable(getModifierGroups(this.cmp.runegem).get(currentIndex));
        int socketableWidth = TextureUtils.getTextureWidthGL(socketable);
        int imageSpacing = 10; // total spacing for the rendered images

        width = Math.max(width, tierDimensions.width + shapeDimensions.width + socketableWidth + imageSpacing);

        for (TieredModifier tieredModifier : group.modifiers()) {

            Holder<Modifier> mod = tieredModifier.modifier();
            int tier = tieredModifier.tier();

            if (mod.getKey() == null) {
                continue;
            }

            MutableComponent cmp = Component.literal("> ");
            cmp.append(Component.literal("[T" + tier + "] "));
            cmp.append(Component
                    .translatable(WanderersOfTheRift.translationId("modifier", mod.getKey().location())).withStyle(mod.value().getStyle()));


            if (ModifierRenderHelper.isKeyDown()) {
                String tierInfo = "";

                List<ModifierEffect> modifierEffects = mod.value().getModifierTier(tier);
                for (ModifierEffect effect : modifierEffects) {
                    tierInfo = getTierInfoString(effect, tier);
                }

                cmp.append(Component.literal(tierInfo));
            }

            width = Math.max(width, font.width(cmp));
        }

        return width;
    }

    @Override
    public void renderText(@NotNull Font font, int x, int y, @NotNull Matrix4f matrix, MultiBufferSource.@NotNull BufferSource bufferSource) {
        y += LINE_SPACING;
        int lightCoords = 15728880;
        int bgColor = 0;
        if (!ModifierRenderHelper.isKeyDown()) {
            font.drawInBatch(
                    Component.translatable(WanderersOfTheRift.translationId("tooltip", "show_extra_info"),
                            WotrKeyMappings.SHOW_TOOLTIP_INFO.getKey().getDisplayName().getString()).withStyle(ChatFormatting.DARK_GRAY),
                    x, y, ColorUtil.WHITE, true, matrix, bufferSource,
                    Font.DisplayMode.NORMAL, bgColor, lightCoords);
            y+= LINE_SPACING;
        }

        font.drawInBatch(Component.translatable(WanderersOfTheRift.translationId("tooltip", "runegem.modifiers")), x, y, ColorUtil.WHITE, true, matrix, bufferSource, Font.DisplayMode.NORMAL, bgColor, lightCoords);
         y += LINE_SPACING;

        RunegemData.ModifierGroup group = this.cmp.data.modifierLists().get(currentIndex);
        List<TieredModifier> mods = new ArrayList<>(group.modifiers());
        mods.sort(Comparator.comparing(mod -> mod.getName().getString())); // Sort alphabetically


        for (TieredModifier tieredModifier : mods) {
            Holder<Modifier> mod = tieredModifier.modifier();
            int tier = tieredModifier.tier();

            if (mod.getKey() == null) {
                continue;
            }

            MutableComponent cmp = Component.literal("> ").withStyle(ChatFormatting.DARK_GRAY);
            cmp.append(Component.literal("[T" + tier + "] ").withStyle(mod.value().getStyle()));
            cmp.append(Component
                    .translatable(WanderersOfTheRift.translationId("modifier", mod.getKey().location())).withStyle(mod.value().getStyle()));

            if (ModifierRenderHelper.isKeyDown()) {
                String tierInfo = "";

                List<ModifierEffect> modifierEffects = mod.value().getModifierTier(tier);
                for (ModifierEffect effect : modifierEffects) {
                    tierInfo = getTierInfoString(effect, tier);
                }

                cmp.append(Component.literal(tierInfo).withStyle(ChatFormatting.DARK_GRAY));
            }

            font.drawInBatch(
                    cmp,
                    x, y, ColorUtil.WHITE, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880
            );
            y += LINE_SPACING;
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphics guiGraphics) {
        ResourceLocation socketable = getSocketable(getModifierGroups(this.cmp.runegem).get(currentIndex));
        int socketableHeight = TextureUtils.getTextureHeightGL(socketable); // TODO necessary?
        int socketableWidth = TextureUtils.getTextureWidthGL(socketable);

        int horizontalSpacing = 5; // spacing between the rendered images

        guiGraphics.blit(RenderType.GUI_TEXTURED, tierLocation, x, y, 0, 0, tierDimensions.width, tierDimensions.height, tierDimensions.width, tierDimensions.height);
        guiGraphics.blit(RenderType.GUI_TEXTURED, shapeLocation, x + horizontalSpacing + tierDimensions.width, y, 0, 0, shapeDimensions.width, shapeDimensions.height, shapeDimensions.width, shapeDimensions.height);
        guiGraphics.blit(RenderType.GUI_TEXTURED, socketable, x + (horizontalSpacing * 2) + tierDimensions.width + shapeDimensions.width, y, 0, 0, socketableWidth, socketableHeight, socketableWidth, socketableHeight);

        // y-spacing for all the modifiers
        y += (LINE_SPACING * this.cmp.data.modifierLists().get(currentIndex).modifiers().size()) + 30;

        if (!ModifierRenderHelper.isKeyDown()) {
            y += LINE_SPACING;
        }

        if (maxIndex <= 1) {
            return; // no need to render dots
        }


        int dotSize = 4;
        int gap = 3;
        int shadowOffset = 1;
        int totalWidth = maxIndex * dotSize + (maxIndex - 1) * gap;
        int startX = x + (width - totalWidth) / 2;
        int dotY = y;

        for (int i = 0; i < maxIndex; i++) {
            int dotX = startX + i * (dotSize + gap);

            int selectedSize = dotSize + 2;
            int halfDiff = (selectedSize - dotSize) / 2;

            // ARGB
            int shadowColor = 0xFF2E2E2E;
            int selectedColor = 0xFFAAAAAA;
            int inactiveColor = 0xFF555555;

            guiGraphics.fill(dotX + shadowOffset, dotY + shadowOffset, dotX + dotSize + shadowOffset, dotY + dotSize + shadowOffset, shadowColor);

            if (i == currentIndex) {
                guiGraphics.fill(dotX - halfDiff + shadowOffset, dotY - halfDiff + shadowOffset,
                        dotX + dotSize + halfDiff + shadowOffset, dotY + dotSize + halfDiff + shadowOffset,
                        shadowColor);

                guiGraphics.fill(dotX - 1, dotY - 1, dotX + dotSize + 1, dotY + dotSize + 1, selectedColor);

            } else {
                guiGraphics.fill(dotX, dotY, dotX + dotSize, dotY + dotSize, inactiveColor);
            }
        }

    }

    /* --- Helpers --- */
    private static List<RunegemData.ModifierGroup> getModifierGroups(ItemStack stack) {
        if (stack != null && stack.has(WotrDataComponentType.RUNEGEM_DATA)) {
            RunegemData gemData = stack.get(WotrDataComponentType.RUNEGEM_DATA);
            if (gemData != null) {
                return gemData.modifierLists();
            }
        }

        return null;
    }

    private static String getTierInfoString(ModifierEffect effect, int tier) {
        if (effect instanceof AttributeModifierEffect attr) {
            return attr.getTierInfoString(tier);
        }
        return " (T " + tier + ")";
    }

    private static ResourceLocation getSocketable(RunegemData.ModifierGroup modifierGroup) {
        if (modifierGroup.supportedItems().unwrapKey().isPresent()) {
            return WanderersOfTheRift.id("textures/tooltip/runegem/socketable/" + modifierGroup.supportedItems().unwrapKey().get().location().getPath() + ".png");
        }
        return null;
    }

    private static ResourceLocation getTierResourceLocation(RunegemTier tier) {
        return WanderersOfTheRift.id("textures/tooltip/runegem/tier/" + tier.getName() + ".png");
    }

    private static ResourceLocation getShapeResourceLocation(RunegemShape shape) {
        return WanderersOfTheRift.id("textures/tooltip/runegem/shape/text/" + shape.getName() + ".png");
    }


    public static class RunegemMouseActions implements ItemSlotMouseAction {
        private final ScrollWheelHandler scrollWheelHandler;

        public RunegemMouseActions() {
            this.scrollWheelHandler = new ScrollWheelHandler();
        }
        @Override
        public boolean matches(Slot slot) {
            return slot.getItem().is(WotrItems.RUNEGEM) && slot.getItem().has(WotrDataComponentType.RUNEGEM_DATA);
        }

        @Override
        public boolean onMouseScrolled(double xOffset, double yOffset, int index, @NotNull ItemStack itemStack) {
            List<RunegemData.ModifierGroup> groups = getModifierGroups(itemStack);

            if (groups == null || groups.isEmpty()) {
                return false;
            }

            maxIndex = groups.size();
            Vector2i scroll = this.scrollWheelHandler.onMouseScroll(xOffset, yOffset);
            int direction = scroll.y == 0 ? -scroll.x : scroll.y;

            if(direction == 0) {
                return false;
            }

            int nextIndex = ScrollWheelHandler.getNextScrollWheelSelection(direction, currentIndex, maxIndex);
            if (nextIndex != currentIndex) {
                currentIndex = nextIndex;
                return true;
            }

            return false;
        }

        @Override
        public void onStopHovering(@NotNull Slot slot) {
            RunegemTooltipRenderer.currentIndex = 0;
            RunegemTooltipRenderer.maxIndex = -1;
        }

        @Override
        public void onSlotClicked(@NotNull Slot slot, @NotNull ClickType clickType) {

        }
    }

    public record RunegemComponent(ItemStack runegem, RunegemData data) implements TooltipComponent {
    }
}
