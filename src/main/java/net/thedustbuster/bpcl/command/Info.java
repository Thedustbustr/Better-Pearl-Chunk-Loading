package net.thedustbuster.bpcl.command;

import net.thedustbuster.bpcl.PearlManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.UUID;

public class Info {
  static int run(CommandContext<ServerCommandSource> context, String id) throws CommandSyntaxException {
    if (!PearlManager.getEnderPearlCache().containsKey(UUID.fromString(id))) {
      context.getSource().sendFeedback(() -> Text.literal("No pearl cached with the UUID: " + id).formatted(Formatting.RED), true);
      return -1;
    }

    PearlManager.EnderPearlData data = PearlManager.getEnderPearlCache().get(UUID.fromString(id));
    DecimalFormat df = new DecimalFormat("#.###");
    context.getSource().sendFeedback(() -> {
      MutableText txt = Text.literal("------------------------------------------------");
      txt.append(Text.literal("\nUUID: ").formatted(Formatting.BOLD).formatted(Formatting.GREEN));
      txt.append(Text.literal(id));
      txt.append(Text.literal("\nPosition: ").formatted(Formatting.BOLD).formatted(Formatting.GREEN));
      txt.append(Text.literal("(" + df.format(data.getPosition().x) + ", " + df.format(data.getPosition().y) + ", " + df.format(data.getPosition().z) + ")"));
      txt.append(Text.literal("\nVelocity: ").formatted(Formatting.BOLD).formatted(Formatting.GREEN));
      txt.append(Text.literal("(" + df.format(data.getVelocity().x) + ", " + df.format(data.getVelocity().y) + ", " + df.format(data.getVelocity().z) + ")"));
      txt.append(Text.literal("\nChunk Pos: ").formatted(Formatting.BOLD).formatted(Formatting.GREEN));
      txt.append(Text.literal(data.getChunkPos().toString()));
      txt.append("\n------------------------------------------------");
      return txt;
    }, false);
    return 1;
  }
}
