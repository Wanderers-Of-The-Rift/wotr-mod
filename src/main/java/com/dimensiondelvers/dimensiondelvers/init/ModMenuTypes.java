package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.gui.menu.AbilityBagMenu;
import com.dimensiondelvers.dimensiondelvers.gui.menu.ComponentContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, DimensionDelvers.MODID);

    public static final Supplier<MenuType<AbilityBagMenu>> ABILITY_BAG_MENU = MENUS.register("ability_bag_menu", () -> new MenuType<>(AbilityBagMenu::new, FeatureFlags.DEFAULT_FLAGS));
}