package nfactory.crazyenchant.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.enchantment.FishHaterEnchantment;
import nfactory.crazyenchant.enchantment.MultihitsEnchantment;
import nfactory.crazyenchant.enchantment.RandomEffectEnchantment;
import nfactory.crazyenchant.enchantment.SilkFortuneEnchantment;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CrazyEnchant.MODID);

    public static final RegistryObject<Enchantment> FISHHATER = ENCHANTMENT.register("fish_hater", FishHaterEnchantment::new);
    public static final RegistryObject<Enchantment> MULTIHITS = ENCHANTMENT.register("multihits", MultihitsEnchantment::new);
    public static final RegistryObject<Enchantment> RANDOM_EFFECT = ENCHANTMENT.register("random_effect", RandomEffectEnchantment::new);
    public static final RegistryObject<Enchantment> SILKFORTUNE = ENCHANTMENT.register("silkfortune", SilkFortuneEnchantment::new);
}
