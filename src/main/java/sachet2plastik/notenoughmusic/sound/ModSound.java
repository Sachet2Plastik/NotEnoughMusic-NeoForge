package sachet2plastik.notenoughmusic.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sachet2plastik.notenoughmusic.DiscsRegistry;
import sachet2plastik.notenoughmusic.NotEnoughMusic;
import sachet2plastik.notenoughmusic.DiscsRegistry.Artists;

public final class ModSound {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, NotEnoughMusic.MOD_ID);
    public static final DeferredHolder<SoundEvent, SoundEvent> NEM_TEST =
            SOUND_EVENTS.register("nem_test", () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(NotEnoughMusic.MOD_ID, "nem_test")));
    public static final ResourceKey<JukeboxSong> NEM_TEST_KEY = registerKey("nem_test");

    private ModSound() {
    }

    private static ResourceKey<JukeboxSong> registerKey(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG,
                ResourceLocation.fromNamespaceAndPath(NotEnoughMusic.MOD_ID, name));
    }

    public static void registerSounds() {
        NotEnoughMusic.LOGGER.info("Registering {} music events", NotEnoughMusic.MOD_ID);
        DiscsRegistry.DISCS.forEach((name, definition) -> {
            String soundName = NotEnoughMusic.MOD_FORMAT_NAME + "_" + definition.Artist.Name() + "_" + definition.Name;
            definition.SoundEvent = SOUND_EVENTS.register(soundName, () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(NotEnoughMusic.MOD_ID, soundName)));
            definition.Key = registerKey(definition.Artist.prefix() + definition.Name);
        });
    }
}
