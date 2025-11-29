package com.wanderersoftherift.wotr.item.essence;

import java.util.Arrays;

public enum EssenceType {

    // USED ESSENCE TYPES
    EARTH(0,"Earth"),
    WATER(1,"Water"),
    PLANT(2,"Plant"),
    DEATH(3,"Death"),
    LIGHT(4,"Light"),
    NETHER(5,"Nether"),
    LIFE(6,"Life"),
    HONEY(7,"Honey"),
    MUSHROOM(8,"Mushroom"),
    FABRIC(9,"Fabric"),
    DARK(10,"Dark"),

    // SECONDARY ESSENCE TYPES
    FIRE(11, "Fire"),
    AIR(12, "Air"),
    ENERGY(13, "Energy"),
    ANIMAL(14, "Animal"),
    CRYSTAL(15, "Crystal"),
    METAL(16, "Metal"),
    FOOD(17, "Food"),
    SLIME(18, "Slime"),
    MIND(19, "Mind"),
    MECHA(20, "Mecha"),
    END(21, "End"),
    FLOW(22, "Flow"),
    FORM(23, "Form"),
    ORDER(24, "Order"),
    CHAOS(25, "Chaos");

    public final int id;
    public final String name;

    EssenceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public static EssenceType get(int id) {
        return Arrays.stream(EssenceType.values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElse(null);
    }

}
