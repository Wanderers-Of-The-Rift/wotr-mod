package com.wanderersoftherift.wotr.item.essence;

public enum EssenceType {

    // USED ESSENCE TYPES
    EARTH("Earth"),
    WATER("Water"),
    PLANT("Plant"),
    DEATH("Death"),
    LIGHT("Light"),
    NETHER("Nether"),
    LIFE("Life"),
    HONEY("Honey"),
    MUSHROOM("Mushroom"),
    FABRIC("Fabric"),
    DARK("Dark"),

    // SECONDARY ESSENCE TYPES
    FIRE("Fire"),
    AIR("Air"),
    ENERGY("Energy"),
    ANIMAL("Animal"),
    CRYSTAL("Crystal"),
    METAL("Metal"),
    FOOD("Food"),
    SLIME("Slime"),
    MIND("Mind"),
    MECHA("Mecha"),
    END("End"),
    FLOW("Flow"),
    FORM("Form"),
    ORDER("Order"),
    CHAOS("Chaos");

    public final String name;

    EssenceType(String name) {
        this.name = name;
    }
}
