package net.thedustbuster.bpcl.command;

import net.thedustbuster.bpcl.utils.BetterPearlChunkLoadingSettings;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Enabled {
  static int run(CommandContext<ServerCommandSource> context, boolean bool) throws CommandSyntaxException {
    BetterPearlChunkLoadingSettings.enabled = bool;

    String msg; if (bool) msg = "enabled";
    else msg = "disabled";

    context.getSource().sendFeedback(() -> {
      MutableText txt = Text.literal("*Note, this will not persist through restarts").formatted(Formatting.GRAY);
      if (!bool) { txt.append(Text.literal("\nBetterPearlChunkLoading is now " + msg).formatted(Formatting.WHITE)); }

      return txt;
    }, true); return 1;
  }
}
