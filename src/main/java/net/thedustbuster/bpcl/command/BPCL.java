package net.thedustbuster.bpcl.command;

import net.thedustbuster.bpcl.PearlManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BPCL {
  public static void registerCommands() {
    CommandRegistrationCallback.EVENT.register(BPCL::register);
  }

  static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
    dispatcher.register(CommandManager.literal("bpcl")
            .then(CommandManager.literal("enabled")
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                            .requires(source -> source.hasPermissionLevel(2))
                            .executes(context -> Enabled.run(context, BoolArgumentType.getBool(context, "value")))))
            .then(CommandManager.literal("kill")
                    .then(CommandManager.argument("selector", StringArgumentType.word())
                            .requires(source -> source.hasPermissionLevel(2))
                            .suggests((context, builder) -> {
                              builder.suggest("all");
                              PearlManager.getEnderPearlCache().forEach((k, v) -> { builder.suggest(k.toString()); });
                              return builder.buildFuture();
                            })
                            .executes(context -> Kill.run(context, StringArgumentType.getString(context, "selector")))))
            .then(CommandManager.literal("info")
                    .then(CommandManager.argument("selector", StringArgumentType.word())
                            .suggests((context, builder) -> {
                              PearlManager.getEnderPearlCache().forEach((k, v) -> { builder.suggest(k.toString()); });
                              return builder.buildFuture();
                            })
                            .executes(context -> Info.run(context, StringArgumentType.getString(context, "selector"))))));
  }
}
