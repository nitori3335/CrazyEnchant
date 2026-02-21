package nfactory.crazyenchant.util;

public class LootContextHelper {
    public static final ThreadLocal<Integer> CURRENT_ENCHANT_LEVEL = ThreadLocal.withInitial(() -> 0);

    public static void setEnchantLevel(int level) {
        CURRENT_ENCHANT_LEVEL.set(level);
    }

    public static int getEnchantLevel() {
        return CURRENT_ENCHANT_LEVEL.get();
    }
}