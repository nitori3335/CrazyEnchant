package org.n_factory.crazyenchant.procedures;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.n_factory.crazyenchant.init.ModEnchantments;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber
public class CreativeFlightAccelerationProcedure {

    private static final float DEFAULT_SPEED = 0.05f;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        if (!player.getAbilities().flying) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.SWIFTFLIGHT.get(), chest);

        float targetSpeed;
        if (level > 0) {
            float speedMultiplier = 1.0f + 0.5f * level;
            targetSpeed = DEFAULT_SPEED * speedMultiplier;
        } else {
            targetSpeed = DEFAULT_SPEED;
        }

        try {
            Field flyingSpeedField = Abilities.class.getDeclaredField("flyingSpeed");
            flyingSpeedField.setAccessible(true);
            float current = flyingSpeedField.getFloat(player.getAbilities());

            if (Math.abs(current - targetSpeed) > 0.0001f) {
                flyingSpeedField.setFloat(player.getAbilities(), targetSpeed);
            }
        } catch (Exception ignored) {
        }
    }
}