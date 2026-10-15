package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;


public class SkyConditionData implements IConditionData {
    Boolean CanSeeTheSky;

    public SkyConditionData(Boolean canSeeTheSky) {
        CanSeeTheSky = canSeeTheSky;
    }


    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this);
    }
}
