package com.wanderersoftherift.wotr.modifier.source;

import com.wanderersoftherift.wotr.modifier.WotrEquipmentSlot;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public interface ModifierSource extends StringRepresentable {
    @Nullable WotrEquipmentSlot slot();
}
