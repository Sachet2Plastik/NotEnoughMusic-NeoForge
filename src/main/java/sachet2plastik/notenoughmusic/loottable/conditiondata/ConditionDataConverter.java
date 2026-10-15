package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.advancements.critereon.LightPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;


public class ConditionDataConverter {

    public LootItemCondition.Builder buildFromData(RandomChanceConditionData data) {
        return LootItemRandomChanceCondition.randomChance(data.Chance);
    }

    public LootItemCondition.Builder buildFromData(RandomChancesEnchantmentConditionData data, IConditionData.ConditionContext ctx) {
        ResourceLocation enchantID = ResourceLocation.parse(data.Enchant);
        ResourceKey<Enchantment> enchantKey = ResourceKey.create(Registries.ENCHANTMENT, enchantID);

        Holder<Enchantment> enchantmentEntry = ctx.Registries
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(enchantKey);

        return () -> new LootItemRandomChanceWithEnchantedBonusCondition(
                data.Base,
                new LevelBasedValue.Linear(data.PerLevel, 0.0f),
                enchantmentEntry
        );
    }

    @SuppressWarnings("unused")
    public LootItemCondition.Builder buildFromData(KilledByPlayerConditionData unused) {
        return LootItemKilledByPlayerCondition.killedByPlayer();
    }

    public LootItemCondition.Builder buildFromData(KilledByConditionData data) {
        ResourceLocation killerID = ResourceLocation.parse(data.Killer);
        EntityType<?> killerType = BuiltInRegistries.ENTITY_TYPE.get(killerID);
        return LootItemEntityPropertyCondition.hasProperties(
                data.Direct ? LootContext.EntityTarget.DIRECT_ATTACKER : LootContext.EntityTarget.ATTACKER,
                EntityPredicate.Builder.entity().of(killerType)
        );
    }

    public LootItemCondition.Builder buildFromData(KilledWithItemConditionData data) {
        ResourceLocation itemID = ResourceLocation.parse(data.Item);
        return LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.ATTACKER,
                EntityPredicate.Builder.entity().equipment(
                        EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM.get(itemID)))
                ).build()
        );
    }

    public LootItemCondition.Builder buildFromData(KilledWithItemTagConditionData data) {
        ResourceLocation tagID = ResourceLocation.parse(data.Tag);
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagID);
        return LootItemEntityPropertyCondition.hasProperties(
                LootContext.EntityTarget.ATTACKER,
                EntityPredicate.Builder.entity().equipment(
                        EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(tagKey))
                )
        );
    }

    public LootItemCondition.Builder buildFromData(BiomesConditionData data, IConditionData.ConditionContext ctx) {
        ArrayList<Holder<Biome>> biomeEntries = new ArrayList<>(List.of());
        for (String biome : data.Biomes) {
            ResourceLocation biomeID = ResourceLocation.parse(biome);
            ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, biomeID);

            Holder<Biome> biomeEntry = ctx.Registries
                    .lookupOrThrow(Registries.BIOME)
                    .getOrThrow(biomeKey);
            biomeEntries.addLast(biomeEntry);
        }
        HolderSet<Biome> biomesEntryList = HolderSet.direct(biomeEntries);
        return LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiomes(biomesEntryList));
    }

    public LootItemCondition.Builder buildFromData(SkyConditionData data) {
        return LocationCheck.checkLocation(LocationPredicate.Builder.location().setCanSeeSky(data.CanSeeTheSky));
    }

    public LootItemCondition.Builder buildFromData(LightConditionData data) {
        if (data.Exactly >= 0) {
            return LocationCheck.checkLocation(
                    LocationPredicate.Builder.location().setLight(
                            LightPredicate.Builder.light().setComposite(MinMaxBounds.Ints.exactly(data.Exactly))
                    ));
        }

        if (data.Min < 0 && data.Max >= 0) {
            return LocationCheck.checkLocation(
                    LocationPredicate.Builder.location().setLight(
                            LightPredicate.Builder.light().setComposite(MinMaxBounds.Ints.atMost(data.Max))
                    ));
        }

        if (data.Min >= 0 && data.Max < 0) {
            return LocationCheck.checkLocation(
                    LocationPredicate.Builder.location().setLight(
                            LightPredicate.Builder.light().setComposite(MinMaxBounds.Ints.atLeast(data.Max))
                    ));
        }

        return LocationCheck.checkLocation(
                LocationPredicate.Builder.location().setLight(
                        LightPredicate.Builder.light().setComposite(MinMaxBounds.Ints.between(data.Min, data.Max))
                ));

    }

    @SuppressWarnings("unused")
    public LootItemCondition.Builder buildFromData(SurviveExplosionConditionData unused) {
        return ExplosionCondition.survivesExplosion();
    }




}