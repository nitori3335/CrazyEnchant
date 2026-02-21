package nfactory.crazyenchant.data.dto;

import java.util.List;

public record EffectFilterRule(
        boolean allowBeneficial,
        boolean allowHarmful,
        boolean allowNeutral,
        List<String> whitelist,
        List<String> blacklist
) {}
