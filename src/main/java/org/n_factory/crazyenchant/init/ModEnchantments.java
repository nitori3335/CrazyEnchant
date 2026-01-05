package org.n_factory.crazyenchant.init;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.enchantment.*;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS,Crazyenchant.MODID);
    public static final RegistryObject<Enchantment> FISHHATER = ENCHANTMENT.register("fishhater", () -> new FishhaterEnchantment());
    public static final RegistryObject<Enchantment> MULTIHITS = ENCHANTMENT.register("multihits", () -> new MultihitsEnchantment());
    public static final RegistryObject<Enchantment> RANDOM_EFFECT = ENCHANTMENT.register("random_effect", () -> new RandomEffectEnchantment());
    public static final RegistryObject<Enchantment> SILKFORTUNE = ENCHANTMENT.register("silkfortune", () -> new SilkfortuneEnchantment());
    public static final RegistryObject<Enchantment> STEALEFFECT = ENCHANTMENT.register("stealeffect", () -> new StealeffectEnchantment());
    public static final RegistryObject<Enchantment> SWIFTFLIGHT = ENCHANTMENT.register("swiftflight", () -> new SwiftflightEnchantment());
    public static final RegistryObject<Enchantment> WITCHHUNTER = ENCHANTMENT.register("witchhunter", () -> new WitchhunterEnchantment());
}
