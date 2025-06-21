package com.wanderersoftherift.wotr.gui.menu.status;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;

public record StatusMenuItem(Component name, MenuType<?> menuType, MenuConstructor menuSupplier) {

}
