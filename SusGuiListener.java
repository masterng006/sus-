package com.yourserver.sus;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SusGuiListener implements Listener {

    private final SusPlugin plugin;

    public SusGuiListener(SusPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        InventoryHolder holder = topInv.getHolder();
        if (!(holder instanceof SusGuiHolder susHolder)) return;

        event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(topInv)) {
            return; // click was in the player's own inventory, ignore
        }

        int slot = event.getSlot();
        Player viewer = (Player) event.getWhoClicked();
        int page = susHolder.getPage();

        if (slot == 45) {
            if (page > 1) {
                plugin.getSusGui().open(viewer, page - 1);
            }
            return;
        }
        if (slot == 49) {
            plugin.getSusGui().open(viewer, page);
            return;
        }
        if (slot == 53) {
            plugin.getSusGui().open(viewer, page + 1);
            return;
        }

        if (slot >= 45) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;
        if (!clicked.hasItemMeta()) return;

        SkullMeta meta = (SkullMeta) clicked.getItemMeta();
        OfflinePlayer target = meta.getOwningPlayer();
        if (target == null || target.getName() == null) return;
        String targetName = target.getName();

        if (event.isLeftClick()) {
            if (!target.isOnline()) {
                viewer.sendMessage(color(plugin.getConfig().getString("messages.not-online", "&c%player% is not online right now.")
                        .replace("%player%", targetName)));
                return;
            }
            Player onlineTarget = target.getPlayer();
            viewer.setGameMode(GameMode.SPECTATOR);
            viewer.teleport(onlineTarget.getLocation());
            viewer.closeInventory();
            viewer.sendMessage(color(plugin.getConfig().getString("messages.now-spectating", "&a[SUS] &7Now spectating &f%player%&7.")
                    .replace("%player%", targetName)));

        } else if (event.isRightClick()) {
            plugin.getSusManager().clear(targetName);
            viewer.sendMessage(color(plugin.getConfig().getString("messages.cleared", "&a[SUS] &fCleared all flags for &e%player%&f.")
                    .replace("%player%", targetName)));
            plugin.getSusGui().open(viewer, page);
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
