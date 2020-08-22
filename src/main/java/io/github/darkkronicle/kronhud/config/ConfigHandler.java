package io.github.darkkronicle.kronhud.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.darkkronicle.kronhud.KronHUD;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class ConfigHandler {
    // Based off of she daniel's code that was released under Apache License 2.0.
    // https://github.com/shedaniel/i-need-keybinds/blob/master/src/main/java/me/shedaniel/ink/ConfigManager.java
    // http://www.apache.org/licenses/LICENSE-2.0

    private static final Gson GSON = Util.make(() -> {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        return builder.create();
    });

    private File config = new File(FabricLoader.getInstance().getConfigDir() + "/kronhud/config.json");

    public ConfigHandler() {
        if (!config.exists() || !config.canRead()) {
            KronHUD.storage = new ConfigStorage();
        }
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveDefaultHandling() {
        boolean success = true;
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void save() throws IOException {
        config.getParentFile().mkdirs();
        if (!config.exists() && !config.createNewFile()) {
            KronHUD.storage = new ConfigStorage();
            return;
        }

        try {
            String result = GSON.toJson(KronHUD.storage);
            if (!config.exists()) {
                config.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(config, false);
            out.write(result.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            KronHUD.storage = new ConfigStorage();
        }
    }

    public void load() throws IOException {
        config.getParentFile().mkdirs();
        boolean failed = false;
        try {
            KronHUD.storage = GSON.fromJson(new FileReader(config), ConfigStorage.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            failed = true;
        }
        if (failed || KronHUD.storage == null) {
            KronHUD.storage = new ConfigStorage();
        }
        save();
    }

}
