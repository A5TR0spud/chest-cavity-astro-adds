package net.astrospud.ccastroadds.util;

import net.astrospud.ccastroadds.registration.CCAAItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.tigereye.chestcavity.ChestCavity;
import net.tigereye.chestcavity.chestcavities.ChestCavityInventory;
import net.tigereye.chestcavity.chestcavities.instance.ChestCavityInstance;

public class AstralCavityUtil {
    public static void growBackOrgans(LivingEntity entity, ChestCavityInstance cc, float count) {
        count = MathHelper.ceil(count);
        ChestCavityInventory def = cc.getChestCavityType().getDefaultChestCavity();
        for (int i = 0; i < def.size() && count > 0; i++) {
            ItemStack organHas = cc.inventory.getStack(i);
            ItemStack organDefault = def.getStack(i);
            organDefault.setCount(organDefault.getMaxCount());
            if (organHas.isEmpty()) {
                if (organDefault.isEmpty() || entity.getRandom().nextFloat() <= 0.25 || organDefault.getItem() == Items.DIRT) {
                    Item[] tumors = {CCAAItems.BENIGN_TUMOR, CCAAItems.AUTOPHAGY_TUMOR};
                    organDefault = tumors[entity.getRandom().nextInt(tumors.length-1)].getDefaultStack();
                }
                if (!organDefault.isEmpty() && !(entity instanceof PlayerEntity)) {
                    NbtCompound tag = new NbtCompound();
                    tag.putUuid("owner", cc.compatibility_id);
                    tag.putString("name", cc.owner.getDisplayName().getString());
                    organDefault.setSubNbt(ChestCavity.COMPATIBILITY_TAG.toString(), tag);
                }
                cc.inventory.setStack(i, organDefault);
                if (entity instanceof PlayerEntity player) {
                    if (organDefault.isFood() && organDefault.getItem().getFoodComponent() != null) {
                        player.addExhaustion(organDefault.getItem().getFoodComponent().getHunger()+organDefault.getItem().getFoodComponent().getSaturationModifier());
                    } else {
                        player.addExhaustion(0.25f);
                    }
                }
                count--;
            }
        }
    }

    public static void eatOrgans(LivingEntity entity, ChestCavityInstance cc, float count) {
        count = MathHelper.ceil(count);
        ChestCavityInventory def = cc.getChestCavityType().getDefaultChestCavity();
        for (int i = 0; i < def.size() && count > 0; i++) {
            ItemStack organHas = cc.inventory.getStack(i);
            if (organHas.isFood()) {
                //FoodComponent food = organHas.getItem().getFoodComponent();
                for (int g = 0; g < organHas.getCount() && count > 0; g++) {
                    if (entity instanceof PlayerEntity player) {
                        if (player.getHungerManager().isNotFull()) {
                            player.eatFood(player.getWorld(), organHas);
                            organHas.decrement(1);
                            cc.inventory.setStack(i, organHas);
                        }
                    } else {
                        entity.heal(1);
                        organHas.decrement(1);
                        cc.inventory.setStack(i, organHas);
                    }
                    count--;
                }
            }
        }
    }
}
