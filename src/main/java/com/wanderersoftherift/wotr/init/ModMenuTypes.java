package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import com.wanderersoftherift.wotr.gui.menu.KeyForgeMenu;
import com.wanderersoftherift.wotr.gui.menu.RiftCompleteMenu;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,
            WanderersOfTheRift.MODID);

    public static final Supplier<MenuType<RuneAnvilMenu>> RUNE_ANVIL_MENU = MENUS.register("rune_anvil_menu",
            () -> new MenuType<>(RuneAnvilMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<KeyForgeMenu>> KEY_FORGE_MENU = MENUS.register("key_forge_menu",
            () -> new MenuType<>(KeyForgeMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<AbilityBenchMenu>> ABILITY_BENCH_MENU = MENUS.register("ability_bench_menu",
            () -> new MenuType<>(AbilityBenchMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<RiftCompleteMenu>> RIFT_COMPLETE_MENU = MENUS.register("rift_complete_menu",
            () -> new MenuType<>(RiftCompleteMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
