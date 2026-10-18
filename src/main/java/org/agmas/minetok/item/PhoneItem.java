package org.agmas.minetok.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.level.Level;

public class PhoneItem extends Item {
    public PhoneItem(Properties properties) {
        super(properties.component(DataComponents.USE_EFFECTS, new UseEffects(true,false,1f)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return super.use(level, player, hand);
    }

    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {return ItemUseAnimation.NONE; }

    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return Integer.MAX_VALUE;
    }
}
