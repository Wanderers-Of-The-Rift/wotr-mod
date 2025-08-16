package com.wanderersoftherift.wotr.world.level;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RiftGenerationPerformanceMetrics {

    public static final int MAX_MEASUREMENT_PAUSE_MILLISECONDS = 3000;

    private final AtomicInteger inFlightChunks = new AtomicInteger(0);

    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunksInWindow = new AtomicInteger(0);
    private final AtomicLong lastChunkStart = new AtomicLong(0);
    private long generationStart = 0;

    public void chunkStarted() {
        var time = System.currentTimeMillis();
        if (inFlightChunks.getAndIncrement() == 0 && time - lastChunkStart.get() > MAX_MEASUREMENT_PAUSE_MILLISECONDS) {
            generationStart = time;
            completedChunksInWindow.set(0);
        }

        lastChunkStart.updateAndGet((value) -> Math.max(value, time));
    }

    public void chunkEnded() {
        inFlightChunks.decrementAndGet();
        completedChunks.incrementAndGet();
        completedChunksInWindow.incrementAndGet();
    }

    public void addDebugScreenInfo(List<String> info) {
        info.add("performance");
        info.add("all generated chunks: " + completedChunks.get());
        info.add("window chunks: " + completedChunksInWindow.get());
        info.add("window time: " + (lastChunkStart.get() - generationStart));
        info.add("window CPS: " + (completedChunksInWindow.get() * 1000.0 / (lastChunkStart.get() - generationStart)));
        info.add("currently generating chunks: " + inFlightChunks.get());
    }
}
