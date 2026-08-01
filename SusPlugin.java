package com.yourserver.sus;

import org.bukkit.plugin.java.JavaPlugin;

public class SusPlugin extends JavaPlugin {

    private SusManager susManager;
    private SusGui susGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.susManager = new SusManager(this);
        this.susGui = new SusGui(this);

        getCommand("sus").setExecutor(new SusCommand(this));
        getCommand("sus").setTabCompleter(new SusCommand(this));

        getServer().getPluginManager().registerEvents(new SusGuiListener(this), this);

        getLogger().info("SusPlugin enabled. Loaded " + susManager.getAll().size() + " flagged player(s).");
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("persist-data", true) && susManager != null) {
            susManager.save();
        }
        getLogger().info("SusPlugin disabled.");
    }

    public SusManager getSusManager() {
        return susManager;
    }

    public SusGui getSusGui() {
        return susGui;
    }
}
