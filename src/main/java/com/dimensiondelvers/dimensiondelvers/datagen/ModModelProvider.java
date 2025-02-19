package com.dimensiondelvers.dimensiondelvers.datagen;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.client.render.item.properties.select.SelectRuneGemShape;
import com.dimensiondelvers.dimensiondelvers.init.ModBlocks;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, DimensionDelvers.MODID);
    }

    private static ResourceLocation createRuneGemShapeModel(ResourceLocation location, Item item, String suffix, ModelTemplate modelTemplate, ItemModelGenerators itemModels) {
        return modelTemplate.create(location, TextureMapping.layer0(location), itemModels.modelOutput);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.EXAMPLE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.DEV_BLOCK.get());

        ResourceLocation modelLoc = DimensionDelvers.id("block/rift_chest");
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(ModBlocks.RIFT_CHEST.get())
                        .with(
                                PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
                                        .select(Direction.NORTH,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, modelLoc)
                                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)
                                        )
                                        .select(
                                                Direction.EAST,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, modelLoc)
                                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                        )
                                        .select(
                                                Direction.SOUTH,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, modelLoc)
                                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                        )
                                        .select(
                                                Direction.WEST,
                                                Variant.variant()
                                                        .with(VariantProperties.MODEL, modelLoc)
                                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                        )
                        )
        );

        itemModels.generateFlatItem(ModItems.EXAMPLE_ITEM.get(), ModelTemplates.FLAT_ITEM);

        this.generateRunegemItem(ModItems.RUNEGEM.get(), itemModels);
    }

    public void generateRunegemItem(Item item, ItemModelGenerators itemModels) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        ResourceLocation shapeLocation = ResourceLocation.fromNamespaceAndPath(DimensionDelvers.MODID, "item/runegem/shape/");
        List<SelectItemModel.SwitchCase<RunegemShape>> list = new ArrayList<>(RunegemShape.values().length);
        for (RunegemShape shape : RunegemShape.values()) {
            ItemModel.Unbaked model = ItemModelUtils.plainModel(createRuneGemShapeModel(shapeLocation.withSuffix(shape.getName()), ModItems.RUNEGEM.get(), shape.getName(), ModelTemplates.FLAT_ITEM, itemModels));
            list.add(ItemModelUtils.when(shape, model));
        }
        itemModels.itemModelOutput.accept(item, ItemModelUtils.select(new SelectRuneGemShape(), ItemModelUtils.plainModel(modelLocation), list));
    }

}
