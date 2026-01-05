package org.n_factory.crazyenchant.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.enchantment.*;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS,Crazyenchant.MODID);
    public static final RegistryObject<Enchantment> FISHHATER = ENCHANTMENT.register("fishhater", FishhaterEnchantment::new);
    public static final RegistryObject<Enchantment> MULTIHITS = ENCHANTMENT.register("multihits", MultihitsEnchantment::new);
    public static final RegistryObject<Enchantment> RANDOM_EFFECT = ENCHANTMENT.register("random_effect", RandomEffectEnchantment::new);
    public static final RegistryObject<Enchantment> SILKFORTUNE = ENCHANTMENT.register("silkfortune", SilkfortuneEnchantment::new);
    public static final RegistryObject<Enchantment> STEALEFFECT = ENCHANTMENT.register("stealeffect", StealeffectEnchantment::new);
    public static final RegistryObject<Enchantment> SWIFTFLIGHT = ENCHANTMENT.register("swiftflight", SwiftflightEnchantment::new);
    public static final RegistryObject<Enchantment> WITCHHUNTER = ENCHANTMENT.register("witchhunter", WitchhunterEnchantment::new);
}
