package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.init.ModTags;
import com.wanderersoftherift.wotr.init.RegistryEvents;
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

public record ModChestLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        generateRunegemLootTable(consumer);
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
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/runegem")).setWeight(20))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/ability")).setWeight(5))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_armor"))
                                        .setWeight(20))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_weapons"))
                                        .setWeight(20))
                                .add(NestedLootTable.lootTableReference(getResourceKey("rift/socketed_vanilla_tools"))
                                        .setWeight(20))
                                .add(LootItem.lootTableItem(Items.EMERALD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.POTION)
                                        .setWeight(20)
                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                .add(LootItem.lootTableItem(Items.BREAD).setWeight(20))
                                .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE).setWeight(10))
                                .add(LootItem.lootTableItem(ModItems.SKILL_THREAD)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
                                .add(LootItem.lootTableItem(ModItems.SKILL_THREAD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F))))
                                .add(LootItem.lootTableItem(ModItems.SKILL_THREAD)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 6.0F))))

                ));
    }

    private void generateRunegemLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(getResourceKey("rift/runegem"), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.RAW_RUNEGEM_GEODE).when(riftTier(0, 2)).setWeight(16))
                        .add(LootItem.lootTableItem(ModItems.SHAPED_RUNEGEM_GEODE).when(riftTier(1, 4)).setWeight(8))
                        .add(LootItem.lootTableItem(ModItems.CUT_RUNEGEM_GEODE).when(riftTier(2, 5)).setWeight(4))
                        .add(LootItem.lootTableItem(ModItems.POLISHED_RUNEGEM_GEODE).when(riftTier(3, 7)).setWeight(2))
                        .add(LootItem.lootTableItem(ModItems.FRAMED_RUNEGEM_GEODE).when(riftTier(4, 7)).setWeight(1))));
    }

    private void generateAbilityLootTable(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        HolderLookup.RegistryLookup<AbstractAbility> reg = registries.lookupOrThrow(RegistryEvents.ABILITY_REGISTRY);
        consumer.accept(getResourceKey("rift/ability"), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.ABILITY_HOLDER)
                                        .when(riftTier(0, 2))
                                        .setWeight(16)
                                        .apply(AbilityHolderFunction.setAbilityOptions(1, 1,
                                                reg.getOrThrow(ModTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(ModItems.ABILITY_HOLDER)
                                        .when(riftTier(1, 3))
                                        .setWeight(8)
                                        .apply(AbilityHolderFunction.setAbilityOptions(1, 3,
                                                reg.getOrThrow(ModTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(ModItems.ABILITY_HOLDER)
                                        .when(riftTier(3, 5))
                                        .setWeight(4)
                                        .apply(AbilityHolderFunction.setAbilityOptions(3, 6,
                                                reg.getOrThrow(ModTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(ModItems.ABILITY_HOLDER)
                                        .when(riftTier(4, 7))
                                        .setWeight(2)
                                        .apply(AbilityHolderFunction.setAbilityOptions(5, 7,
                                                reg.getOrThrow(ModTags.Abilities.RIFT_DROPS))))
                                .add(LootItem.lootTableItem(ModItems.ABILITY_HOLDER)
                                        .when(riftTier(5, 7))
                                        .setWeight(1)
                                        .apply(AbilityHolderFunction.setAbilityOptions(7, 10,
                                                reg.getOrThrow(ModTags.Abilities.RIFT_DROPS))))
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
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, ModTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, ModTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, ModTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.LEATHER_HELMET)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, ModTags.Items.ROGUE_TYPE_GEAR.location().getPath())))
                                // tank type gear
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, ModTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, ModTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, ModTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_HELMET)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, ModTags.Items.TANK_TYPE_GEAR.location().getPath())))
                                // barbarian type gear
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, ModTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, ModTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, ModTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HELMET)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, ModTags.Items.BARBARIAN_TYPE_GEAR.location().getPath())))
                                // wizard type gear
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                3, 4, ModTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                4, 5, ModTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                5, 6, ModTags.Items.WIZARD_TYPE_GEAR.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_HELMET)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(
                                                6, 6, ModTags.Items.WIZARD_TYPE_GEAR.location().getPath())))

                                // Elytra for fun at this point probably don't need implicits on elytra, so only rolling
                                // sockets
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier(4, 5))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(4, 5)))
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier(6, 7))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.ELYTRA)
                                        .when(riftTier(7, 7))
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
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                ModTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                ModTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                ModTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.WOODEN_SWORD)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                ModTags.Items.ROGUE_TYPE_WEAPON.location().getPath())))
                                // Tank type weapons
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                ModTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                ModTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                ModTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                ModTags.Items.TANK_TYPE_WEAPON.location().getPath())))
                                // Barbarian type weapons
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                ModTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                ModTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                ModTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                ModTags.Items.BARBARIAN_TYPE_WEAPON.location().getPath())))
                                // Wizard type weapons
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier(0, 2))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(3, 4,
                                                ModTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier(2, 5))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(4, 5,
                                                ModTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(5, 6,
                                                ModTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
                                .add(LootItem.lootTableItem(Items.GOLDEN_SWORD)
                                        .when(riftTier(7, 7))
                                        .setWeight(20)
                                        .apply(RollGearFunction.rollRiftGear(6, 6,
                                                ModTags.Items.WIZARD_TYPE_WEAPON.location().getPath())))
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
                                        .when(riftTier(0, 4))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_PICKAXE)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_SHOVEL)
                                        .when(riftTier(0, 4))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_SHOVEL)
                                        .when(riftTier(5, 7))
                                        .setWeight(20)
                                        .apply(GearSocketsFunction.setGearSockets(5, 6)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_HOE)
                                        .when(riftTier(0, 4))
                                        .setWeight(5)
                                        .apply(GearSocketsFunction.setGearSockets(3, 5)))
                                .add(LootItem.lootTableItem(Items.NETHERITE_HOE)
                                        .when(riftTier(5, 7))
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