package org.n_factory.crazyenchant.config;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.n_factory.crazyenchant.Crazyenchant;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MinionConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("crazyenchant/minions.json");

    private static final List<EntityType<?>> minionTypes = new ArrayList<>();

    public static void load() {
        LOGGER.info("Attempting to load minions config from: {}", CONFIG_PATH);

        minionTypes.clear();

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            LOGGER.info("Config directory created/checked");

            if (!Files.exists(CONFIG_PATH)) {
                LOGGER.info("Config file not found, generating default...");
                generateDefaultConfig();
            } else {
                LOGGER.info("Config file exists, reading...");
            }

            String jsonStr = Files.readString(CONFIG_PATH);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            JsonArray array = json.getAsJsonArray("minions");

            for (JsonElement elem : array) {
                String idStr = elem.getAsString();
                ResourceLocation loc = ResourceLocation.tryParse(idStr);
                if (loc == null) continue;

                EntityType.byString(idStr).ifPresent(minionTypes::add);
            }

            LOGGER.info("Loaded {} minion types", minionTypes.size());
        } catch (Exception e) {
            LOGGER.error("Critical error loading minions.json! Falling back to default.", e);
            // 手動フォールバック（ゾンビだけじゃなく複数）
            minionTypes.add(EntityType.ZOMBIE);
            minionTypes.add(EntityType.SKELETON);
        }
    }


    private static void generateDefaultConfig() throws IOException {
        JsonObject defaultJson = new JsonObject();
        JsonArray array = new JsonArray();
        array.add("minecraft:zombie");
        array.add("minecraft:skeleton");
        array.add("minecraft:creeper");
        array.add("minecraft:spider");
        array.add("minecraft:witch");
        array.add("minecraft:vindicator");
        array.add("minecraft:pillager");

        defaultJson.add("minions", array);
        defaultJson.addProperty("enable_extra_effects", true);
        defaultJson.addProperty("comment", "Add mob IDs here (minecraft:zombie etc.)");

        Files.writeString(CONFIG_PATH, new GsonBuilder().setPrettyPrinting().create().toJson(defaultJson));
    }

    public static EntityType<?> getRandomMinion() {
        if (minionTypes.isEmpty()) return EntityType.ZOMBIE;  // フォールバック
        return minionTypes.get(Crazyenchant.RANDOM.nextInt(minionTypes.size()));
    }
}