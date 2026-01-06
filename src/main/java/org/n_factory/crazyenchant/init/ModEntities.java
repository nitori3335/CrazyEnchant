package org.n_factory.crazyenchant.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.n_factory.crazyenchant.Crazyenchant;
import org.n_factory.crazyenchant.ritual.SummoningGate;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Crazyenchant.MODID);

    public static final RegistryObject<EntityType<SummoningGate>> SUMMONING_GATE = ENTITIES.register("summoning_gate", () ->
            EntityType.Builder.of(SummoningGate::new,MobCategory.MISC)
                .sized(2.0F, 2.0F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build("summoning_gate"));
}
