package com.wanderersoftherift.wotr.item.runegem;

import com.wanderersoftherift.wotr.client.tooltip.RunegemTooltipRenderer;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Runegem extends Item {
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
    public @NotNull Component getName(ItemStack stack) {
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
            return Optional.of(new RunegemTooltipRenderer.RunegemComponent(gemData));
        }
        return super.getTooltipImage(stack);
    }
}
