package com.wanderersoftherift.wotr.client.model.geo.standard.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DroneBeeModel extends EntityModel<BeeRenderState> {

    private static final String BONE = "bone";
    private static final String BODY = "body";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private static final String RIGHT_WING = "rightwing_bone";
    private static final String LEFT_WING = "leftwing_bone";

    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLeg;
    private final ModelPart midLeg;
    private final ModelPart backLeg;
    private float rollAmount;

    public DroneBeeModel(ModelPart root) {
        super(root);
        this.bone = root.getChild(BONE);
        ModelPart modelpart = this.bone.getChild(BODY);
        this.rightWing = modelpart.getChild(RIGHT_WING);
        this.leftWing = modelpart.getChild(LEFT_WING);
        this.frontLeg = modelpart.getChild(FRONT_LEGS);
        this.midLeg = modelpart.getChild(MIDDLE_LEGS);
        this.backLeg = modelpart.getChild(BACK_LEGS);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild(BONE, CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition body = bone.addOrReplaceChild(BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.5F, -8.0F, -10.0F, 11.0F, 11.0F, 20.0F, new CubeDeformation(0.0F))
                        .texOffs(2, 0)
                        .addBox(3.5F, -7.0F, -13.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(2, 3)
                        .addBox(-4.5F, -7.0F, -13.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.5F, 19.0F, 0.0F));

        PartDefinition rightwing = body.addOrReplaceChild(RIGHT_WING,
                CubeListBuilder.create()
                        .texOffs(0, 31)
                        .addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.5F, -8.0F, -3.0F));

        PartDefinition leftwing = body.addOrReplaceChild(LEFT_WING,
                CubeListBuilder.create()
                        .texOffs(9, 37)
                        .addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, -8.0F, -3.0F));

        PartDefinition legFront = body.addOrReplaceChild(FRONT_LEGS,
                CubeListBuilder.create()
                        .texOffs(42, 1)
                        .addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, -2.0F));

        PartDefinition legMid = body.addOrReplaceChild(MIDDLE_LEGS,
                CubeListBuilder.create()
                        .texOffs(42, 3)
                        .addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, 1.0F));

        PartDefinition legBack = body.addOrReplaceChild(BACK_LEGS,
                CubeListBuilder.create()
                        .texOffs(42, 5)
                        .addBox(-5.0F, 0.0F, 0.0F, 7.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.5F, 3.0F, 4.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setupAnim(@NotNull BeeRenderState state) {
        super.setupAnim(state);
        this.rollAmount = state.rollAmount;
        if (!state.isOnGround) {
            float f = state.ageInTicks * 120.321_13F * (float) (Math.PI / 180.0);
            this.rightWing.yRot = 0.0F;
            this.rightWing.zRot = Mth.cos(f) * (float) Math.PI * 0.15F;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = this.rightWing.yRot;
            this.leftWing.zRot = -this.rightWing.zRot;
            this.frontLeg.xRot = (float) (Math.PI / 4);
            this.midLeg.xRot = (float) (Math.PI / 4);
            this.backLeg.xRot = (float) (Math.PI / 4);
        }

        if (!state.isAngry && !state.isOnGround) {
            float f1 = Mth.cos(state.ageInTicks * 0.18F);
            this.bone.xRot = 0.1F + f1 * (float) Math.PI * 0.025F;
            this.frontLeg.xRot = -f1 * (float) Math.PI * 0.1F + (float) (Math.PI / 8);
            this.backLeg.xRot = -f1 * (float) Math.PI * 0.05F + (float) (Math.PI / 4);
            this.bone.y = this.bone.y - Mth.cos(state.ageInTicks * 0.18F) * 0.9F;
        }

        if (this.rollAmount > 0.0F) {
            this.bone.xRot = Mth.rotLerpRad(this.rollAmount, this.bone.xRot, 3.091_592_8F);
        }
    }
}
