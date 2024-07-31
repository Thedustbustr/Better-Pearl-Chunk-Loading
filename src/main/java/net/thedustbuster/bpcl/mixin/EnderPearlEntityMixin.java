package net.thedustbuster.bpcl.mixin;

import net.thedustbuster.bpcl.utils.BetterPearlChunkLoadingSettings;
import net.thedustbuster.bpcl.PearlManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin extends ThrownItemEntity {
  protected EnderPearlEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
    super(entityType, world);
  }

  // Runs every tick
  @Inject(method = "tick()V", at = @At(value = "HEAD"))
  private void appendPearl(CallbackInfo info) {
    if (!BetterPearlChunkLoadingSettings.enabled) return;
    World world = this.getEntityWorld();
    Vec3d velocity = this.getVelocity();

    // If it is with the server's world (idk why it wouldn't); If it has horizontal motion
    if (world instanceof ServerWorld serverWorld && (Math.abs(velocity.x) > 0.001 || Math.abs(velocity.z) > 0.001)) {
      Vec3d position = this.getPos();
      ChunkPos nextChunkPosition = PearlManager.calculateNextChunkPosition(position, velocity);

      /* Update pearl data in the cache
         Note: This is required as without it the pearl would get stuck in transit, mainly due to position and velocity desync with the cache */
      PearlManager.updatePearl(this, position, velocity);

      // Is the chunk it's trying to load already loaded, if so, don't send another ticket
      if (PearlManager.isEntityTickingChunk(serverWorld, nextChunkPosition)) return;
      serverWorld.getChunkManager().addTicket(PearlManager.ENDER_PEARL_TICKET, nextChunkPosition, 2, nextChunkPosition);
    }
  }
}