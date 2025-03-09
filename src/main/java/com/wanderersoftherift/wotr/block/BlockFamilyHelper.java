package com.wanderersoftherift.wotr.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.google.common.collect.Maps;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraft.data.BlockFamily.Variant.*;


public class BlockFamilyHelper {
    public static final String SLAB_SUFFIX = "_slab";
    public static final String STAIRS_SUFFIX = "_stairs";
    public static final String BUTTON_SUFFIX = "_button";
    public static final String PLATE_SUFFIX = "_pressure_plate";
    public static final String WALL_SUFFIX = "_wall";
    public static final String FENCE_SUFFIX = "_fence";
    public static final String FENCE_GATE_SUFFIX = "_fence_gate";
    public static final String TRAPDOOR_SUFFIX = "_trapdoor";

    private final String blockId;
    private final Supplier<Block> baseBlock;
    private final Map<BlockFamily.Variant, Supplier<Block>> variants = Maps.newHashMap();
    private BlockFamily blockFamily;

    public BlockFamilyHelper(String blockId, Supplier<Block> baseBlock, Map<BlockFamily.Variant, Supplier<Block>> variants){
        this.blockId = blockId;
        this.baseBlock = baseBlock;
        this.variants.putAll(variants);
    }

    public String getBlockId() {
        return blockId;
    }

    public String getId() {
        return blockId;
    }

    public Supplier<Block> getBlock() {
        return baseBlock;
    }

    public BlockFamily getFamily() {
        if (blockFamily == null) {
            blockFamily = generateBlockFamily();
        }
        return blockFamily;
    }

    private BlockFamily generateBlockFamily() {
        BlockFamily.Builder blockFamilyBuilder = new BlockFamily.Builder(baseBlock.get());
        if(variants.containsKey(SLAB)) blockFamilyBuilder.slab(variants.get(SLAB).get());
        if(variants.containsKey(STAIRS)) blockFamilyBuilder.stairs(variants.get(STAIRS).get());
        if(variants.containsKey(BUTTON)) blockFamilyBuilder.button(variants.get(BUTTON).get());
        if(variants.containsKey(PRESSURE_PLATE)) blockFamilyBuilder.pressurePlate(variants.get(PRESSURE_PLATE).get());
        if(variants.containsKey(WALL)) blockFamilyBuilder.wall(variants.get(WALL).get());
        if(variants.containsKey(FENCE)) blockFamilyBuilder.fence(variants.get(FENCE).get());
        if(variants.containsKey(FENCE_GATE)) blockFamilyBuilder.fenceGate(variants.get(FENCE_GATE).get());
        if(variants.containsKey(TRAPDOOR)) blockFamilyBuilder.trapdoor(variants.get(TRAPDOOR).get());
        return blockFamilyBuilder.getFamily();
    }

    public ResourceLocation getBaseResourceLocation() {
        return WanderersOfTheRift.id(blockId);
    }

    public Map<BlockFamily.Variant, Supplier<Block>> getVariants() {
        return new HashMap<>(variants);
    }

    public  Supplier<Block> getVariant(BlockFamily.Variant variant) {
        return getVariants().get(variant);
    }

    public static class Builder {

        private String blockId;
        private Supplier<Block> baseBlock;
        private final Map<BlockFamily.Variant, Supplier<Block>> variants = Maps.newHashMap();

        public Builder withBlockId(String blockId) {
            this.blockId = blockId;
            return this;
        }

        public Builder withBlock(Supplier<Block> block) {
            this.baseBlock = block;
            return this;
        }

        public Builder withSlab(Supplier<Block> slab) {
            this.variants.put(SLAB, slab);
            return this;
        }

        public Builder withStairs(Supplier<Block> stairs) {
            this.variants.put(BlockFamily.Variant.STAIRS, stairs);
            return this;
        }

        public Builder withButton(Supplier<Block> button) {
            this.variants.put(BlockFamily.Variant.BUTTON, button);
            return this;
        }

        public Builder withPressurePlate(Supplier<Block> pressurePlate) {
            this.variants.put(BlockFamily.Variant.PRESSURE_PLATE, pressurePlate);
            return this;
        }

        public Builder withFence(Supplier<Block> fence) {
            this.variants.put(BlockFamily.Variant.FENCE, fence);
            return this;
        }

        public Builder withFenceGate(Supplier<Block> fenceGate) {
            this.variants.put(BlockFamily.Variant.FENCE_GATE, fenceGate);
            return this;
        }

        public Builder withWall(Supplier<Block> wall) {
            this.variants.put(BlockFamily.Variant.WALL, wall);
            return this;
        }

        public Builder withTrapdoor(Supplier<Block> trapdoor) {
            this.variants.put(BlockFamily.Variant.TRAPDOOR, trapdoor);
            return this;
        }

        public BlockFamilyHelper createBuildBlockHelper() {
            return new BlockFamilyHelper(blockId, baseBlock, variants);
        }
    }


}
