package sachet2plastik.notenoughmusic.loottable.conditiondata;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class BiomesConditionData implements IConditionData {
    List<String> Biomes;


    public BiomesConditionData(List<String> biomes) {
        Biomes = biomes;
    }


    @Override
    public LootItemCondition.Builder convert(ConditionDataConverter dataConverter, ConditionContext ctx) {
        return dataConverter.buildFromData(this, ctx);
    }
}
