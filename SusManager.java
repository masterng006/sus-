package com.yourserver.sus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SusManager {

    private final JavaPlugin plugin;
    private final Map<String, FlagRecord> flagged = new LinkedHashMap<>();
    private final File dataFile;

    public SusManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "flags.yml");
        load();
    }

    public void flag(String playerName, String check) {
        FlagRecord record = flagged.get(playerName.toLowerCase());
        if (record == null) {
            record = new FlagRecord(check);
            flagged.put(playerName.toLowerCase(), record);
        } else {
            record.addFlag(check);
        }
    }

    public void clear(String playerName) {
        flagged.remove(playerName.toLowerCase());
    }

    public FlagRecord get(String playerName) {
        return flagged.get(playerName.toLowerCase());
    }

    public Map<String, FlagRecord> getAll() {
        return flagged;
    }

    public void load() {
        if (!dataFile.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        if (yaml.getConfigurationSection("flags") == null) return;

        for (String key : yaml.getConfigurationSection("flags").getKeys(false)) {
            String path = "flags." + key + ".";
            int total = yaml.getInt(path + "total", 1);
            String lastCheck = yaml.getString(path + "lastCheck", "Unknown");
            long lastTime = yaml.getLong(path + "lastTime", System.currentTimeMillis());

            FlagRecord record = new FlagRecord(lastCheck);
            record.setTotalFlags(total);
            record.setLastFlagTime(lastTime);
            flagged.put(key, record);
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<String, FlagRecord> entry : flagged.entrySet()) {
            String path = "flags." + entry.getKey() + ".";
            yaml.set(path + "total", entry.getValue().getTotalFlags());
            yaml.set(path + "lastCheck", entry.getValue().getLastCheck());
            yaml.set(path + "lastTime", entry.getValue().getLastFlagTime());
        }
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            yaml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save flags.yml: " + e.getMessage());
        }
    }
}
