package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.BlockFamilyHelper;
import com.wanderersoftherift.wotr.block.RiftMobSpawnerBlock;
import com.wanderersoftherift.wotr.block.TrapBlock;
import com.wanderersoftherift.wotr.client.render.item.emblem.AbilityEmblemProvider;
import com.wanderersoftherift.wotr.client.render.item.emblem.CurrencyEmblemProvider;
import com.wanderersoftherift.wotr.client.render.item.emblem.EmblemSpecialRenderer;
import com.wanderersoftherift.wotr.client.render.item.properties.select.SelectRuneGemShape;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WotrModelProvider extends ModelProvider {
    public WotrModelProvider(PackOutput output) {
        super(output, WanderersOfTheRift.MODID);
    }

    private static ResourceLocation createRuneGemShapeModel(
            ResourceLocation location,
            ModelTemplate modelTemplate,
            ItemModelGenerators itemModels) {
        return modelTemplate.create(location, TextureMapping.layer0(location), itemModels.modelOutput);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.createTrivialBlock(WotrBlocks.DITTO_BLOCK.get(),
                TexturedModel.CUBE.updateTemplate(template -> template.extend().renderType("cutout").build()));
        blockModels.createTrivialCube(WotrBlocks.SPRING_BLOCK.get());

        createBlockStatesForTrapBlock(WotrBlocks.MOB_TRAP_BLOCK, blockModels);
        createBlockStatesForTrapBlock(WotrBlocks.PLAYER_TRAP_BLOCK, blockModels);
        createBlockStatesForTrapBlock(WotrBlocks.TRAP_BLOCK, blockModels);

        createRiftMobSpawner(blockModels, itemModels);

        ResourceLocation abilityBenchModel = WanderersOfTheRift.id("block/ability_bench");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(WotrBlocks.ABILITY_BENCH.get(),
                        Variant.variant().with(VariantProperties.MODEL, abilityBenchModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation keyForgeModel = WanderersOfTheRift.id("block/key_forge");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(WotrBlocks.KEY_FORGE.get(),
                        Variant.variant().with(VariantProperties.MODEL, keyForgeModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation runeAnvilModel = WanderersOfTheRift.id("block/rune_anvil");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.get(),
                        Variant.variant().with(VariantProperties.MODEL, runeAnvilModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation baseChestModel = WanderersOfTheRift.id("block/rift_chest");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(WotrBlocks.RIFT_CHEST.get(),
                        Variant.variant().with(VariantProperties.MODEL, baseChestModel))
                .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        ResourceLocation baseRiftSpawnerModel = WanderersOfTheRift.id("block/rift_spawner");
        blockModels.blockStateOutput.accept(MultiVariantGenerator
                .multiVariant(WotrBlocks.RIFT_SPAWNER.get(),
                        Variant.variant().with(VariantProperties.MODEL, baseRiftSpawnerModel))
                .with(createFacingDispatchFromUpModel()));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVGRAVEL.get(), TexturedModel.CUBE.updateTexture(
                mapping -> mapping.put(TextureSlot.ALL, ResourceLocation.withDefaultNamespace("block/gravel"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVSAND.get(), TexturedModel.CUBE.updateTexture(
                mapping -> mapping.put(TextureSlot.ALL, ResourceLocation.withDefaultNamespace("block/sand"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVREDSAND.get(), TexturedModel.CUBE.updateTexture(
                mapping -> mapping.put(TextureSlot.ALL, ResourceLocation.withDefaultNamespace("block/red_sand"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVWHITECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/white_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVORANGECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/orange_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVMAGENTACONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/magenta_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/light_blue_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/yellow_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVLIMECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/lime_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVPINKCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/pink_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVGRAYCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/gray_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/light_gray_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVCYANCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/cyan_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVPURPLECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/purple_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVBLUECONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/blue_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVBROWNCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/brown_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVGREENCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/green_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVREDCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/red_concrete_powder"))));

        blockModels.createTrivialBlock(WotrBlocks.NOGRAVBLACKCONCRETEPOWDER.get(),
                TexturedModel.CUBE.updateTexture(mapping -> mapping.put(TextureSlot.ALL,
                        ResourceLocation.withDefaultNamespace("block/black_concrete_powder"))));

        itemModels.itemModelOutput.accept(WotrItems.BUILDER_GLASSES.get(),
                ItemModelUtils.plainModel(WanderersOfTheRift.id("item/builder_glasses")));

        itemModels.generateFlatItem(WotrItems.RIFT_KEY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.RAW_RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.SHAPED_RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.CUT_RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.POLISHED_RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.FRAMED_RUNEGEM_GEODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.RAW_RUNEGEM_MONSTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.SHAPED_RUNEGEM_MONSTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.CUT_RUNEGEM_MONSTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.POLISHED_RUNEGEM_MONSTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.FRAMED_RUNEGEM_MONSTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.BASE_ABILITY_HOLDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.BASE_CURRENCY_BAG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(WotrItems.SKILL_THREAD.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(WotrItems.ABILITY_HOLDER.get(),
                new SpecialModelWrapper.Unbaked(WanderersOfTheRift.id("item/base_ability_holder"),
                        new EmblemSpecialRenderer.Unbaked(WotrItems.BASE_ABILITY_HOLDER, new AbilityEmblemProvider(),
                                0.5f, 0f, 0f, 0f)));

        itemModels.itemModelOutput.accept(WotrItems.CURRENCY_BAG.get(),
                new SpecialModelWrapper.Unbaked(WanderersOfTheRift.id("item/base_currency_bag"),
                        new EmblemSpecialRenderer.Unbaked(WotrItems.BASE_CURRENCY_BAG, new CurrencyEmblemProvider(),
                                0.5f, 0.0f, -0.125f, -0.045f)));

        this.generateRunegemItem(WotrItems.RUNEGEM.get(), itemModels);

        WotrBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> createModelsForBuildBlock(helper, blockModels));
    }

    private void createBlockStatesForTrapBlock(
            DeferredBlock<? extends Block> trapBlock,
            BlockModelGenerators generators) {
        ResourceLocation model0 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/0");
        ResourceLocation model1 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/1");
        ResourceLocation model2 = ModelLocationUtils.getModelLocation(trapBlock.get(), "/2");

        // Item Model (Pull from model0)
        generators.registerSimpleItemModel(trapBlock.get(), model0);

        // Block Models (one for each variant)
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/0", TextureMapping.cube(model0),
                generators.modelOutput);
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/1", TextureMapping.cube(model1),
                generators.modelOutput);
        ModelTemplates.CUBE_ALL.createWithSuffix(trapBlock.get(), "/2", TextureMapping.cube(model2),
                generators.modelOutput);

        // Blockstate (point to the three unique block models)
        generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(trapBlock.get())
                .with(PropertyDispatch.property(TrapBlock.STAGE)
                        .select(0, Variant.variant().with(VariantProperties.MODEL, model0))
                        .select(1, Variant.variant().with(VariantProperties.MODEL, model1))
                        .select(2, Variant.variant().with(VariantProperties.MODEL, model2))));

    }

    private void createModelsForBuildBlock(BlockFamilyHelper helper, BlockModelGenerators blockModels) {
        if (helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.PANE) != null) {
            createGlassPane(blockModels,
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.GLASS_BLOCK).get(),
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.PANE).get());
        }

        if (helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.DIRECTIONAL_PILLAR) != null) {
            createDirectionalPillar(blockModels,
                    helper.getModVariants(BlockFamilyHelper.ModBlockFamilyVariant.DIRECTIONAL_PILLAR).get());
        }

        blockModels.family(helper.getBlock().get()).generateFor(helper.getFamily());
    }

    public void generateRunegemItem(Item item, ItemModelGenerators itemModels) {
        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
        ResourceLocation shapeLocation = ResourceLocation.fromNamespaceAndPath(WanderersOfTheRift.MODID,
                "item/runegem/shape/");
        List<SelectItemModel.SwitchCase<RunegemShape>> list = new ArrayList<>(RunegemShape.values().length);
        for (RunegemShape shape : RunegemShape.values()) {
            ItemModel.Unbaked model = ItemModelUtils.plainModel(createRuneGemShapeModel(
                    shapeLocation.withSuffix(shape.getName()), ModelTemplates.FLAT_ITEM, itemModels));
            list.add(ItemModelUtils.when(shape, model));
        }
        itemModels.itemModelOutput.accept(item,
                ItemModelUtils.select(new SelectRuneGemShape(), ItemModelUtils.plainModel(modelLocation), list));
    }

    /**
     * @return A dispatch for facing a model away from the surface it is placed on, starting from an upward facing model
     */
    public static PropertyDispatch createFacingDispatchFromUpModel() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant())
                .select(Direction.DOWN,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.NORTH,
                        Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.EAST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                .select(Direction.WEST,
                        Variant.variant()
                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private void createGlassPane(BlockModelGenerators blockModels, Block glassBlock, Block paneBlock) {
        blockModels.createTrivialBlock(glassBlock, TexturedModel.createDefault(
                block -> new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(glassBlock)),
                ExtendedModelTemplateBuilder.builder()
                        .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/cube_all"))
                        .requiredTextureSlot(TextureSlot.ALL)
                        .renderType("translucent")
                        .build()));

        ExtendedModelTemplate panePostTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_post"))
                .suffix("_post")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneSideTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_side"))
                .suffix("_side")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneSideAltTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_side_alt"))
                .suffix("_side_alt")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneNoSideTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_noside"))
                .suffix("_noside")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        ExtendedModelTemplate paneNoSideAltTemplate = ExtendedModelTemplateBuilder.builder()
                .parent(ResourceLocation.fromNamespaceAndPath("minecraft", "block/template_glass_pane_noside_alt"))
                .suffix("_noside_alt")
                .requiredTextureSlot(TextureSlot.EDGE)
                .requiredTextureSlot(TextureSlot.PANE)
                .renderType("translucent")
                .build();

        TextureMapping texturemapping = TextureMapping.pane(glassBlock, paneBlock);
        ResourceLocation resourceLocationPanePost = panePostTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationPaneSide = paneSideTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationPaneSideAlt = paneSideAltTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationNoSide = paneNoSideTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        ResourceLocation resourceLocationNoSideAlt = paneNoSideAltTemplate.create(paneBlock, texturemapping,
                blockModels.modelOutput);
        Item item = paneBlock.asItem();

        blockModels.registerSimpleItemModel(item, blockModels.createFlatItemModelWithBlockTexture(item, glassBlock));
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(paneBlock)
                .with(Variant.variant().with(VariantProperties.MODEL, resourceLocationPanePost))
                .with(Condition.condition().term(BlockStateProperties.NORTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationPaneSide))
                .with(Condition.condition().term(BlockStateProperties.EAST, true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationPaneSide)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.SOUTH, true),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationPaneSideAlt))
                .with(Condition.condition().term(BlockStateProperties.WEST, true),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationPaneSideAlt)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.NORTH, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationNoSide))
                .with(Condition.condition().term(BlockStateProperties.EAST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourceLocationNoSideAlt))
                .with(Condition.condition().term(BlockStateProperties.SOUTH, false),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationNoSideAlt)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .with(Condition.condition().term(BlockStateProperties.WEST, false),
                        Variant.variant()
                                .with(VariantProperties.MODEL, resourceLocationNoSide)
                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    private void createDirectionalPillar(BlockModelGenerators blockModels, Block directionalPillarBlock) {
        blockModels.createRotatedPillarWithHorizontalVariant(directionalPillarBlock, TexturedModel.COLUMN_ALT,
                TexturedModel.COLUMN_HORIZONTAL_ALT);
    }

    public void createRiftMobSpawner(BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        Block block = WotrBlocks.RIFT_MOB_SPAWNER.get();
        TextureMapping texturemapping = TextureMapping.trialSpawner(block, "_side_inactive", "_top_inactive");
        TextureMapping texturemapping1 = TextureMapping.trialSpawner(block, "_side_active", "_top_active");
        TextureMapping texturemapping2 = TextureMapping.trialSpawner(block, "_side_active", "_top_ejecting_reward");
        TextureMapping texturemapping3 = TextureMapping.trialSpawner(block, "_side_inactive_ominous",
                "_top_inactive_ominous");
        TextureMapping texturemapping4 = TextureMapping.trialSpawner(block, "_side_active_ominous",
                "_top_active_ominous");
        TextureMapping texturemapping5 = TextureMapping.trialSpawner(block, "_side_active_ominous",
                "_top_ejecting_reward_ominous");
        ResourceLocation resourcelocation = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .create(block, texturemapping, blockModels.modelOutput);
        ResourceLocation resourcelocation1 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .createWithSuffix(block, "_active", texturemapping1, blockModels.modelOutput);
        ResourceLocation resourcelocation2 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .createWithSuffix(block, "_ejecting_reward", texturemapping2, blockModels.modelOutput);
        ResourceLocation resourcelocation3 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .createWithSuffix(block, "_inactive_ominous", texturemapping3, blockModels.modelOutput);
        ResourceLocation resourcelocation4 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .createWithSuffix(block, "_active_ominous", texturemapping4, blockModels.modelOutput);
        ResourceLocation resourcelocation5 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.extend()
                .renderType("cutout")
                .build()
                .createWithSuffix(block, "_ejecting_reward_ominous", texturemapping5, blockModels.modelOutput);
        blockModels.registerSimpleItemModel(block, resourcelocation);
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(block)
                        .with(
                                PropertyDispatch.properties(RiftMobSpawnerBlock.STATE, BlockStateProperties.OMINOUS)
                                        .generate(
                                                (state, isActive) -> {
                                                    return switch (state) {
                                                        case INACTIVE,
                                                                COOLDOWN ->
                                                            Variant.variant()
                                                                    .with(VariantProperties.MODEL,
                                                                            isActive ? resourcelocation3
                                                                                    : resourcelocation);
                                                        case WAITING_FOR_PLAYERS, ACTIVE,
                                                                WAITING_FOR_REWARD_EJECTION ->
                                                            Variant.variant()
                                                                    .with(VariantProperties.MODEL,
                                                                            isActive ? resourcelocation4
                                                                                    : resourcelocation1);
                                                        case EJECTING_REWARD -> Variant.variant()
                                                                .with(VariantProperties.MODEL,
                                                                        isActive ? resourcelocation5
                                                                                : resourcelocation2);
                                                    };
                                                }
                                        )
                        )
        );
    }
}
