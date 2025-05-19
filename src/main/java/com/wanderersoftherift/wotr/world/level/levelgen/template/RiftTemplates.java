package com.wanderersoftherift.wotr.world.level.levelgen.template;

import com.wanderersoftherift.wotr.mixin.AccessorSinglePoolElement;
import com.wanderersoftherift.wotr.mixin.AccessorStructureTemplate;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.TemplateIdLookup;
import com.wanderersoftherift.wotr.world.level.levelgen.template.payload.BasicPayload;
import com.wanderersoftherift.wotr.world.level.levelgen.template.payload.LazyPayload;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.lang.ref.PhantomReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        var result = new ArrayList<RiftGeneratable>();
        for (int idx = 0; idx < palettes.size(); idx++) {
            result.add(fromPalette(palettes.get(idx), template.getSize(), processorsHolder.value(), entities,
                    MessageFormat.format("{0}:{1}:{2}", id.getNamespace(), id.getPath(), idx)));
        }
        return result;
    }

    public static RiftGeneratable fromPalette(
            StructureTemplate.Palette palette,
            Vec3i size,
            StructureProcessorList processors,
            List<StructureTemplate.StructureEntityInfo> entities,
            String identifier) {
        return new PayloadRiftTemplate(size, processors, palette.jigsaws(), identifier,
                new LazyPayload(() -> BasicPayload.of(palette, size, entities)));
    }

}
