package io.github.darkkronicle.kronhud.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.hud.CoordsHud;
import io.github.darkkronicle.kronhud.gui.hud.CrosshairHud;
import io.github.darkkronicle.kronhud.gui.hud.ScoreboardHud;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigHandler {
    // Based off of shedaniel's code that was released under Apache License 2.0.
    // https://github.com/shedaniel/i-need-keybinds/blob/master/src/main/java/me/shedaniel/ink/ConfigManager.java
    // http://www.apache.org/licenses/LICENSE-2.0

    private static final Logger LOGGER = LogManager.getLogger();
    private File config = new File(FabricLoader.getInstance().getConfigDir() + "/kronhud.json");

    public ConfigHandler() {
        if(!config.exists()) {
            try {
                ConfigVersions.attemptConvert();
            } catch (IOException | IllegalStateException e) {
                LOGGER.error("Failed to convert old config", e);
            }
        }
        try {
            load();
        } catch (IOException | IllegalStateException e) {
            LOGGER.error("Failed to load config", e);
        }
        saveDefaultHandling();
    }

    public boolean saveDefaultHandling() {
        boolean success = true;
        try {
            save();
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
            success = false;
        }
        return success;
    }

    public void save() throws IOException {
        config.getParentFile().mkdirs();
        if (!config.exists() && !config.createNewFile()) {
            return;
        }

        JsonObject object = new JsonObject();
        object.addProperty("configVersion", "2");
        for (AbstractHudEntry hud : KronHUD.hudManager.getEntries()) {
            JsonObject section = new JsonObject();
            for (IConfigBase config : hud.getAllOptions()) {
                section.add(((KronConfig) config).getId(), config.getAsJsonElement());
            }
            object.add(hud.getId().toString(), section);
        }
        if (!config.exists()) {
            config.createNewFile();
        }
        String result = object.toString();
        FileOutputStream out = new FileOutputStream(config, false);
        out.write(result.getBytes());
        out.flush();
        out.close();
    }

    public void load() throws IOException {
        config.getParentFile().mkdirs();
        try {
            KronHUD.storage = JsonParser.parseReader(new FileReader(config)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            KronHUD.storage = new JsonObject();
        }
        for (AbstractHudEntry hud : KronHUD.hudManager.getEntries()) {
            if (KronHUD.storage.has(hud.getId().toString())) {
                JsonObject section = KronHUD.storage.get(hud.getId().toString()).getAsJsonObject();
                for (IConfigBase config : hud.getAllOptions()) {
                    String id = ((KronConfig) config).getId();
                    if (section.has(id)) {
                        config.setValueFromJsonElement(section.get(id));
                    }
                }
            }
        }
    }

    public enum ConfigVersions {
        V1 {

            @Override
            protected File getFile() {
                // Can't think of a good reason to use NIO.
                return new File(FabricLoader.getInstance().getConfigDir() + "/kronhud/config.json");
            }

            @Override
            protected JsonObject convert(JsonObject object) {
                JsonObject result = new JsonObject();
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    if (entry.getKey().endsWith("Storage")) {
                        String converted =
                                "kronhud:" + entry.getKey().substring(0, entry.getKey().indexOf("Storage"))
                            .toLowerCase();
                        JsonObject resultValue = new JsonObject();

                        for (Map.Entry<String, JsonElement> configEntry : entry.getValue().getAsJsonObject().entrySet()) {
                            String newEntryKey;
                            resultValue.add(newEntryKey = configEntry.getKey().toLowerCase(), configEntry.getValue());

                            if (configEntry.getValue().isJsonObject()) {
                                if (configEntry.getValue().getAsJsonObject().has("color")) {
                                    resultValue.addProperty(configEntry.getKey(),
                                            new Color(resultValue.remove(newEntryKey)
                                                    .getAsJsonObject().get("color").getAsInt()).toString());
                                }
                            }

                            if (converted.equals(CrosshairHud.ID.toString())) {
                                if (newEntryKey.equals("type")) {
                                    resultValue.addProperty(newEntryKey,
                                            resultValue.remove(newEntryKey).getAsString().toLowerCase());
                                } else if (newEntryKey.equals("basic")) {
                                    resultValue.add(newEntryKey = "defaultcolor", resultValue.remove("basic"));
                                } else if (newEntryKey.equals("entity")) {
                                    resultValue.add(newEntryKey = "entitycolor", resultValue.remove("entity"));
                                } else if (newEntryKey.equals("block")) {
                                    resultValue.add(newEntryKey = "blockcolor", resultValue.remove("block"));
                                }
                            } else if (converted.equals(CoordsHud.ID.toString())) {
                                if(newEntryKey.equals("decimalnum")) {
                                    resultValue.addProperty(newEntryKey = "decimalplaces",
                                            resultValue.get("decimalnum").getAsInt());
                                }
                            } else if (converted.equals(ScoreboardHud.ID.toString())) {
                                if (newEntryKey.equals("top")) {
                                    resultValue.add(newEntryKey = "topbackgroundcolor",
                                            resultValue.get("top"));
                                } else if (newEntryKey.equals("background")) {
                                    resultValue.add(newEntryKey = "backgroundcolor",
                                            resultValue.get("background"));
                                }
                            } else if (converted.equals(ScoreboardHud.ID)) {
                                if (newEntryKey.equals("unselected")) {
                                    resultValue.add(newEntryKey = "backgroundcolor",
                                            resultValue.get("unselected"));
                                } else if (newEntryKey.equals("selected")) {
                                    resultValue.add(newEntryKey = "heldbackgroundcolor",
                                            resultValue.get("selected"));
                                }
                            }
                        }
                        result.add(converted, resultValue);
                    }
                }
                return result;
            }

        },
        V2;

        /**
         * Gets the version's file.
         * @return The file.
         */
        protected File getFile() {
            return new File(FabricLoader.getInstance().getConfigDir() + "/kronhud.json");
        }

        /**
         * Tests for a version within a json object.
         * @param object The input.
         * @return The result.
         */
        protected boolean test(JsonObject object) {
            return true;
        }

        /**
         * Converts the config of the version to the next version.
         * @param object The input.
         */
        protected JsonObject convert(JsonObject object) {
            throw new IllegalStateException();
        }

        public static void attemptConvert() throws IOException {
            ConfigVersions current;
            ConfigVersions next = values()[0];
            for (int i = 0; i < values().length - 1; i++) {
                current = next;
                next = values()[i + 1];
                if (current.getFile().exists() && !next.getFile().exists()) {
                    JsonObject content = JsonParser.parseReader(new FileReader(current.getFile())).getAsJsonObject();
                    if (current.test(content)) {
                        JsonObject result = current.convert(content);
                        FileWriter writer = new FileWriter(next.getFile());
                        writer.write(result.toString());
                        System.out.println(result.toString());
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }

    }

}
