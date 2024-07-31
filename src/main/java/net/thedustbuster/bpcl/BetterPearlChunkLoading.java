package net.thedustbuster.bpcl;

import net.thedustbuster.bpcl.command.BPCL;
import net.thedustbuster.bpcl.utils.BetterPearlChunkLoadingSettings;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class BetterPearlChunkLoading implements ModInitializer {
  public static final String MOD_ID = "better-pearl-chunk-loading";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitialize() {
    ServerTickEvents.START_SERVER_TICK.register(server -> {
      onTick();
    });

    BPCL.registerCommands();
  }

  private void onTick() {
    if (BetterPearlChunkLoadingSettings.enabled) PearlManager.tick();
  }
}