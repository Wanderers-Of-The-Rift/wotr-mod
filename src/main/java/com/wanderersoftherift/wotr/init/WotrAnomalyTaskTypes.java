package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.AnomalyTask;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.BattleTask;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.BundleTask;
import com.wanderersoftherift.wotr.block.blockentity.anomaly.NeedleTask;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrAnomalyTaskTypes {
    public static final DeferredRegister<AnomalyTask.AnomalyTaskType<?>> ANOMALY_TASK_TYPES = DeferredRegister
            .create(WotrRegistries.ANOMALY_TASK_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<AnomalyTask.AnomalyTaskType<?>> NEEDLE_TASK = ANOMALY_TASK_TYPES.register("needle",
            () -> NeedleTask.TYPE);

    public static final Supplier<AnomalyTask.AnomalyTaskType<?>> BUNDLE_TASK = ANOMALY_TASK_TYPES.register("bundle",
            () -> BundleTask.TYPE);

    public static final Supplier<AnomalyTask.AnomalyTaskType<?>> BATTLE_TASK = ANOMALY_TASK_TYPES.register("battle",
            () -> BattleTask.TYPE);
}
