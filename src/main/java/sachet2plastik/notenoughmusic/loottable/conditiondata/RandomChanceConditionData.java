package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class RandomChanceConditionData implements IConditionData {
    Float Chance;

    public RandomChanceConditionData(Float chance) {
        Chance = chance;
    }

    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }

}
