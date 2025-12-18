package com.wanderersoftherift.wotr.util;

import java.util.random.RandomGeneratorFactory;

public enum RandomFactoryType {
    DEFAULT{
        @Override
        RandomGeneratorFactory get() {
            return RandomGeneratorFactory.getDefault();
        }
    },

    XOSHIRO{
        @Override
        RandomGeneratorFactory get() {
            return RandomGeneratorFactory.all()
                    .filter((it) -> !it.isDeprecated())
                    .filter(f -> f.name()
                            .equals("Xoshiro256PlusPlus"))
                    .findFirst()
                    .orElse(RandomGeneratorFactory.getDefault());
        }
    };

    abstract RandomGeneratorFactory get();
}
