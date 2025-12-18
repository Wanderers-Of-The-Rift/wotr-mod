package com.wanderersoftherift.wotr.util;

import java.util.random.RandomGeneratorFactory;

public enum RandomFactoryType {
    DEFAULT {
        @Override
        RandomGeneratorFactory get() {
            return RandomGeneratorFactory.getDefault();
        }
    },

    XOSHIRO {
        @Override
        RandomGeneratorFactory get() {
            return RandomGeneratorFactory.all()
                    .filter((it) -> !it.isDeprecated())
                    .filter(f -> "Xoshiro256PlusPlus".equals(f.name()))
                    .findFirst()
                    .orElse(RandomGeneratorFactory.getDefault());
        }
    };

    abstract RandomGeneratorFactory get();
}
