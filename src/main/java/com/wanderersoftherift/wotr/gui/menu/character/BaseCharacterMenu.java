package com.wanderersoftherift.wotr.gui.menu.character;

import com.google.common.collect.ImmutableList;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
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
import java.util.stream.IntStream;

/**
 * BaseCharacterMenu provides the base for all character menus
 */
public abstract class BaseCharacterMenu extends AbstractContainerMenu {

    private static List<Holder<CharacterMenuItem>> characterMenuItems;

    protected BaseCharacterMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    /**
     * @param registryAccess
     * @return A consistently sorted list of character menu items to be displayed on each character screen
     */
    public static List<Holder<CharacterMenuItem>> getSortedMenuItems(RegistryAccess registryAccess) {
        if (characterMenuItems == null) {
            Registry<CharacterMenuItem> registry = registryAccess
                    .lookupOrThrow(WotrRegistries.Keys.CHARACTER_MENU_ITEMS);
            characterMenuItems = sortItems(registry.stream().map(registry::wrapAsHolder).toList());
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
    private static List<Holder<CharacterMenuItem>> sortItems(List<Holder<CharacterMenuItem>> items) {
        List<Holder<CharacterMenuItem>> open = new ArrayList<>(items);
        Set<MenuType<?>> closed = new HashSet<>();
        List<Holder<CharacterMenuItem>> result = new ArrayList<>();
        // Detect reference loops
        boolean changed = true;
        while (!open.isEmpty() && changed) {
            changed = false;

            Iterator<Holder<CharacterMenuItem>> iterator = open.iterator();
            while (iterator.hasNext()) {
                Holder<CharacterMenuItem> item = iterator.next();
                int insertAt = result.size();
                if (item.value().relativeTo() != null) {
                    if (!closed.contains(item.value().relativeTo())) {
                        continue;
                    }
                    int index = IntStream.range(0, result.size())
                            .filter(i -> result.get(i).value().menuType() == item.value().relativeTo())
                            .findFirst()
                            .getAsInt();
                    insertAt = item.value().orderHint().insertAt(index);
                }
                result.add(insertAt, item);
                closed.add(item.value().menuType());
                iterator.remove();
                changed = true;
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
