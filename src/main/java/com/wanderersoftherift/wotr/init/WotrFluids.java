package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.*;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;

import java.util.HashMap;

public class WotrFluids {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WanderersOfTheRift.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WanderersOfTheRift.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.FLUID_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID,
            WanderersOfTheRift.MODID);

    public static final HashMap<String, WotrFluid> fluidMap = new HashMap<>();

    static {
        for (int i = 1; i < 16; i++) {
            fluidMap.put(String.valueOf(i), new WotrFluid("fluid_" + i));
        }
    }

    static <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> registryKey, String id) {
        return ResourceKey.create(registryKey, createResourceLocation(id));
    }

    static ResourceLocation createResourceLocation(String id) {
        return ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, id);
    }

    public static class WotrFluid {
        public ResourceLocation FLUID_STILL_ID;
        public ResourceLocation FLUID_FLOWING_ID;
        public ResourceLocation FLUID_OVERLAY_ID;

        public DeferredHolder<FluidType, FluidType> FLUID_TYPE;
        public DeferredHolder<Fluid, FlowingFluid> FLUID_SOURCE;
        public DeferredHolder<Fluid, FlowingFluid> FLUID_FLOWING;

        public DeferredBlock<LiquidBlock> FLUID_BLOCK;
        public DeferredItem<Item> FLUID_BUCKET;

        WotrFluid(String name) {
            FLUID_STILL_ID = createResourceLocation(String.format("block/%s_still", name));
            FLUID_FLOWING_ID = createResourceLocation(String.format("block/%s_flow", name));
            FLUID_OVERLAY_ID = createResourceLocation(String.format("block/water_overlay", name));

            FLUID_TYPE = FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()));

            FLUID_SOURCE = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(makeProperties()));
            FLUID_FLOWING = FLUIDS.register(String.format("%s_flowing", name),
                    () -> new BaseFlowingFluid.Flowing(makeProperties()));

            FLUID_BLOCK = BLOCKS.register(String.format("%s_block", name),
                    () -> new LiquidBlock(FLUID_SOURCE.value(), LiquidBlock.Properties.of()
                        .replaceable()
                        .noCollission()
                        .strength(100.0F)
                        .pushReaction(PushReaction.DESTROY)
                        .liquid()
                        .noLootTable()
                        .setId(createResourceKey(Registries.BLOCK, String.format("%s_block", name)))));
            FLUID_BUCKET = ITEMS.register(String.format("%s_bucket", name),
                    () -> new BucketItem(FLUID_SOURCE.value(), new Item.Properties().craftRemainder(Items.BUCKET)
                            .stacksTo(1)
                            .setId(createResourceKey(Registries.ITEM, String.format("%s_bucket", name)))));
        }

        private BaseFlowingFluid.Properties makeProperties() {
            return new BaseFlowingFluid.Properties(FLUID_TYPE::value, FLUID_SOURCE, FLUID_FLOWING)
                    .bucket(FLUID_BUCKET)
                    .block(FLUID_BLOCK);
        }
    }
}
