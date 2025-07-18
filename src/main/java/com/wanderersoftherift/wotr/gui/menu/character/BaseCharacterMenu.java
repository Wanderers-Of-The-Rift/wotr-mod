package com.wanderersoftherift.wotr.gui.menu.character;

import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class BaseCharacterMenu extends AbstractContainerMenu {

    private static final List<CharacterMenuItem> ITEMS = new ArrayList<>();

    protected BaseCharacterMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    public static List<CharacterMenuItem> getSortedMenuItems(RegistryAccess registryAccess) {
        if (ITEMS.isEmpty()) {
            ITEMS.addAll(sortItems(
                    registryAccess.lookupOrThrow(WotrRegistries.Keys.CHARACTER_MENU_ITEMS).stream().toList()));
        }
        return ITEMS;
    }

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
        return result;
    }
}
