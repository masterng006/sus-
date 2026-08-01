package com.yourserver.sus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SusGui {

    private final SusPlugin plugin;

    public SusGui(SusPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player viewer, int page) {
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("gui.title", "&8&lSuspicious Players"));
        int size = plugin.getConfig().getInt("gui.size", 54);
        int perPage = plugin.getConfig().getInt("gui.heads-per-page", 45);

        SusGuiHolder holder = new SusGuiHolder(page);
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        List<String> names = new ArrayList<>(plugin.getSusManager().getAll().keySet());

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, names.size());

        int slot = 0;
        for (int i = start; i < end; i++) {
            String name = names.get(i);
            FlagRecord record = plugin.getSusManager().get(name);
            if (record == null) continue;

            inv.setItem(slot, buildHead(name, record));
            slot++;
        }

        // Navigation row
        inv.setItem(45, buildNavItem(Material.ARROW, "&aPrevious Page"));
        inv.setItem(49, buildNavItem(Material.CLOCK, "&eRefresh"));
        inv.setItem(53, buildNavItem(Material.ARROW, "&aNext Page"));

        viewer.openInventory(inv);
    }

    private ItemStack buildHead(String playerName, FlagRecord record) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        meta.setOwningPlayer(offline);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&l" + playerName));

        long secondsAgo = (System.currentTimeMillis() - record.getLastFlagTime()) / 1000;
        String timeAgo = formatTimeAgo(secondsAgo);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Last check: &f" + record.getLastCheck()));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Total flags: &f" + record.getTotalFlags()));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&7Last flag: &f" + timeAgo + " ago"));
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&aLeft-click to spectate"));
        lore.add(ChatColor.translateAlternateColorCodes('&', "&cRight-click to clear"));
        meta.setLore(lore);

        skull.setItemMeta(meta);
        return skull;
    }

    private ItemStack buildNavItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return item;
    }

    private String formatTimeAgo(long seconds) {
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m " + (seconds % 60) + "s";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h " + (minutes % 60) + "m";
        long days = hours / 24;
        return days + "d " + (hours % 24) + "h";
    }
}
