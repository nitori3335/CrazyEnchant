package nfactory.crazyenchant.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nfactory.crazyenchant.CrazyEnchant;
import nfactory.crazyenchant.enchantment.*;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CrazyEnchant.MODID);

    public static final RegistryObject<Enchantment> FISHHATER = ENCHANTMENT.register("fish_hater", FishHaterEnchantment::new);
    public static final RegistryObject<Enchantment> MULTIHITS = ENCHANTMENT.register("multihits", MultihitsEnchantment::new);
    public static final RegistryObject<Enchantment> RANDOMEFFECT = ENCHANTMENT.register("random_effect", RandomEffectEnchantment::new);
    public static final RegistryObject<Enchantment> SILKFORTUNE = ENCHANTMENT.register("silkfortune", SilkFortuneEnchantment::new);
    public static final RegistryObject<Enchantment> STEALEFFECT = ENCHANTMENT.register("steal_effect", StealEffectEnchantment::new);
    public static final RegistryObject<Enchantment> SWIFTFLIGHT = ENCHANTMENT.register("swift_flight", SwiftFlightEnchantment::new);
    public static final RegistryObject<Enchantment> WITCHHUNTER = ENCHANTMENT.register("witch_hunter", WitchHunterEnchantment::new);
}
