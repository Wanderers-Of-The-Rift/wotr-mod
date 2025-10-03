package com.wanderersoftherift.wotr.modifier.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;


public class EnchantmentModifierEffect extends AbstractModifierEffect {

    private final ResourceKey<Enchantment> enchantmentKey;

    public static final MapCodec<EnchantmentModifierEffect> MODIFIER_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantmentModifierEffect::getId),
                    ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(EnchantmentModifierEffect::getEnchantmentKey),
                    Codec.DOUBLE.fieldOf("min_roll").forGetter(EnchantmentModifierEffect::getMinimumRoll),
                    Codec.DOUBLE.fieldOf("max_roll").forGetter(EnchantmentModifierEffect::getMaximumRoll)
            ).apply(instance, EnchantmentModifierEffect::new)
    );

    private final ResourceLocation id;
    private final double minRoll;
    private final double maxRoll;

    public EnchantmentModifierEffect(ResourceLocation id, ResourceKey<Enchantment> enchantmentKey, double minRoll,
            double maxRoll) {
        this.id = id;
        this.enchantmentKey = enchantmentKey;
        this.minRoll = minRoll;
        this.maxRoll = maxRoll;

// DEBUG LOG
        WanderersOfTheRift.LOGGER.info(
                "[DEBUG] Loaded EnchantmentModifierEffect: {} -> {} (min={}, max={})",
                id,
                enchantmentKey.location(),
                minRoll,
                maxRoll
        );
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceKey<Enchantment> getEnchantmentKey() {
        return enchantmentKey;
    }

    public double getMinimumRoll() {
        return minRoll;
    }

    public double getMaximumRoll() {
        return maxRoll;
    }

    @Override
    public MapCodec<? extends AbstractModifierEffect> getCodec() {
        return MODIFIER_CODEC;
    }

    public int calculateLevel(double roll) {
        // Map [0..1] roll into [minRoll..maxRoll], then clamp to integer level bounds
        double lerped = Mth.lerp(Mth.clamp((float) roll, 0.0F, 1.0F), minRoll, maxRoll);
        int raw = Mth.floor(lerped + 0.5D);

        // In our registrations we set minRoll=1.0 and maxRoll=<enchant max level>,
        // so the valid bounds are exactly these two values.
        int min = Math.max(1, (int) Math.round(minRoll));
        int max = Math.max(min, (int) Math.round(maxRoll));

        return Mth.clamp(raw, min, max);
    }



    @Override
    public void enableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        // Intentionally left as a stub to mirror structure without making assumptions about ModifierSource.
        // Recommended implementation (1.21.4+):
        // 1) Obtain the ItemStack this modifier should apply to from `source`.
        // 2) Read/modify its DataComponents.ENCHANTMENTS (ItemEnchantments) to set `enchantment` to
        // `calculateLevel(roll)`.
        // 3) Write the updated ItemEnchantments back to the stack.
    }

    @Override
    public void disableModifier(double roll, Entity entity, ModifierSource source, int effectIndex) {
        // Likewise, remove or downgrade the enchantment on the target ItemStack, if appropriate.
    }

    @Override
    public TooltipComponent getTooltipComponent(ItemStack stack, float roll, Style style) {
        int level = calculateLevel(roll);
        MutableComponent cmp = Component.translatable(
                "modifier." + WanderersOfTheRift.MODID + ".enchantment.add",
                enchantmentKey.location().toString(),
                level
        );

        if (style != null) {
            cmp = cmp.withStyle(style);
        }
        // Reuse an existing tooltip image if you don't have a dedicated one yet.
        // Update to a dedicated enchantment texture when available.
        ResourceLocation icon = WanderersOfTheRift.id("textures/tooltip/attribute/damage_attribute.png");
        return new ImageComponent(stack, cmp, icon);
    }
}
