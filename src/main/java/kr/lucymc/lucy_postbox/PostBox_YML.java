package kr.lucymc.lucy_postbox;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class PostBox_YML {
    public static File SetItemFile() {
        return new File(Lucy_PostBox.getInstance().getDataFolder(), "/ItemData.yml");
    }

    public static boolean FindItemFile(File f) {
        if (f.exists() || f.isFile()) {
            return true;
        } else {
            return false;
        }
    }

    public static void SaveItemFile(File f) {
        File file = f;
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        configFile.set("Item", "null");
        try {
            configFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void SaveItemYmlFile(File f, ItemStack L) {
        File file = f;
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        configFile.set("Item", L);
        try {
            configFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileConfiguration LoadItemnFile(File f) {
        File file = f;
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
        return configFile;
    }
}
