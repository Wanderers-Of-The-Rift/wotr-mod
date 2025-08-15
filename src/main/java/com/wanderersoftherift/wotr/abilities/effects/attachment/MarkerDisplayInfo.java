package com.wanderersoftherift.wotr.abilities.effects.attachment;

import com.wanderersoftherift.wotr.abilities.EffectMarker;
import net.minecraft.core.Holder;

/**
 * Information for displaying an EffectMarker
 * 
 * @param marker The effect marker to display
 * @param until  The time (in ticks from game start) the effect will last until
 */
public record MarkerDisplayInfo(Holder<EffectMarker> marker, long until) {
}
