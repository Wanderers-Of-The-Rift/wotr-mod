package com.dimensiondelvers.dimensiondelvers.item.runegem;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.client.tooltip.ImageTooltipRenderer;
import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class Runegem extends Item {
    public static final Map<RunegemTier, ResourceLocation> TIER_RESOURCE_LOCATION_MAP =
            Map.of(
                    RunegemTier.RAW, DimensionDelvers.id("textures/tooltip/runegem/raw.png"),
                    RunegemTier.SHAPED, DimensionDelvers.id("textures/tooltip/runegem/shaped.png"),
                    RunegemTier.CUT, DimensionDelvers.id("textures/tooltip/runegem/cut.png"),
                    RunegemTier.POLISHED, DimensionDelvers.id("textures/tooltip/runegem/polished.png"),
                    RunegemTier.FRAMED, DimensionDelvers.id("textures/tooltip/runegem/framed.png"),
                    RunegemTier.UNIQUE, DimensionDelvers.id("textures/tooltip/runegem/unique.png")
            );

    public Runegem(Properties properties) {
        super(properties);
    }

    private Holder<Enchantment> getRandomModifier(ServerLevel serverLevel, TagKey<Enchantment> tag) {
        return serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(serverLevel.random))
                .orElse(null);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if(stack.has(ModDataComponentType.RUNEGEM_DATA)) {
            RunegemData gemData = stack.get(ModDataComponentType.RUNEGEM_DATA);
            ImageTooltipRenderer.ImageComponent fancyComponent =
                    new ImageTooltipRenderer.ImageComponent(stack, Component.empty(), TIER_RESOURCE_LOCATION_MAP.get(Objects.requireNonNull(gemData).tier()));
            return Optional.of(fancyComponent);
        }

        return super.getTooltipImage(stack);
    }

}
