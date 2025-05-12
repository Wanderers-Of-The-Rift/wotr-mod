package com.wanderersoftherift.wotr.world.level.levelgen.template;

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
import java.lang.ref.PhantomReference;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class RiftTemplates {

    private static final Map<String, FastWeightedList<RiftGeneratable>> RIFT_TEMPLATE_POOL_CACHE = new ConcurrentHashMap<>();

    private static PhantomReference<MinecraftServer> cachedServer;

    public static RiftGeneratable random(MinecraftServer server, ResourceLocation pool, RandomSource random) {
        var cacheKey = pool.toString();
        if ("minecraft:empty".equals(cacheKey)) {
            return null;
        }

        tryInvalidateCache(server);
        var list = RIFT_TEMPLATE_POOL_CACHE.computeIfAbsent(cacheKey,
                (unused) -> FastWeightedList.byCountingDuplicates(all(server, pool), RiftGeneratable::identifier));
        return list.random(random);
    }

    private static void tryInvalidateCache(MinecraftServer server) { // some processors contain registry holders which
                                                                     // need to be reloaded to work properly,
                                                                     // alternatively you could refresh holders used by
                                                                     // those processors
        if (cachedServer == null || !cachedServer.refersTo(server)) {
            RIFT_TEMPLATE_POOL_CACHE.clear();
            cachedServer = new PhantomReference<>(server, null);
        }
    }

    public static List<RiftGeneratable> all(MinecraftServer server, ResourceLocation pool) {
        var registryAccess = server.registryAccess();
        var manager = server.getStructureManager();
        var startPool = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL)
                .get(pool)
                .map(it -> it.value().getShuffledTemplates(RandomSource.create(0)))
                .orElse(Collections.emptyList());
        return startPool.stream().flatMap(it -> fromPoolElement((SinglePoolElement) it, manager).stream()).toList();
    }

    public static List<RiftGeneratable> fromPoolElement(SinglePoolElement e, StructureTemplateManager manager) {
        var template = ((AccessorSinglePoolElement) e).callGetTemplate(manager);
        var id = ((TemplateIdLookup) manager).idForTemplate(template);
        if (id == null) {
            return Collections.emptyList();
        }

        var processorsHolder = ((AccessorSinglePoolElement) e).getProcessors();
        var palettes = ((AccessorStructureTemplate) template).getPalettes();
        var entities = ((AccessorStructureTemplate) template).getEntityInfoList();

        return IntStream.range(0, palettes.size())
                .mapToObj(
                        idx -> fromPalette(palettes.get(idx), template.getSize(), processorsHolder.value(), entities,
                                MessageFormat.format("{0}:{1}:{2}", id.getNamespace(), id.getPath(), idx))
                )
                .toList();
    }

    public static RiftGeneratable fromPalette(
            StructureTemplate.Palette palette,
            Vec3i size,
            StructureProcessorList processors,
            List<StructureTemplate.StructureEntityInfo> entities,
            String identifier) {
        int chunkCount = Math.ceilDiv(size.getX(), BasicRiftTemplate.CHUNK_WIDTH);
        var blockStates = new BlockState[chunkCount][BasicRiftTemplate.CHUNK_WIDTH * size.getY() * size.getZ()];
        var blockEntities = new HashMap<Vec3i, CompoundTag>();

        palette.blocks().forEach((block) -> {
            if (block instanceof StructureTemplate.StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt)) {
                var chunk = pos.getX() / BasicRiftTemplate.CHUNK_WIDTH;
                var blockStateChunk = blockStates[chunk];
                blockStateChunk[(pos.getX() % BasicRiftTemplate.CHUNK_WIDTH)
                        + pos.getZ() * BasicRiftTemplate.CHUNK_WIDTH
                        + pos.getY() * BasicRiftTemplate.CHUNK_WIDTH * size.getZ()] = state;
                if (nbt != null && !nbt.isEmpty()) {
                    blockEntities.put(pos, nbt);
                }
            }
        });

        var blocks = new Block[chunkCount][BasicRiftTemplate.CHUNK_WIDTH * size.getY() * size.getZ()];
        for (int chunk = 0; chunk < chunkCount; chunk++) {

            var blockStateChunk = blockStates[chunk];
            var blockChunk = blocks[chunk];
            for (int i = 0; i < blockStateChunk.length; i++) {
                var state = blockStateChunk[i];
                if (state != null) {
                    blockChunk[i] = state.getBlock();
                }
            }
        }
        var settings = new StructurePlaceSettings();

        processors.list().forEach(settings::addProcessor);
        return new BasicRiftTemplate(blockStates, size, settings, blockEntities, palette.jigsaws(), identifier,
                entities);
    }

}
