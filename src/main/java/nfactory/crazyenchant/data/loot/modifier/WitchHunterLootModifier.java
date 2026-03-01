package nfactory.crazyenchant.data.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import nfactory.crazyenchant.init.ModEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class WitchHunterLootModifier extends LootModifier {

    public static final Codec<WitchHunterLootModifier> CODEC =
            RecordCodecBuilder.create(instance ->
                    codecStart(instance).apply(instance, WitchHunterLootModifier::new)
            );

    protected WitchHunterLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot, LootContext context
    ) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Witch)) return generatedLoot;

        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        if (!(killer instanceof Player player)) return generatedLoot;

        ItemStack weapon = player.getMainHandItem();
        int enchantCount = weapon.getEnchantmentLevel(ModEnchantments.WITCHHUNTER.get());
        if (enchantCount <= 0) return generatedLoot;

        ItemStack book = createRandomEnchantBook(context, enchantCount);

        generatedLoot.add(book);
        return generatedLoot;
    }

    private static @NotNull ItemStack createRandomEnchantBook(LootContext context, int enchantCount) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

        var allEnchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());
        var random = context.getRandom();

        int count = Math.min(enchantCount, allEnchantments.size());

        for (int i = 0; i < count; i++) {
            int index = random.nextInt(allEnchantments.size());
            var enchant = allEnchantments.remove(index);

            int maxAllowed = Math.min(enchant.getMaxLevel(), 10);
            int level = 1 + random.nextInt(maxAllowed);

            book.enchant(enchant, level);
        }
        return book;
    }

    @Override
    public Codec<? extends IGlobalLootModifier>  codec() { return WitchHunterLootModifier.CODEC; }
}
