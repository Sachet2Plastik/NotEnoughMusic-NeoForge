package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class KilledWithItemTagConditionData implements IConditionData {
    String Tag;

    public KilledWithItemTagConditionData(String tag) {
        Tag = tag;
    }

    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }

}
