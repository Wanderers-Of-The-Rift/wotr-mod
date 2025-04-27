package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.mixin.AccessorSinglePoolElement;
import com.wanderersoftherift.wotr.mixin.AccessorStructureTemplate;
import com.wanderersoftherift.wotr.util.TemplateIdLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RiftTemplates {


    private static final Map<String, List<RiftGeneratable>> RIFT_TEMPLATE_CACHE = new ConcurrentHashMap<>();

    public static RiftGeneratable random(MinecraftServer server, ResourceLocation pool, Vec3i size, RandomSource random){
        var registryAccess =server.registryAccess();
        var manager =server.getStructureManager();
        var startPool = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL).get(pool).map(it->it.value().getShuffledTemplates(random)).orElse(Collections.emptyList());
        var possibleStructures=startPool.stream().filter((it)->size==null || it.getSize(manager, Rotation.NONE).equals(size)).toList();
        try {

            var it = possibleStructures.getFirst();//todo getRandom
            if(it instanceof SinglePoolElement single){
                return of(single, manager,random);
            }else {

            }
        }catch (NoSuchElementException ignored){ }
        return size==null?null:new PlaceholderRiftTemplate(size);//placeholder
    }

    public static RiftGeneratable of(SinglePoolElement e, StructureTemplateManager manager, RandomSource random){
        var template = ((AccessorSinglePoolElement)e).callGetTemplate(manager);
        var id = ((TemplateIdLookup)manager).idForTemplate(template);
        if (id==null) return null;

        var templates = RIFT_TEMPLATE_CACHE.computeIfAbsent(id.toString(),(sid)->{
            var processorsHolder = ((AccessorSinglePoolElement)e).getProcessors();
            var palettes = ((AccessorStructureTemplate)template).getPalettes();
            WanderersOfTheRift.LOGGER.debug("["+Thread.currentThread()+"] processing template "+ id+", number of palettes: "+palettes.size());
            return palettes.stream().map((palette)->
                    of(palette, template.getSize(),processorsHolder.value())
            ).toList();
        });
        return templates.isEmpty() ?null:templates.get(random.nextInt(templates.size()));
    }

    public static RiftGeneratable of(StructureTemplate.Palette palette, Vec3i size, StructureProcessorList processors){
        int chunkCount= Math.ceilDiv(size.getX() , BasicRiftTemplate.CHUNK_WIDTH);
        var blockStates = new BlockState[chunkCount][BasicRiftTemplate.CHUNK_WIDTH*size.getY()*size.getZ()];
        var blockEntities = new HashMap<Vec3i, CompoundTag>();

        palette.blocks().forEach((block)->{
            if(block instanceof StructureTemplate.StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt)){
                var chunk = pos.getX()/ BasicRiftTemplate.CHUNK_WIDTH;
                var blockStateChunk = blockStates[chunk];
                blockStateChunk[(pos.getX()% BasicRiftTemplate.CHUNK_WIDTH) + pos.getZ()* BasicRiftTemplate.CHUNK_WIDTH + pos.getY()* BasicRiftTemplate.CHUNK_WIDTH*size.getZ()] = state;
                if(nbt!=null && !nbt.isEmpty())blockEntities.put(pos, nbt);
            }
        });

        var blocks = new Block[chunkCount][BasicRiftTemplate.CHUNK_WIDTH*size.getY()*size.getZ()];
        for (int chunk = 0; chunk < chunkCount; chunk++) {

            var blockStateChunk = blockStates[chunk];
            var blockChunk = blocks[chunk];
            for (int i = 0; i < blockStateChunk.length; i++) {
                var state = blockStateChunk[i];
                if(state!=null)blockChunk[i]=state.getBlock();
            }
        }
        var settings = new StructurePlaceSettings();

        processors.list().forEach(settings::addProcessor);
        return new BasicRiftTemplate(blockStates, size, settings,blockEntities,palette.jigsaws());

    }

}
