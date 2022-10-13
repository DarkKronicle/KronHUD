package io.github.darkkronicle.kronhud.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.darkkronicle.darkkore.DarkKore;
import io.github.darkkronicle.darkkore.config.ModConfig;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.config.options.OptionSection;
import io.github.darkkronicle.darkkore.hotkeys.HotkeySettings;
import io.github.darkkronicle.darkkore.hotkeys.HotkeySettingsOption;
import io.github.darkkronicle.darkkore.intialization.profiles.PlayerContextCheck;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import io.github.darkkronicle.kronhud.gui.hud.CoordsHud;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.CrosshairHud;
import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.hud.vanilla.ScoreboardHud;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class ConfigHandler extends ModConfig {

    private static final ConfigHandler INSTANCE = new ConfigHandler();

    public static ConfigHandler getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = LogManager.getLogger();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir() + "/kronhud.json");

    private ConfigHandler() {}

    public final HotkeySettingsOption editHud = new HotkeySettingsOption(
            "RIGHT_SHIFT", "option.kronhud.general.edithud", "option.kronhud.general.edithud.info",
            new HotkeySettings(false, false, true, new ArrayList<>(List.of(GLFW.GLFW_KEY_RIGHT_SHIFT)), PlayerContextCheck.getDefault())
    );

    public final OptionSection general = new OptionSection(
            "general", "option.section.general", "option.section.general.info",
            List.of(editHud)
    );

    @Override
    public File getFile() {
        return CONFIG_FILE;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void save() {
        setupFileConfig();
        config.load();
        for (Option<?> entry : getOptions()) {
            try {
                entry.save(config.getConfig());
            } catch (Exception e) {
                DarkKore.LOGGER.log(Level.WARN, "Fail saving option " + entry.getValue(), e);
            }
        }
        config.getConfig().set("configVersion", "2");
        config.save();
        config.close();
    }

    @Override
    public void setupFileConfig() {
        if (!CONFIG_FILE.exists()) {
            try {
                ConfigVersions.attemptConvert();
            } catch (IOException | IllegalStateException e) {
                LOGGER.error("Failed to convert old config", e);
            }
        }
        super.setupFileConfig();
    }

    @Override
    public List<Option<?>> getOptions() {
        List<Option<?>> options = HudManager.getInstance().getEntriesSorted().stream().map(HudEntryOption::new).collect(Collectors.toList());
        options.add(general);
        return options;
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
                                    resultValue.addProperty(
                                            configEntry.getKey(),
                                            new Color(resultValue.remove(newEntryKey).getAsJsonObject().get("color").getAsInt()).toString()
                                    );
                                }
                            }

                            if (converted.equals(CrosshairHud.ID.toString())) {
                                if (newEntryKey.equals("type")) {
                                    resultValue.addProperty(
                                            newEntryKey,
                                            resultValue.remove(newEntryKey).getAsString().toLowerCase()
                                    );
                                } else if (newEntryKey.equals("basic")) {
                                    resultValue.add(newEntryKey = "defaultcolor", resultValue.remove("basic"));
                                } else if (newEntryKey.equals("entity")) {
                                    resultValue.add(newEntryKey = "entitycolor", resultValue.remove("entity"));
                                } else if (newEntryKey.equals("block")) {
                                    resultValue.add(newEntryKey = "blockcolor", resultValue.remove("block"));
                                }
                            } else if (converted.equals(CoordsHud.ID.toString())) {
                                if (newEntryKey.equals("decimalnum")) {
                                    resultValue.addProperty(
                                            newEntryKey = "decimalplaces",
                                            resultValue.get("decimalnum").getAsInt()
                                    );
                                }
                            } else if (converted.equals(ScoreboardHud.ID.toString())) {
                                if (newEntryKey.equals("top")) {
                                    resultValue.add(
                                            newEntryKey = "topbackgroundcolor",
                                            resultValue.get("top")
                                    );
                                } else if (newEntryKey.equals("background")) {
                                    resultValue.add(
                                            newEntryKey = "backgroundcolor",
                                            resultValue.get("background")
                                    );
                                }
                            } else if (converted.equals(ScoreboardHud.ID)) {
                                if (newEntryKey.equals("unselected")) {
                                    resultValue.add(
                                            newEntryKey = "backgroundcolor",
                                            resultValue.get("unselected")
                                    );
                                } else if (newEntryKey.equals("selected")) {
                                    resultValue.add(
                                            newEntryKey = "heldbackgroundcolor",
                                            resultValue.get("selected")
                                    );
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
         *
         * @return The file.
         */
        protected File getFile() {
            return new File(FabricLoader.getInstance().getConfigDir() + "/kronhud.json");
        }

        /**
         * Tests for a version within a json object.
         *
         * @param object The input.
         * @return The result.
         */
        protected boolean test(JsonObject object) {
            return true;
        }

        /**
         * Converts the config of the version to the next version.
         *
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
                        System.out.println(result);
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }

    }

}
