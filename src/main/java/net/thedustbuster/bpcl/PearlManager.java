package net.thedustbuster.bpcl;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

public class PearlManager {
  public static final ChunkTicketType<ChunkPos> ENDER_PEARL_TICKET = ChunkTicketType.create("ender_pearl", Comparator.comparingLong(ChunkPos::toLong), 2);
  private static final Map<UUID, EnderPearlData> enderPearlCache = new HashMap<>();

  public static Map<UUID, EnderPearlData> getEnderPearlCache() {
    return enderPearlCache;
  }

  // Runs every tick
  public static void tick() {
    for (Iterator<Map.Entry<UUID, EnderPearlData>> it = enderPearlCache.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<UUID, EnderPearlData> entry = it.next(); EnderPearlData data = entry.getValue();

      // If the pearl lands, remove it from the cache
      if (!data.getEntity().isAlive()) { it.remove(); continue; }

      ServerWorld world = (ServerWorld) data.getEntity().getWorld(); checkPearl(world, data);
    }
  }

  private static void checkPearl(ServerWorld world, EnderPearlData data) {
    /* This may seem weird. Basically, if the next chunk is not loaded (if the mixin failed to load it) then the pearl is going to get stuck. Since we only
    really know the pearl's exact velocity and position the tick before it gets stuck, we are going to move the pearl back to it's chunk position before it gets stuck.
    The issue is, this chunk is not unloaded yet, as it is loaded for 2 ticks, not 1. So if we check if the current chunk is loaded, it will return true. Then one more
    tick would go by, the pearl gets stuck in the next chunk, then the program tries to move it back, but it is now unloaded and will get stuck in the previous chunk.
    Basically we are figuring out when it will get stuck instead of waiting for it to get stuck. */
    if (!isEntityTickingChunk(world, data.getNextChunkPos())) {
      // Velocity is removed when the pearl gets stuck, so we roll back to last know position and velocity
      data.getEntity().setPosition(data.getPosition()); data.getEntity().setVelocity(data.getVelocity());

      /* Note the ticket is for 4 ticks, not 2 ticks. This is because if it was 2 ticks, because of desync between the mixin and the cache,
         there is a non-zero chance that the mixin does not update in time, thereby getting the pearl stuck again. */
      world.getChunkManager().addTicket(ChunkTicketType.create("ender_pearl", Comparator.comparingLong(ChunkPos::toLong), 4), data.getChunkPos(), 2, data.getChunkPos());
    }
  }

  public static boolean isEntityTickingChunk(ServerWorld world, ChunkPos chunkPos) {
    WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
    return (chunk != null && chunk.getLevelType() == ChunkLevelType.ENTITY_TICKING);
  }

  public static ChunkPos calculateNextChunkPosition(Vec3d position, Vec3d velocity) {
    return new ChunkPos(MathHelper.floor(position.x + velocity.x) >> 4, MathHelper.floor(position.z + velocity.z) >> 4);
  }

  public static void updatePearl(Entity entity, Vec3d position, Vec3d velocity) {
    // If the pearl is not cached, create an entry
    if (!enderPearlCache.containsKey(entity.getUuid())) {
      enderPearlCache.put(entity.getUuid(), new EnderPearlData(entity, position, velocity));
    } else {
      enderPearlCache.get(entity.getUuid()).setPosition(position);
      enderPearlCache.get(entity.getUuid()).setVelocity(velocity);
    }
  }

  public static class EnderPearlData {
    private final Entity entity;
    private Vec3d position;
    private Vec3d velocity;

    public EnderPearlData(Entity entity, Vec3d position, Vec3d velocity) {
      this.entity = entity;
      this.position = position;
      this.velocity = velocity;
    }

    public Entity getEntity() {
      return this.entity;
    }

    public Vec3d getPosition() { return position; }

    public Vec3d getVelocity() { return velocity; }

    public void setPosition(Vec3d position) { this.position = position; }

    public void setVelocity(Vec3d velocity) { this.velocity = velocity; }

    public ChunkPos getChunkPos() {
      return new ChunkPos(MathHelper.floor(this.getPosition().x) >> 4, MathHelper.floor(this.getPosition().z) >> 4);
    }

    public ChunkPos getNextChunkPos() {
      return calculateNextChunkPosition(this.getPosition(), this.getVelocity());
    }

    @Override
    public String toString() {
      return "Entity: " + this.entity.getUuid() + "\nPosition: " + this.getPosition() + "\nVelocity: " + this.getVelocity() + "\nChunk Position: " + this.getChunkPos();
    }
  }
}