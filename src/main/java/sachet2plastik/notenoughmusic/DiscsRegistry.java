package sachet2plastik.notenoughmusic;

import sachet2plastik.notenoughmusic.DiscDefinition.RawDiscDefinition;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sachet2plastik.notenoughmusic.DiscDefinition.convertRaw;

public class DiscsRegistry {
    public enum Artists {
        T_EN_M("t_en_m/", "tenm"),
        LAUDIVIDNI("laudividni/", "laudividni"),
        STEREONYX("stereonyx/", "stereonyx");
        // add new artists here

        private final String Prefix;
        private final String FormatedName;

        Artists(String prefix, String name) {
            Prefix = prefix;
            FormatedName = name;
        }

        public String prefix() {
            return Prefix;
        }

        public String Name() {
            return FormatedName;
        }

        public static Artists resolveArtist(String name) {
            for (Artists artist : Artists.values()) {
                if (artist.FormatedName.equals(name)) {
                    return artist;
                }
            }
            return null;
        }
    }

    public static Rarity resolveRarity(String name) {
        for (Rarity rarity : Rarity.values()) {
            if (rarity.name().equals(name)) {
                return rarity;
            }
        }
        return null;
    }





    public static final Map<String, DiscDefinition> DISCS = new HashMap<>();
    private static final Type DEFINITION_TYPE = new TypeToken<List<RawDiscDefinition>>() {}.getType();

    public static void printResource(String path) {
        InputStream in = NotEnoughMusic.class
                .getResourceAsStream(path);

        if (in == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            NotEnoughMusic.LOGGER.error("Error during resource printing");
        }
    }



    public static void registerDiscFromConfig() {
        InputStream stream = NotEnoughMusic.class.getResourceAsStream("/data/notenoughmusic/definitions.json");

        if (stream == null) {
            NotEnoughMusic.LOGGER.error("Error: could not read discs definitions json file;\n" +
                    "Make sure the 'definitions.json' file is in 'ressources/data/notenoughmusic/' folder");
            return;
        }
        Gson gson = new Gson();

        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        List<RawDiscDefinition> PARSED_DISCS = gson.fromJson(reader, DEFINITION_TYPE);

        for (RawDiscDefinition dd : PARSED_DISCS) {
            DISCS.put(dd.name(), convertRaw(dd));
            /*if (dd.loots() != null) {
                System.out.println("Target:" + dd.loots().getFirst().target() + "\nType: " + dd.loots().getFirst().conditions().getFirst().type());
            }*//*
            System.out.println(
                    "Name: " +  dd.name() + "\n" +
                    "Artist: " + dd.artist() + "\n"+
                    "Max Count: " + dd.maxCount() + "\n" +
                    "Rariry: " + dd.rarity() + "\n" +
                    "Destription: " + dd.description() + "\n" +
                    "Comparator Output: " + dd.comparatorOutput() + "\n" +
                    "Lenght: " + dd.lengthInSeconds() + "\n----------\n");*/
        }


    }
}
