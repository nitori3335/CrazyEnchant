package nfactory.crazyenchant.data.result;

import nfactory.crazyenchant.data.dto.StolenEffectData;

import java.util.ArrayList;
import java.util.List;

public class StealResult {
    private final List<StolenEffectData> stolenEffects = new ArrayList<>();

    private boolean anyStolen = false;

    public void addStolenEffect(StolenEffectData stolenEffectData) {
        stolenEffects.add(stolenEffectData);
        anyStolen = true;
    }

    public List<StolenEffectData> getStolenEffects() { return stolenEffects; }
    public boolean isAnyStolen() { return anyStolen; }
    public int size() { return stolenEffects.size(); }
}
