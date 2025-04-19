package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.commands.AbilityArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister
            .create(Registries.COMMAND_ARGUMENT_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<ArgumentTypeInfo<?, ?>> ABILITY_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("ability",
            () -> ArgumentTypeInfos.registerByClass(AbilityArgument.class,
                    SingletonArgumentInfo.contextFree(AbilityArgument::new)));

}
