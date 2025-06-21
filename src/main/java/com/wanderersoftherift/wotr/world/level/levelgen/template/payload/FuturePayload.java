package com.wanderersoftherift.wotr.world.level.levelgen.template.payload;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PayloadRiftTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class FuturePayload implements PayloadRiftTemplate.TemplatePayload {
    private final CompletableFuture<PayloadRiftTemplate.TemplatePayload> payload;

    public FuturePayload(Supplier<PayloadRiftTemplate.TemplatePayload> basePayload) {
        this.payload = CompletableFuture.supplyAsync(basePayload, Thread::startVirtualThread);
    }

    private PayloadRiftTemplate.TemplatePayload getBasePayload() {
        try {
            return payload.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processPayloadBlocks(
            PayloadRiftTemplate template,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            BlockPos offset,
            TripleMirror mirror) {
        var base = getBasePayload();
        base.processPayloadBlocks(template, destination, world, offset, mirror);
    }

    @Override
    public void processPayloadEntities(
            PayloadRiftTemplate payloadRiftTemplate,
            RiftProcessedRoom destination,
            ServerLevelAccessor world,
            BlockPos offset,
            TripleMirror mirror) {
        var base = getBasePayload();
        base.processPayloadEntities(payloadRiftTemplate, destination, world, offset, mirror);
    }
}
