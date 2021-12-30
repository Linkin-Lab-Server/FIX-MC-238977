package com.plusls.fix.mc238977.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(GlowSquidEntity.class)
class MixinGlowSquidEntity {

    @Inject(method = "canSpawn", at = @At(value = "RETURN"), cancellable = true)
    private static void postCanSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (!hasNoLight(world, pos) || !hasStoneFloor(pos, world)) {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Returns if the position has a stone floor.
     *
     * @implNote This method checks if a block in the {@link net.minecraft.tag.BlockTags#BASE_STONE_OVERWORLD}
     * tag exists within 5 blocks, and all blocks in between are water.
     */
    private static boolean hasStoneFloor(BlockPos pos, ServerWorldAccess world) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < 5; ++i) {
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
                return true;
            }
            if (blockState.isOf(Blocks.WATER)) continue;
            return false;
        }
        return false;
    }

    private static boolean hasNoLight(ServerWorldAccess world, BlockPos pos) {
        int i = world.toServerWorld().isThundering() ? world.getLightLevel(pos, 10) : world.getLightLevel(pos);
        return i == 0;
    }
}