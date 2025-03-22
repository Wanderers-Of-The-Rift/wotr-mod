package com.wanderersoftherift.wotr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(StructureTemplate.class)
public class MixinStructureTemplate {

    @Inject(method = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;", at = @At("HEAD"))
    private static void processBlockInfosSortList(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> blockInfos, StructureTemplate template, CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir) {
        sortByPos(blockInfos);
    }

    @Inject(method = "processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureProcessor;finalizeProcessing(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Ljava/util/List;Ljava/util/List;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;)Ljava/util/List;"))
    private static void processBlockInfosSortList1(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> blockInfos, StructureTemplate template, CallbackInfoReturnable<List<StructureTemplate.StructureBlockInfo>> cir, @Local(ordinal = 2) List<StructureTemplate.StructureBlockInfo> list1) {
        sortByPos(list1);
    }

    private static void sortByPos(List<StructureTemplate.StructureBlockInfo> blockInfos) {
        blockInfos.sort((o1, o2) -> {
            if (o1.pos().getZ() == o2.pos().getZ()) {
                if (o1.pos().getY() == o2.pos().getY()) {
                    return o1.pos().getX() - o2.pos().getX();
                }
                return o1.pos().getY() - o2.pos().getY();
            }
            return o1.pos().getZ() - o2.pos().getZ();
        });
    }


}
