package com.wanderersoftherift.wotr.core.rift.predicate;

import com.wanderersoftherift.wotr.item.riftkey.RiftConfig;
import net.minecraft.network.chat.MutableComponent;

/**
 * Interface for all predicates filtering on RiftConfig
 */
public interface RiftConfigPredicate {
    /**
     * @param config
     * @return Whether the config matches the predicate
     */
    boolean match(RiftConfig config);

    /**
     * @return Display text for the predicate
     */
    MutableComponent displayText();
}
