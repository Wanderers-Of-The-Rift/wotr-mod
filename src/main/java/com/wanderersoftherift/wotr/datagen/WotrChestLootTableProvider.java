package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.init.WotrTags;
import com.wanderersoftherift.wotr.loot.functions.AbilityHolderFunction;
import com.wanderersoftherift.wotr.loot.functions.GearSocketsFunction;
import com.wanderersoftherift.wotr.loot.functions.RollGearFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

import static com.wanderersoftherift.wotr.loot.predicates.RiftLevelCheck.riftTier;

public record WotrChestLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        generateGeodeRunegemLootTable(consumer);
        generateMonsterRunegemLootTable(consumer);
        generateAbilityLootTable(consumer);
        generateSocketedVanillaArmorLootTable(consumer);
        generateSocketedVanillaWeaponLootTable(consumer);
        generateSocketedVanillaToolLootTable(consumer);
        consumer.accept(getResourceKey("chests/wooden"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(UniformGenerator.between(6.0F, 10.0F))
                                .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                        .setWeight(40)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/geode_runegem"))
                                        .setWeight(20))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/ability")).setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_armor"))
                                        .setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_weapons"))
                                        .setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_tools"))
                                        .setWeight(5))
                                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.POTION)
                                        .setWeight(20)
                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                .add(LootItem.lootTableItem(Items.BREAD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(10))
                                .add(LootItem.lootTableItem(WotrItems.SKILL_THREAD)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .add(LootItem.lootTableItem(WotrItems.SKILL_THREAD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))))
                                .add(LootItem.lootTableItem(WotrItems.SKILL_THREAD)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))))

                ));
        consumer.accept(getResourceKey("chests/rift_spawner/default"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                        .setWeight(30)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/monster_runegem"))
                                        .setWeight(25))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/ability")).setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_armor"))
                                        .setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_weapons"))
                                        .setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_tools"))
                                        .setWeight(5))
                                .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(20))
                                .add(LootItem.lootTableItem(Items.POTION)
                                        .setWeight(20)
                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                .add(LootItem.lootTableItem(Items.COOKED_BEEF).setWeight(20))
                                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(10))

                ));
    }

    private void generateGeodeRunegemLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/geode_runegem"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(WotrItems.RAW_RUNEGEM_GEODE).when(riftTier().max(3)).setWeight(16))
                        .add(LootItem.lootTableItem(WotrItems.SHAPED_RUNEGEM_GEODE).when(riftTier(2, 4)).setWeight(8))
                        .add(LootItem.lootTableItem(WotrItems.CUT_RUNEGEM_GEODE).when(riftTier(3, 5)).setWeight(4))
                        .add(LootItem.lootTableItem(WotrItems.POLISHED_RUNEGEM_GEODE)
                                .when(riftTier().min(4))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(WotrItems.FRAMED_RUNEGEM_GEODE)
                                .when(riftTier().min(5))
                                .setWeight(1))));
    }

    private void generateMonsterRunegemLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/monster_runegem"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(WotrItems.RAW_RUNEGEM_MONSTER)
                                .when(riftTier().max(3))
                                .setWeight(16))
                        .add(LootItem.lootTableItem(WotrItems.SHAPED_RUNEGEM_MONSTER).when(riftTier(2, 4)).setWeight(8))
                        .add(LootItem.lootTableItem(WotrItems.CUT_RUNEGEM_MONSTER).when(riftTier(3, 5)).setWeight(4))
                        .add(LootItem.lootTableItem(WotrItems.POLISHED_RUNEGEM_MONSTER)
                                .when(riftTier().min(4))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(WotrItems.FRAMED_RUNEGEM_MONSTER)
                                .when(riftTier().min(5))
                                .setWeight(1))));
    }

    private void generateAbilityLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        HolderLookup.RegistryLookup<Ability> reg = registries.lookupOrThrow(WotrRegistries.Keys.ABILITIES);
        consumer.accept(getResourceKey("rift/ability"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(WotrItems.ABILITY_HOLDER)
                                        .when(riftTier().max(2))
                                        .setWeight(16)
                                        .apply(AbilityHolderFunction.setAbilityOptions(1, 1,
                                                reg.getOrThrow(WotrTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(WotrItems.ABILITY_HOLDER)
                                        .when(riftTier(1, 3))
                                        .setWeight(8)
                                        .apply(AbilityHolderFunction.setAbilityOptions(1, 3,
                                                reg.getOrThrow(WotrTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(WotrItems.ABILITY_HOLDER)
                                        .when(riftTier(3, 5))
                                        .setWeight(4)
                                        .apply(AbilityHolderFunction.setAbilityOptions(3, 6,
                                                reg.getOrThrow(WotrTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(WotrItems.ABILITY_HOLDER)
                                        .when(riftTier().min(4))
                                        .setWeight(2)
                                        .apply(AbilityHolderFunction.setAbilityOptions(5, 7,
                                                reg.getOrThrow(WotrTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(WotrItems.ABILITY_HOLDER)
                                        .when(riftTier().min(5))
                                        .setWeight(1)
                                        .apply(AbilityHolderFunction.setAbilityOptions(7, 10,
                                                reg.getOrThrow(WotrTags.Abilities.RIFT_DROPS))))
                ));
    }

    /**
     * Generate a loot table for socketed vanilla armor. This table only contains helmets as the function that rolls
     * sockets currently check specific tags and rerolls the item types
     * 
     * @param consumer
     */
    private void generateSocketedVanillaArmorLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/socketed_vanilla_armor"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                // rogue type gear
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, WotrTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, WotrTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, WotrTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, WotrTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                // tank type gear
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, WotrTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, WotrTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, WotrTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, WotrTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                // barbarian type gear
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, WotrTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, WotrTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, WotrTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, WotrTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                // wizard type gear
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, WotrTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, WotrTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, WotrTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, WotrTags.Items.WIZARD_TYPE_GEAR.location().getPath())))

                                // Elytra for fun at this point probably don't need implicits on elytra, so only rolling
                                // sockets
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier(4, 5))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(4, 5)))
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier().min(6))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(6, 6)))

                        ));

    }

    /**
     * Generate a loot table for socketed vanilla weapons. This table only contains swords as the function that rolls
     * sockets currently check specific tags and rerolls the item types
     * 
     * @param consumer
     */
    private void generateSocketedVanillaWeaponLootTable(
            BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/socketed_vanilla_weapons"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                // rogue type weapons
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                WotrTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                WotrTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                WotrTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                WotrTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                // Tank type weapons
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                WotrTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                WotrTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                WotrTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                WotrTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                // Barbarian type weapons
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                WotrTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                WotrTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                WotrTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                WotrTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                // Wizard type weapons
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier().max(2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                WotrTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                WotrTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                WotrTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier().min(7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                WotrTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                        ));

    }

    /**
     * Generate a loot table for socketed vanilla tools. as durability is disabled, this just rolls netherite tools with
     * sockets
     * 
     * @param consumer
     */
    private void generateSocketedVanillaToolLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/socketed_vanilla_tools"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.NETHERITE_PICKAXE)
                                        .when(riftTier().max(4))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_PICKAXE)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_SHOVEL)
                                        .when(riftTier().max(4))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_SHOVEL)
                                        .when(riftTier().min(5))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_HOE)
                                        .when(riftTier().max(4))
                                        .setWeight(5)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_HOE)
                                        .when(riftTier().min(5))
                                        .setWeight(5)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                ));
    }

    private static @NotNull ResourceKey<LootTable> getResourceKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, path));
    }

    public HolderLookup.Provider registries() {
        return this.registries;
    }

}