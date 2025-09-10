package com.wanderersoftherift.wotr.world.level.levelgen.template.payload;

import com.wanderersoftherift.wotr.util.TripleMirror;
import com.wanderersoftherift.wotr.world.level.levelgen.RiftProcessedRoom;
import com.wanderersoftherift.wotr.world.level.levelgen.template.PayloadRiftTemplate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyPayload implements PayloadRiftTemplate.TemplatePayload {
    private final Supplier<PayloadRiftTemplate.TemplatePayload> basePayload;
    private final AtomicReference<CompletableFuture<PayloadRiftTemplate.TemplatePayload>> payload = new AtomicReference<>();

    public LazyPayload(Supplier<PayloadRiftTemplate.TemplatePayload> basePayload) {
        this.basePayload = basePayload;
    }

    private PayloadRiftTemplate.TemplatePayload getBasePayload() {
        var plainFuture = payload.getPlain();
        if (plainFuture == null) {
            var future = new CompletableFuture<PayloadRiftTemplate.TemplatePayload>();
            if (payload.compareAndSet(null, future)) {
                var value = basePayload.get();
                if (value == null) {
                    throw new NullPointerException("basePayload returned null");
                }
                future.complete(value);
                return value;
            }
            plainFuture = payload.get();
        }
        try {
            return plainFuture.get();
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
