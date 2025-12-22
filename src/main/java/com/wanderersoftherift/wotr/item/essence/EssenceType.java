package com.wanderersoftherift.wotr.item.essence;

/**
 * Entries added to this enum will be added as essence items right now works like this: - Will create a new item with
 * the next id [name.toLowerCase() + _essence] - Will look for the next texture name [name.toLowerCase() + _essence] -
 * Will added to te creative tab menu - Will add the essence value (5) specified in the name - Will add a translation
 * for english naming it [name + space + Essence] - Will be added to a pool to appear in wooden chest in the rift - Will
 * add all the essence items specified here to the ESSENCE_ITEM tag.
 */
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
    DARK("Dark");

   // SECONDARY ESSENCE TYPES commented out temporarily for testing,
    // add in each as they get a use and or icon
   // FIRE("Fire"),
   // AIR("Air"),
   // ENERGY("Energy"),
   // ANIMAL("Animal"),
   // CRYSTAL("Crystal"),
   // METAL("Metal"),
   // FOOD("Food"),
   // SLIME("Slime"),
   // MIND("Mind"),
   // MECHA("Mecha"),
   // END("End"),
   // FLOW("Flow"),
   // FORM("Form"),
   // ORDER("Order"),
   // CHAOS("Chaos");

    public final String name;

    EssenceType(String name) {
        this.name = name;
    }
}
