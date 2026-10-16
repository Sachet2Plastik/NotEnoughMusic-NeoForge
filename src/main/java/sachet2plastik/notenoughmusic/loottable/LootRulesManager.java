package sachet2plastik.notenoughmusic.loottable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import sachet2plastik.notenoughmusic.DiscDefinition;
import sachet2plastik.notenoughmusic.DiscsRegistry;
import sachet2plastik.notenoughmusic.NotEnoughMusic;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Reader;
import java.util.Optional;

public final class LootRulesManager extends SimplePreparableReloadListener<Optional<JsonElement>> {
    private static final Gson GSON = new Gson();
    private static final Type LOOT_LIST_TYPE = new TypeToken<List<DiscDefinition.RawLoot>>() {}.getType();
    private static final ResourceLocation CONFIG_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(NotEnoughMusic.MOD_ID, "disc_config.json");
    private static volatile Map<String, List<LootDefinition>> activeRules = Map.of();

    public LootRulesManager() {}

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new LootRulesManager());
    }

    public static void initializeBundledRules() {
        resetToBundledRules();
    }

    @Override
    protected Optional<JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return resourceManager.getResource(CONFIG_RESOURCE).map(resource -> {
            try (Reader reader = resource.openAsReader()) {
                return GSON.fromJson(reader, JsonElement.class);
            } catch (Exception error) {
                throw new JsonParseException("could not read " + CONFIG_RESOURCE, error);
            }
        });
    }

    public static List<LootDefinition> rulesFor(String discName) {
        return activeRules.getOrDefault(discName, List.of());
    }

    private static void resetToBundledRules() {
        Map<String, List<LootDefinition>> defaults = new HashMap<>();
        DiscsRegistry.DISCS.forEach((name, definition) ->
                defaults.put(name, List.copyOf(definition.Loots)));
        activeRules = Map.copyOf(defaults);
    }

    @Override
    protected void apply(Optional<JsonElement> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        if (prepared.isEmpty()) {
            resetToBundledRules();
            NotEnoughMusic.LOGGER.info("No disc acquisition override found; using bundled loot rules");
            return;
        }

        try {
            Map<String, List<LootDefinition>> replacement = parseReplacement(prepared.get());
            activeRules = Map.copyOf(replacement);
            NotEnoughMusic.LOGGER.info("Loaded server disc acquisition override for {} disc(s)", replacement.size());
        } catch (RuntimeException error) {
            NotEnoughMusic.LOGGER.error("Invalid {}. Keeping the previous disc acquisition rules",
                    CONFIG_RESOURCE, error);
        }
    }

    private static Map<String, List<LootDefinition>> parseReplacement(JsonElement element) {
        if (!element.isJsonObject()) {
            throw new JsonParseException("root must be an object");
        }
        JsonObject root = element.getAsJsonObject();
        JsonElement discsElement = root.get("discs");
        if (discsElement == null || !discsElement.isJsonObject()) {
            throw new JsonParseException("discs must be an object");
        }

        Map<String, List<LootDefinition>> replacement = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : discsElement.getAsJsonObject().entrySet()) {
            if (!DiscsRegistry.DISCS.containsKey(entry.getKey())) {
                throw new JsonParseException("unknown disc: " + entry.getKey());
            }
            if (!entry.getValue().isJsonArray()) {
                throw new JsonParseException("loot rules for " + entry.getKey() + " must be an array");
            }
            List<DiscDefinition.RawLoot> rawRules = GSON.fromJson(entry.getValue(), LOOT_LIST_TYPE);
            validateRules(entry.getKey(), rawRules);
            replacement.put(entry.getKey(), List.copyOf(DiscDefinition.convertRawLoots(rawRules)));
        }
        return replacement;
    }

    private static void validateRules(String discName, List<DiscDefinition.RawLoot> rules) {
        if (rules == null) {
            throw new JsonParseException("rules for " + discName + " must not be null");
        }
        for (DiscDefinition.RawLoot rule : rules) {
            if (rule == null || rule.target() == null || rule.target().isBlank()) {
                throw new JsonParseException("rule for " + discName + " has no target");
            }
            if (rule.conditions() == null || rule.count() == null || rule.rolls() == null) {
                throw new JsonParseException("rule for " + discName + " is missing conditions, count, or rolls");
            }
            if (rule.rolls() < 0 || rule.count().min() < 0 || rule.count().max() < rule.count().min()) {
                throw new JsonParseException("invalid rolls/count range for " + discName);
            }
            for (DiscDefinition.RawCondition condition : rule.conditions()) {
                if (condition == null || condition.type() == null || !isSupportedCondition(condition.type())) {
                    throw new JsonParseException("unsupported condition in rule for " + discName);
                }
            }
        }
    }

    private static boolean isSupportedCondition(String type) {
        return switch (type) {
            case "random_chance", "random_chance_enchantments", "killed_by_player",
                    "killed_with_item", "killed_with_item_tag", "killed_by",
                    "biomes", "sky", "light", "survive_explosion" -> true;
            default -> false;
        };
    }
}
