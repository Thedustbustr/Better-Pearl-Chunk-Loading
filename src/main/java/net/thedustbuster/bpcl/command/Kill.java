package net.thedustbuster.bpcl.command;

import net.thedustbuster.bpcl.PearlManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class Kill {
  static int run(CommandContext<ServerCommandSource> context, String id) throws CommandSyntaxException {
    if (id.equals("all")) {
      PearlManager.getEnderPearlCache().forEach((k, v) -> v.getEntity().kill());
      context.getSource().sendFeedback(() -> Text.literal("Removed all cached ender pearls"), true);
    } else if (!PearlManager.getEnderPearlCache().containsKey(UUID.fromString(id))) {
      context.getSource().sendFeedback(() -> Text.literal("No pearl cached with the UUID: " + id).formatted(Formatting.RED), true);
      return -1;
    } else {
      PearlManager.getEnderPearlCache().get(UUID.fromString(id)).getEntity().kill();
      context.getSource().sendFeedback(() -> Text.literal("Removed " + id + " from cache"), true);
    }

    return 1;
  }
}
