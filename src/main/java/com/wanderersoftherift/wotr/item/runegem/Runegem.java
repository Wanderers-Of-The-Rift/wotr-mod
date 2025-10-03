package com.wanderersoftherift.wotr.item.runegem;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.tooltip.ImageComponent;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.modifier.TieredModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Runegem extends Item {
    public static final Map<RunegemTier, ResourceLocation> TIER_RESOURCE_LOCATION_MAP = Map.of(RunegemTier.RAW,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/raw.png"), RunegemTier.SHAPED,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/shaped.png"), RunegemTier.CUT,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/cut.png"), RunegemTier.POLISHED,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/polished.png"), RunegemTier.FRAMED,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/framed.png"), RunegemTier.UNIQUE,
            WanderersOfTheRift.id("textures/tooltip/runegem/tier/unique.png"));

    public Runegem(Properties properties) {
        super(properties);
    }

    private Holder<Enchantment> getRandomModifier(ServerLevel serverLevel, TagKey<Enchantment> tag) {
        return serverLevel.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(serverLevel.random))
                .orElse(null);
    }

    @Override
    public Component getName(ItemStack stack) {
        RunegemData gemData = stack.get(WotrDataComponentType.RUNEGEM_DATA);
        if (gemData != null) {
            return gemData.name();
        }
        return super.getName(stack);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.has(WotrDataComponentType.RUNEGEM_DATA)) {
            RunegemData gemData = stack.get(WotrDataComponentType.RUNEGEM_DATA);
            ImageComponent fancyComponent = new ImageComponent(Component.empty(),
                    TIER_RESOURCE_LOCATION_MAP.get(Objects.requireNonNull(gemData).tier()));
            return Optional.of(fancyComponent);
        }

        return super.getTooltipImage(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        if (stack.has(WotrDataComponentType.RUNEGEM_DATA)) {
            RunegemData gemData = stack.get(WotrDataComponentType.RUNEGEM_DATA);
            if (gemData != null) {
                tooltipComponents.add(Component.translatable("tooltip.wotr.runegem.shape", gemData.shape().name()));
                tooltipComponents.add(Component.translatable("tooltip.wotr.runegem.modifiers"));
                for (RunegemData.ModifierGroup group : gemData.modifierLists()) {
                    group.supportedItems()
                            .unwrapKey()
                            .ifPresent(tagKey -> tooltipComponents
                                    .add(Component.literal(tagKey.toString()).withStyle(ChatFormatting.AQUA)));
                    for (TieredModifier tieredModifier : group.modifiers()) {
                        MutableComponent cmp = (MutableComponent) tieredModifier.getName();
                        tooltipComponents.add(
                                cmp.withStyle(tieredModifier.modifier().value().getStyle()));
                    }
                }
            }
        }
    }
}
