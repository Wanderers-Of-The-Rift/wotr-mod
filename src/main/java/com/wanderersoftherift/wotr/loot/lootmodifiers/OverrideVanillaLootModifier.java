package com.wanderersoftherift.wotr.loot.lootmodifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.core.rift.RiftData;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.item.implicit.GearImplicits;
import com.wanderersoftherift.wotr.item.socket.GearSockets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import static com.wanderersoftherift.wotr.init.ModItems.RAW_RUNEGEM_GEODE;
import static com.wanderersoftherift.wotr.init.ModTags.Items.SOCKETABLE;

public class OverrideVanillaLootModifier extends LootModifier {

    public static final MapCodec<OverrideVanillaLootModifier> CODEC = RecordCodecBuilder.mapCodec(
            inst -> LootModifier.codecStart(inst).apply(inst, OverrideVanillaLootModifier::new));

    protected OverrideVanillaLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context) {

        // Check if the current level is a rift level and return
        // note this should never be the case, as the conditions from glm should prevent it
        ServerLevel serverlevel = context.getLevel();
        if (RiftData.isRift(serverlevel)) {
            return generatedLoot;
        }
        ResourceKey<Level> dimension = context.getLevel().dimension();

        // roll rift gear
        for (ItemStack itemStack : generatedLoot) {
            if (itemStack.is(SOCKETABLE)) {
                // Add sockets to the item
                if (Level.END.equals(dimension)) {
                    GearSockets.generateForItem(itemStack, context.getLevel(), 1, 2);
                } else {
                    GearSockets.generateForItem(itemStack, context.getLevel(), 1, 1);

                }
            }
            GearImplicits implicits = itemStack.get(ModDataComponentType.GEAR_IMPLICITS);
            if (implicits != null) {
                implicits.modifierInstances(itemStack, context.getLevel());
            }
        }

        float chance = context.getRandom().nextFloat();
        // roll runegems per dim
        if (Level.OVERWORLD.equals(dimension)) {
            // ow chest is 2.5% drop rate
            if (chance < 0.025F) {
                generatedLoot.add(new ItemStack(RAW_RUNEGEM_GEODE.asItem()));
            }
        } else if (Level.NETHER.equals(dimension)) {
            // nether chest is 7.5% drop rate
            if (chance < 0.075F) {
                generatedLoot.add(new ItemStack(RAW_RUNEGEM_GEODE.asItem()));
            }
        } else if (Level.END.equals(dimension)) {
            // end chest is 15% drop rate
            if (chance < 0.15F) {
                generatedLoot.add(new ItemStack(RAW_RUNEGEM_GEODE.asItem()));
            }
        }

        return generatedLoot; // Return the modified loot
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}