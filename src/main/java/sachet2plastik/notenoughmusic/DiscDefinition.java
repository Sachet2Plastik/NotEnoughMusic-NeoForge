package sachet2plastik.notenoughmusic;

import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import sachet2plastik.notenoughmusic.DiscsRegistry.Artists;
import sachet2plastik.notenoughmusic.loottable.conditiondata.*;
import sachet2plastik.notenoughmusic.loottable.LootDefinition;

import java.util.ArrayList;
import java.util.List;

import static sachet2plastik.notenoughmusic.DiscsRegistry.resolveRarity;

public class DiscDefinition {
    DiscDefinition(
            String name,
            Artists artist,
            DeferredHolder<SoundEvent, SoundEvent> soundEvent,
            ResourceKey<JukeboxSong> key,
            DeferredItem<Item> item,
            Integer maxCount,
            Rarity rarity,
            String description,
            Integer comparatorOutput,
            Float lengthInSeconds,
            List<LootDefinition> loots
    ) {
        Name = name;
        Artist = artist;
        SoundEvent = soundEvent;
        Key = key;
        Item = item;
        MaxCount = maxCount;
        Rarity = rarity;
        Description = description;
        ComparatorOutput = comparatorOutput;
        LengthInSeconds = lengthInSeconds;
        Loots = loots;
    }

    public record RawCondition(
            String type,
            Float chance,
            String enchant,
            Float base,
            Float perLevel,
            String item,
            String tag,
            String killer,
            Boolean direct,
            List<String> biomes,
            Boolean canSee,
            Integer min,
            Integer max,
            Integer exactly
    ) {
        public RawCondition {
            if (min == null) min = -1;
            if (max == null) max = -1;
            if (exactly == null) exactly = -1;
        }
    }

    public record RawCount(
            Integer min,
            Integer max
    ) {}

    public record RawLoot(
            String target,
            List<RawCondition> conditions,
            RawCount count,
            Integer rolls
    ) {}

    public record RawDiscDefinition(
            String name,
            String artist,
            Integer maxCount,
            String rarity,
            String description,
            Integer comparatorOutput,
            Float lengthInSeconds,
            List<RawLoot> loots
    ) {}

    public static DiscDefinition convertRaw(RawDiscDefinition raw) {
        return new DiscDefinition(
                raw.name(),
                Artists.resolveArtist(raw.artist()),
                null,
                null,
                null,
                raw.maxCount(),
                resolveRarity(raw.rarity()),
                raw.description(),
                raw.comparatorOutput(),
                raw.lengthInSeconds(),
                convertRawLoots(raw.loots())
        );
    }

    private static IConditionData buildConditionData(RawCondition data) {
        return switch (data.type()) {
            case "random_chance" -> new RandomChanceConditionData(data.chance());
            case "random_chance_enchantments" -> new RandomChancesEnchantmentConditionData(data.enchant(), data.base(), data.perLevel());
            case "killed_by_player" -> new KilledByPlayerConditionData();
            case "killed_with_item" -> new KilledWithItemConditionData(data.item());
            case "killed_with_item_tag" -> new KilledWithItemTagConditionData(data.tag());
            case "killed_by" -> new KilledByConditionData(data.killer(), data.direct());
            case "biomes" -> new BiomesConditionData(data.biomes());
            case "sky" -> new SkyConditionData(data.canSee());
            case "light" -> new LightConditionData(data.min(), data.max(), data.exactly());
            case "survive_explosion" -> new SurviveExplosionConditionData();
            default -> null;
        };
    }

    private static String DebugBuildString(String buff, RawCondition data) {
        return switch (data.type()) {
            case "random_chance" -> buff.concat("{ Type: " + data.type() + ", Chance: " + data.chance() + " },\n");
            case "random_chance_enchantments" -> buff.concat("{ Type: " + data.type() + ", Target Enchant: " + data.enchant() + ", Base Chance: " + data.base() + ", Per Level Increase: " + data.perLevel() + " },\n");
            case "killed_by_player", "survive_explosion" -> buff.concat("{ Type: " + data.type() + " },\n");
            case "killed_with_item" -> buff.concat("{ Type: " + data.type() + ", Item to kill with: " + data.item() + " },\n");
            case "killed_with_item_tag" -> buff.concat("{ Type: " + data.type() + ", Item Tag to kill with: " + data.tag() + " },\n");
            case "killed_by" -> buff.concat("{ Type: " + data.type() + ", killer: " + data.killer() + ", direct: " + data.direct() + " },\n");
            case "biomes" -> buff.concat("{ Type: " + data.type() + ", Biomes: " + data.biomes() + " },\n");
            case "sky" -> buff.concat("{ Type: " + data.type() + ", Can See The Sky: " + data.canSee() + " },\n");
            case "light" -> buff.concat("{ Type: " + data.type() + ", Min: " + data.min() + ", Max: " + data.max() + "Exactly: " + data.exactly() + " },\n");
            default -> buff.concat("{condition not implemented yet}\n");
        };
    }

    private static List<LootDefinition> convertRawLoots(List<RawLoot> raw) {
        ArrayList<LootDefinition> lootDefinitions = new ArrayList<>(List.of());

        if (raw == null)
            return lootDefinitions;

        for (RawLoot loot : raw) {
            LootDefinition toAdd = new LootDefinition();
            toAdd.Target = loot.target();
            String buff = "";
            buff = buff.concat("Target: " + toAdd.Target + "\nConditions: [\n");
            toAdd.Conditions = new ArrayList<>(List.of());
            for (RawCondition condition : loot.conditions()) {
                toAdd.Conditions.addLast(buildConditionData(condition));
                buff = DebugBuildString(buff, condition);
            }
            toAdd.CountMin = loot.count().min();
            toAdd.CountMax = loot.count().max();
            toAdd.Rolls = loot.rolls();
            buff = buff.concat("]\nCount: [" + toAdd.CountMin + " ; " + toAdd.CountMax + "], Rolls: " + toAdd.Rolls + "\n");
            System.out.println(buff);
            lootDefinitions.addLast(toAdd);
        }
        return lootDefinitions;
    }

    public String Name;
    public Artists Artist;

    public Integer MaxCount;
    public Rarity Rarity;

    public String Description;
    public Integer ComparatorOutput;
    public Float LengthInSeconds;

    public DeferredItem<Item> Item;
    public DeferredHolder<SoundEvent, SoundEvent> SoundEvent;
    public ResourceKey<JukeboxSong> Key;

    public List<LootDefinition> Loots;
}
