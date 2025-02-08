package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.gui.menu.RuneAnvilMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, DimensionDelvers.MODID);

    public static final Supplier<MenuType<RuneAnvilMenu>> RUNE_ANVIL_MENU = MENUS.register("rune_anvil_menu", () -> new MenuType<>((containerId, playerInventory) -> new RuneAnvilMenu(containerId, playerInventory), FeatureFlags.DEFAULT_FLAGS));
}
