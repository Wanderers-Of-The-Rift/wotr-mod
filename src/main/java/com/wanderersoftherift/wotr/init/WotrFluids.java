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
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;

public class WotrFluids {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WanderersOfTheRift.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WanderersOfTheRift.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.FLUID_TYPES, WanderersOfTheRift.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID,
            WanderersOfTheRift.MODID);

    public static final HashMap<String, WotrFluid> FLUID_MAP = new HashMap<>();
    public static final WotrFluid FAKE_LAVA = new WotrFluid("fake_lava");

    static {
        for (int i = 1; i < 16; i++) {
            FLUID_MAP.put(String.valueOf(i), new WotrFluid("fluid_" + i));
        }
    }

    static <T> ResourceKey<T> createResourceKey(ResourceKey<? extends Registry<T>> registryKey, String id) {
        return ResourceKey.create(registryKey, createResourceLocation(id));
    }

    static ResourceLocation createResourceLocation(String id) {
        return ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID, id);
    }

    public static class WotrFluid {
        public ResourceLocation fluidStillId;
        public ResourceLocation fluidFlowingId;
        public ResourceLocation fluidOverlayId;

        public DeferredHolder<FluidType, FluidType> fluidType;
        public DeferredHolder<Fluid, FlowingFluid> fluidSource;
        public DeferredHolder<Fluid, FlowingFluid> fluidFlowing;

        public DeferredBlock<LiquidBlock> fluidBlock;
        public DeferredItem<Item> fluidBucket;

        WotrFluid(String name) {
            fluidStillId = createResourceLocation(String.format("block/%s_still", name));
            fluidFlowingId = createResourceLocation(String.format("block/%s_flow", name));
            fluidOverlayId = createResourceLocation(String.format("block/%s_overlay", name));

            fluidType = FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()));

            fluidSource = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(makeProperties()));
            fluidFlowing = FLUIDS.register(String.format("%s_flowing", name),
                    () -> new BaseFlowingFluid.Flowing(makeProperties()));

            fluidBlock = BLOCKS
                    .register(String.format("%s_block", name),
                            () -> new LiquidBlock(fluidSource.value(), LiquidBlock.Properties.of()
                                    .replaceable()
                                    .noCollission()
                                    .strength(100.0F)
                                    .pushReaction(PushReaction.DESTROY)
                                    .liquid()
                                    .noLootTable()
                                    .setId(createResourceKey(Registries.BLOCK, String.format("%s_block", name)))));
            fluidBucket = ITEMS
                    .register(String.format("%s_bucket", name),
                            () -> new BucketItem(fluidSource.value(), new Item.Properties().craftRemainder(Items.BUCKET)
                                    .stacksTo(1)
                                    .setId(createResourceKey(Registries.ITEM, String.format("%s_bucket", name)))));
        }

        private BaseFlowingFluid.Properties makeProperties() {
            return new BaseFlowingFluid.Properties(fluidType::value, fluidSource, fluidFlowing).bucket(fluidBucket)
                    .block(fluidBlock);
        }
    }
}
