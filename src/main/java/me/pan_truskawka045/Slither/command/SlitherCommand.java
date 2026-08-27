package me.pan_truskawka045.Slither.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.pan_truskawka045.Slither.worm.WormEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class SlitherCommand {

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("slither")
                .requires(source -> source.getExecutor() instanceof Player player && player.isOp())
                .then(Commands.literal("points")
                        .then(Commands.argument("points", IntegerArgumentType.integer(0))
                                .executes(SlitherCommand::setPoints)))
                .build();
    }

    private static int setPoints(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getExecutor();
        Entity vehicle = ((CraftPlayer) player).getHandle().getVehicle();
        if (!(vehicle instanceof WormEntity worm)) {
            player.sendMessage(Component.text("You must be riding a worm.").color(NamedTextColor.RED));
            return 0;
        }

        int points = IntegerArgumentType.getInteger(context, "points");
        worm.setPoints(points);
        player.sendMessage(Component.text("Worm points set to " + points + ".").color(NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
