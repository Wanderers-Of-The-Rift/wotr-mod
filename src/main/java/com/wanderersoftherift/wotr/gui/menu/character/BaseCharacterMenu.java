package com.wanderersoftherift.wotr.gui.menu.character;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * BaseCharacterMenu provides the base for all character menus
 */
public abstract class BaseCharacterMenu extends AbstractContainerMenu {

    private static List<CharacterMenuItem> characterMenuItems;

    protected BaseCharacterMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    /**
     * @param registryAccess
     * @return A consistently sorted list of character menu items to be displayed on each character screen
     */
    public static List<CharacterMenuItem> getSortedMenuItems(RegistryAccess registryAccess) {
        if (characterMenuItems == null) {
            characterMenuItems = sortItems(
                    registryAccess.lookupOrThrow(WotrRegistries.Keys.CHARACTER_MENU_ITEMS).stream().toList());
        }
        return characterMenuItems;
    }

    /**
     * Each item may define another item it should be placed relative to (before or after) - so the items form a partial
     * ordering. This method handles sorting the items to respect these placement requirements.
     * 
     * @param items
     * @return A sorted list of {@link CharacterMenuItem}
     */
    private static List<CharacterMenuItem> sortItems(List<CharacterMenuItem> items) {
        List<CharacterMenuItem> open = new ArrayList<>(items);
        Set<MenuType<?>> closed = new HashSet<>();
        List<CharacterMenuItem> result = new ArrayList<>();
        // Detect reference loops
        int remaining = Integer.MAX_VALUE;
        while (!open.isEmpty() && open.size() < remaining) {
            remaining = open.size();

            Iterator<CharacterMenuItem> iterator = open.iterator();
            while (iterator.hasNext()) {
                CharacterMenuItem item = iterator.next();
                if (item.orderHint() == OrderHint.NONE || item.relativeTo() == null) {
                    result.add(item);
                    closed.add(item.menuType());
                    iterator.remove();
                } else if (closed.contains(item.relativeTo())) {
                    int index = 0;
                    while (index < result.size()) {
                        if (result.get(index).menuType() == item.relativeTo()) {
                            break;
                        }
                        index++;
                    }
                    result.add((item.orderHint() == OrderHint.BEFORE ? index : index + 1), item);
                    closed.add(item.menuType());
                    iterator.remove();
                }
            }
        }
        result.addAll(open);
        return ImmutableList.copyOf(result);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
