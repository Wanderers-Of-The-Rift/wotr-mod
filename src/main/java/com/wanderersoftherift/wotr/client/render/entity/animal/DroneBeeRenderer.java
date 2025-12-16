package com.wanderersoftherift.wotr.client.render.entity.animal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.model.geo.standard.entity.DroneBeeModel;
import com.wanderersoftherift.wotr.entity.animal.DroneBee;
import com.wanderersoftherift.wotr.init.client.WotrModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DroneBeeRenderer extends MobRenderer<DroneBee, BeeRenderState, DroneBeeModel> {
    private static final ResourceLocation TEXTURE = WanderersOfTheRift.id("textures/entity/drone_bee/drone_bee.png");
    private static final ResourceLocation ANGRY_TEXTURE = WanderersOfTheRift
            .id("textures/entity/drone_bee/drone_bee_angry.png");

    public DroneBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneBeeModel(context.bakeLayer(WotrModelLayers.DRONE_BEE)), 1.0f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BeeRenderState renderState) {
        if (renderState.isAngry) {
            return ANGRY_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    public @NotNull BeeRenderState createRenderState() {
        return new BeeRenderState();
    }

    @Override
    public void extractRenderState(@NotNull DroneBee bee, @NotNull BeeRenderState state, float partialTick) {
        super.extractRenderState(bee, state, partialTick);
        state.rollAmount = bee.getRollAmount(partialTick);
        state.isOnGround = bee.onGround() && bee.getDeltaMovement().lengthSqr() < 1.0E-7;
        state.isAngry = bee.isAngry();
        state.hasNectar = bee.hasNectar();
    }
}
