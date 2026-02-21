package nfactory.crazyenchant.data.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.CrazyEnchant;

import javax.annotation.Nullable;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FishingEnchantLevelLimit {

    private static final Map<Enchantment, Integer> LIMITS = new HashMap<>();
    private static final ResourceLocation FILE = ResourceLocation
            .fromNamespaceAndPath(CrazyEnchant.MODID, "enchant_level_limit/fishing_level_limit.json");

    public static void reload(ResourceManager manager) {
        LIMITS.clear();
        load(manager);
    }

    @Nullable
    public static Integer getLimit(Enchantment enchantment) {
        return LIMITS.get(enchantment);
    }

    private static void load(ResourceManager manager) {
        Optional<Resource> resource = manager.getResource(FILE);
        CrazyEnchant.LOGGER.info("Trying to load: {}", FILE);

        if (resource.isEmpty()) {
            CrazyEnchant.LOGGER.info("No fishing_level_limit.json found");
            return;
        }

        try (Reader reader = resource.get().openAsReader()){
            JsonObject json = GsonHelper.parse(reader);

            for (String key : json.keySet()) {
                parseEntry(key, json.get(key));
            }

            CrazyEnchant.LOGGER.info("Loaded {} fishing enchant limits", LIMITS.size());
        } catch (Exception e) {
            CrazyEnchant.LOGGER.error("Failed to load fishing_level_limit.json", e);
        }
    }

    private static void parseEntry(String enchantId, JsonElement element) {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            CrazyEnchant.LOGGER.warn("Invalid value for enchant {}", enchantId);
            return;
        }

        int maxLevel = element.getAsInt();
        if (maxLevel <= 0) return;

        ResourceLocation id = ResourceLocation.tryParse(enchantId);
        if (id == null) {
            CrazyEnchant.LOGGER.warn("Invalid enchant ID {}", enchantId);
            return;
        }

        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(id);
        if (enchantment == null) {
            CrazyEnchant.LOGGER.warn("Unknown enchantment {}", enchantId);
            return;
        }

        LIMITS.put(enchantment, maxLevel);
    }
}
