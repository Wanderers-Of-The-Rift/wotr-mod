package com.wanderersoftherift.wotr.gui.menu.character;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.MenuType;

public record CharacterMenuItem(Component name, MenuType<?> menuType, MenuConstructor menuSupplier) {

}
