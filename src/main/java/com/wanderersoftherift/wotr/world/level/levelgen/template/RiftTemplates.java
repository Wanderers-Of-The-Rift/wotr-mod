package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.mixin.AccessorSinglePoolElement;
import com.wanderersoftherift.wotr.mixin.AccessorStructureTemplate;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.TemplateIdLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class RiftTemplates {
    
    private static final Map<String, List<RiftGeneratable>> RIFT_TEMPLATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, FastWeightedList<RiftGeneratable>> RIFT_TEMPLATE_POOL_CACHE = new ConcurrentHashMap<>();


    public static RiftGeneratable random(MinecraftServer server, ResourceLocation pool, RandomSource random){
        var cacheKey = pool.toString();
        if (cacheKey.equals("minecraft:empty")){
            return null;
        }
        var list = RIFT_TEMPLATE_POOL_CACHE.computeIfAbsent(cacheKey,(unused)->FastWeightedList.byCountingDuplicates(all(server, pool), RiftGeneratable::identifier));
        return list.random(random);
    }

    public static List<RiftGeneratable> all(MinecraftServer server, ResourceLocation pool){
        var registryAccess = server.registryAccess();
        var manager = server.getStructureManager();
        var startPool = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL)
                .get(pool).map(it -> it.value().getShuffledTemplates(RandomSource.create(0))).orElse(Collections.emptyList());
        return startPool.stream().flatMap(it ->  fromPoolElement((SinglePoolElement) it, manager).stream()).toList();
    }

    public static RiftGeneratable fromPoolElement(SinglePoolElement e, StructureTemplateManager manager, RandomSource random){
        var template = ((AccessorSinglePoolElement) e).callGetTemplate(manager);
        var id = ((TemplateIdLookup) manager).idForTemplate(template);
        if (id==null) return null;

        var templates = RIFT_TEMPLATE_CACHE.computeIfAbsent(id.toString(),(sid)->{
            var processorsHolder = ((AccessorSinglePoolElement) e).getProcessors();
            var palettes = ((AccessorStructureTemplate) template).getPalettes();
            WanderersOfTheRift.LOGGER.debug("[" + Thread.currentThread() + "] processing template " + id + ", number of palettes: " + palettes.size());
            return IntStream.range(0,palettes.size()).mapToObj((idx)->
                    fromPalette(palettes.get(idx), template.getSize(),processorsHolder.value(),sid+":"+idx)
            ).toList();
        });
        return templates.isEmpty() ? null : templates.get(random.nextInt(templates.size()));
    }



    public static List<RiftGeneratable> fromPoolElement(SinglePoolElement e, StructureTemplateManager manager){
        var template = ((AccessorSinglePoolElement) e).callGetTemplate(manager);
        var id = ((TemplateIdLookup) manager).idForTemplate(template);
        if (id == null) return null;

        var processorsHolder = ((AccessorSinglePoolElement) e).getProcessors();
        var palettes = ((AccessorStructureTemplate) template).getPalettes();
        var templates = IntStream.range(0, palettes.size()).mapToObj( idx ->
                fromPalette(palettes.get(idx), template.getSize(), processorsHolder.value(), id.toString()+":"+idx)
        ).toList();

        return templates;
    }

    public static RiftGeneratable fromPalette(StructureTemplate.Palette palette, Vec3i size, StructureProcessorList processors, String identifier){
        int chunkCount = Math.ceilDiv(size.getX(), BasicRiftTemplate.CHUNK_WIDTH);
        var blockStates = new BlockState[chunkCount][BasicRiftTemplate.CHUNK_WIDTH * size.getY() * size.getZ()];
        var blockEntities = new HashMap<Vec3i, CompoundTag>();

        palette.blocks().forEach((block)->{
            if(block instanceof StructureTemplate.StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt)){
                var chunk = pos.getX()/ BasicRiftTemplate.CHUNK_WIDTH;
                var blockStateChunk = blockStates[chunk];
                blockStateChunk[(pos.getX()%BasicRiftTemplate.CHUNK_WIDTH) + pos.getZ() * BasicRiftTemplate.CHUNK_WIDTH + pos.getY() * BasicRiftTemplate.CHUNK_WIDTH * size.getZ()] = state;
                if(nbt!=null && !nbt.isEmpty())blockEntities.put(pos, nbt);
            }
        });

        var blocks = new Block[chunkCount][BasicRiftTemplate.CHUNK_WIDTH * size.getY() * size.getZ()];
        for (int chunk = 0; chunk < chunkCount; chunk++) {

            var blockStateChunk = blockStates[chunk];
            var blockChunk = blocks[chunk];
            for (int i = 0; i < blockStateChunk.length; i++) {
                var state = blockStateChunk[i];
                if(state != null) blockChunk[i] = state.getBlock();
            }
        }
        var settings = new StructurePlaceSettings();

        processors.list().forEach(settings::addProcessor);
        return new BasicRiftTemplate(blockStates, size, settings, blockEntities, palette.jigsaws(), identifier);

    }

}
