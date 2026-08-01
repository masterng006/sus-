package com.yourserver.sus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SusGuiHolder implements InventoryHolder {

    private final int page;
    private Inventory inventory;

    public SusGuiHolder(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
