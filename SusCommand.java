package com.yourserver.sus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SusCommand implements CommandExecutor, TabCompleter {

    private final SusPlugin plugin;

    public SusCommand(SusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.gui-player-only")));
                return true;
            }
            if (!player.hasPermission("sus.use")) {
                player.sendMessage(color(plugin.getConfig().getString("messages.no-permission")));
                return true;
            }
            plugin.getSusGui().open(player, 1);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "flag" -> {
                if (!sender.hasPermission("sus.flag")) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.usage-flag")));
                    return true;
                }
                String targetName = args[1];
                String check = String.join(" ", Arrays.asList(args).subList(2, args.length));

                plugin.getSusManager().flag(targetName, check);

                String flagger = (sender instanceof ConsoleCommandSender) ? "GrimAC" : sender.getName();
                FlagRecord record = plugin.getSusManager().get(targetName);
                String broadcast = plugin.getConfig().getString("messages.flag-broadcast")
                        .replace("%player%", targetName)
                        .replace("%check%", check)
                        .replace("%total%", String.valueOf(record.getTotalFlags()))
                        .replace("%flagger%", flagger);
                plugin.getServer().getConsoleSender().sendMessage(color(broadcast));
                return true;
            }

            case "clear" -> {
                if (!sender.hasPermission("sus.clear")) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission")));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.usage-clear")));
                    return true;
                }
                plugin.getSusManager().clear(args[1]);
                sender.sendMessage(color(plugin.getConfig().getString("messages.cleared")
                        .replace("%player%", args[1])));
                return true;
            }

            case "reload" -> {
                if (!sender.hasPermission("sus.reload")) {
                    sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission")));
                    return true;
                }
                plugin.reloadConfig();
                sender.sendMessage(color(plugin.getConfig().getString("messages.reloaded")));
                return true;
            }

            default -> {
                sender.sendMessage(color("&cUnknown subcommand. Use /sus, /sus flag, /sus clear, or /sus reload."));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.addAll(Arrays.asList("flag", "clear", "reload"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("clear"))) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                options.add(p.getName());
            }
        }
        return options;
    }

    private String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
