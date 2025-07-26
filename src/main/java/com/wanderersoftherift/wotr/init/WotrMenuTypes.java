package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.AbilityBenchMenu;
import com.wanderersoftherift.wotr.gui.menu.KeyForgeMenu;
import com.wanderersoftherift.wotr.gui.menu.RiftCompleteMenu;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.gui.menu.character.GuildMenu;
import com.wanderersoftherift.wotr.gui.menu.character.QuestMenu;
import com.wanderersoftherift.wotr.gui.menu.character.WalletMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestGiverMenu;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestRewardMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrMenuTypes {
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

    /// Quest Menus

    public static final Supplier<MenuType<QuestGiverMenu>> QUEST_GIVER_MENU = MENUS.register("quest_giver_menu",
            () -> new MenuType<>(QuestGiverMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<QuestCompletionMenu>> QUEST_COMPLETION_MENU = MENUS.register(
            "quest_completion_menu", () -> new MenuType<>(QuestCompletionMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<QuestRewardMenu>> QUEST_REWARD_MENU = MENUS.register(
            "quest_reward_menu", () -> new MenuType<>(QuestRewardMenu::new, FeatureFlags.DEFAULT_FLAGS));

    /// Character Menus

    public static final Supplier<MenuType<GuildMenu>> GUILDS_MENU = MENUS.register("guilds_menu",
            () -> new MenuType<>(GuildMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<WalletMenu>> WALLET_MENU = MENUS.register("wallet_menu",
            () -> new MenuType<>(WalletMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final Supplier<MenuType<QuestMenu>> QUEST_MENU = MENUS.register("quest_menu",
            () -> new MenuType<>(QuestMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
