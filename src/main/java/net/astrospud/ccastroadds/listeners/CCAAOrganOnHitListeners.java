package net.astrospud.ccastroadds.listeners;

import net.astrospud.ccastroadds.registration.CCAAOrganScores;
import net.astrospud.ccastroadds.registration.CCAAStatusEffects;
import net.astrospud.ccastroadds.specials.AmethystExplosion;
import net.astrospud.ccastroadds.specials.ClusterExplosion;
import net.astrospud.ccastroadds.specials.ShriekerExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.tigereye.chestcavity.ChestCavity;
import net.tigereye.chestcavity.chestcavities.instance.ChestCavityInstance;
import net.tigereye.chestcavity.interfaces.ChestCavityEntity;
import net.tigereye.chestcavity.listeners.OrganOnHitCallback;
import net.tigereye.chestcavity.listeners.OrganOnHitListeners;
import net.tigereye.chestcavity.listeners.OrganTickCallback;
import net.tigereye.chestcavity.registration.CCOrganScores;

public class CCAAOrganOnHitListeners {
    public static void register(){
        OrganOnHitCallback.EVENT.register(CCAAOrganOnHitListeners::TickPanic);
        OrganOnHitCallback.EVENT.register(CCAAOrganOnHitListeners::TickResonance);
        OrganOnHitCallback.EVENT.register(CCAAOrganOnHitListeners::TickShrieking);
        OrganOnHitCallback.EVENT.register(CCAAOrganOnHitListeners::TickClusterExplode);
    }

    public static void TickPanic(LivingEntity attacker, LivingEntity entity, ChestCavityInstance notused){
        if (!(entity instanceof ChestCavityEntity)) { return; }
        ChestCavityInstance cc = ((ChestCavityEntity)entity).getChestCavityInstance();
        float panic = cc.getOrganScore(CCAAOrganScores.PANIC) - cc.getChestCavityType().getDefaultOrganScore(CCAAOrganScores.PANIC);
        if (panic <= 0 || entity.hasStatusEffect(CCAAStatusEffects.PANIC)) { return; }
        int power = (int) Math.floor(2 * Math.log10((panic) + 1));
        int duration = (int)(100 * Math.log10(2*panic -1))+100;
        entity.addStatusEffect(new StatusEffectInstance(CCAAStatusEffects.PANIC, duration, power, false, true, true));
    }

    public static void TickResonance(LivingEntity attacker, LivingEntity entity, ChestCavityInstance notused){
        if (!(entity instanceof ChestCavityEntity)) { return; }
        ChestCavityInstance cc = ((ChestCavityEntity)entity).getChestCavityInstance();
        float resonance = cc.getOrganScore(CCAAOrganScores.RESONANCE) - cc.getChestCavityType().getDefaultOrganScore(CCAAOrganScores.RESONANCE);
        float shrieking = cc.getOrganScore(CCAAOrganScores.SHRIEKING) - cc.getChestCavityType().getDefaultOrganScore(CCAAOrganScores.SHRIEKING);
        resonance -= shrieking;
        if (resonance <= 0) {
            return;
        }
        if (!entity.hasStatusEffect(CCAAStatusEffects.RESONANCE_COOLDOWN)) {
            entity.addStatusEffect(new StatusEffectInstance(CCAAStatusEffects.RESONANCE_COOLDOWN, (int)(75/(resonance/2)),0, false, false, true));

            Position entityPos = entity.getPos();
            double x = entityPos.getX();
            double y = entityPos.getY();
            double z = entityPos.getZ();
            float power = 5;
            World entityWorld = entity.getWorld();

            AmethystExplosion explosion = new AmethystExplosion(entityWorld, entity, x, y, z, power);
            explosion.collectBlocksAndDamageEntities();
            if (!entityWorld.isClient) {
                float rand = entityWorld.random.nextFloat() * 1.2F;
                entityWorld.playSound((PlayerEntity)null, entity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.BLOCKS, 7.0F, 0.5F + rand);
                entityWorld.playSound((PlayerEntity)null, entity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 7.0F, 0.5F + rand);
                entityWorld.playSound((PlayerEntity)null, entity.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 2.0F, 2.5F + rand);
                //entityWorld.playSound((PlayerEntity) null, entity.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.75F, 3F + entityWorld.random.nextFloat() * 0.4F);
            }
        }
    }

    public static void TickShrieking(LivingEntity attacker, LivingEntity entity, ChestCavityInstance notused){
        if (!(entity instanceof ChestCavityEntity)) { return; }
        ChestCavityInstance cc = ((ChestCavityEntity)entity).getChestCavityInstance();
        float shrieking = cc.getOrganScore(CCAAOrganScores.SHRIEKING) - cc.getChestCavityType().getDefaultOrganScore(CCAAOrganScores.SHRIEKING);

        if (shrieking <= 0) {
            return;
        }
        if (!entity.hasStatusEffect(CCAAStatusEffects.SHRIEKING_COOLDOWN)) {
            entity.addStatusEffect(new StatusEffectInstance(CCAAStatusEffects.SHRIEKING_COOLDOWN, (int)(75/(shrieking/2)),0, false, false, true));

            Position entityPos = entity.getPos();
            double x = entityPos.getX();
            double y = entityPos.getY();
            double z = entityPos.getZ();
            float power = 5;
            World entityWorld = entity.getWorld();

            ShriekerExplosion explosion = new ShriekerExplosion(entityWorld, entity, x, y, z, power);
            explosion.collectBlocksAndDamageEntities();
            if (!entityWorld.isClient) {
                float rand = entityWorld.random.nextFloat() * 0.25F;
                entityWorld.playSound((PlayerEntity)null, entity.getBlockPos(), SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK, SoundCategory.BLOCKS, 2.0F, 0.8F + rand);
                //entityWorld.playSound((PlayerEntity) null, entity.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.75F, 3F + entityWorld.random.nextFloat() * 0.4F);
            }
        }
    }

    public static void TickClusterExplode(LivingEntity attacker, LivingEntity entity, ChestCavityInstance notused){
        if (!(entity instanceof ChestCavityEntity)) { return; }
        ChestCavityInstance cc = ((ChestCavityEntity)entity).getChestCavityInstance();
        float clusterBomb = cc.getOrganScore(CCAAOrganScores.CLUSTEREXPLODE) - cc.getChestCavityType().getDefaultOrganScore(CCAAOrganScores.CLUSTEREXPLODE);

        if (clusterBomb <= 0) {
            return;
        }
        Position entityPos = entity.getPos();
        double x = entityPos.getX();
        double y = entityPos.getY();
        double z = entityPos.getZ();
        float power = 5;
        World entityWorld = entity.getWorld();

        ClusterExplosion explosion = new ClusterExplosion(entityWorld, entity, x, y, z, power);
        explosion.collectBlocksAndDamageEntities();
    }
}
