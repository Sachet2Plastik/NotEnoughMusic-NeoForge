package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

public interface IConditionData {
    class ConditionContext {
        ResourceKey<LootTable> Key;
        LootTable.Builder TableBuilder;
        Object Source;
        HolderLookup.Provider Registries;

        public ConditionContext(ResourceKey<LootTable> key, LootTable.Builder tableBuilder, Object source, HolderLookup.Provider registries) {
            Key = key;
            TableBuilder = tableBuilder;
            Source = source;
            Registries = registries;
        }
    }
    LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx);
}
