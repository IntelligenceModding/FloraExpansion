package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.artemis.floraexpansion.common.util.SaltingTubParticleEffects;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Timeline-local particles: independent of frame rate, and never added to the player's world. */
final class SaltingRecipeParticles {
    void submit(SaltingRecipeRenderer.State state, PoseStack poses, SubmitNodeCollector collector) {
        for (int layer = 0; layer < 4; layer++) {
            itemBurst(state.food(), state.time() - (1.7F + layer), SaltingTubParticleEffects.FOOD_COUNT,
                    41 + layer, ContentsFillRenderer.height((layer+1)/4F), poses, collector);
        }
        itemBurst(new ItemStack(de.artemis.floraexpansion.common.registry.ModItems.RAW_SALT.get()),
                state.time() - 6F, SaltingTubParticleEffects.SALT_COUNT, 83, 1F, poses, collector);
        finishBurst(state.time() - 9F, poses, collector);
    }

    private void itemBurst(ItemStack ingredient, float seconds, int count, int seed, float surface,
                           PoseStack poses, SubmitNodeCollector collector) {
        if (seconds < 0 || seconds >= 2.1F || ingredient.isEmpty()) return;
        var mc = Minecraft.getInstance();
        var item = new ItemStackRenderState();
        // BreakingItemParticle resolves the GROUND model, rather than the GUI model.
        mc.getItemModelResolver().updateForTopItem(item, ingredient, ItemDisplayContext.GROUND, mc.level, null, 0);
        var random = RandomSource.create(seed);
        var material = item.pickParticleMaterial(random);
        if (material == null) return;
        var sprite = material.sprite();
        float ticks = seconds * 20;
        for (int i = 0; i < count; i++) {
            var emission = SaltingTubParticleEffects.addition(random);
            int lifetime = (int)(4F / (random.nextFloat()*0.9F + 0.1F));
            var velocity = initialVelocity(random);
            // BreakingItemParticle halves SingleQuadParticle's 0.1..0.2 block half-size.
            float size = 0.1F * (random.nextFloat()*0.5F + 0.5F);
            float u = random.nextFloat()*0.75F, v = random.nextFloat()*0.75F;
            if (ticks >= lifetime) continue;
            var pos = position(emission, velocity, ticks, 1F, 0.98F, surface);
            billboard(poses,collector,sprite.atlasLocation(),pos[0],pos[1],pos[2],size,
                    sprite.getU(u+0.25F),sprite.getV(v),sprite.getU(u),sprite.getV(v+0.25F),0xFFFFFFFF);
        }
    }

    private void finishBurst(float seconds, PoseStack poses, SubmitNodeCollector collector) {
        if (seconds < 0 || seconds >= 1.3F) return;
        var random = RandomSource.create(127);
        float ticks = seconds * 20;
        for (int i = 0; i < SaltingTubParticleEffects.FINISH_COUNT; i++) {
            var emission = SaltingTubParticleEffects.finish(random);
            var velocity = initialVelocity(random);
            // DustParticleBase: SingleQuadParticle size times 0.75 times the supplied scale.
            float size = 0.1F * (random.nextFloat()*0.5F+0.5F)*2F
                    * 0.75F * SaltingTubParticleEffects.FINISH_SCALE;
            int baseLifetime = (int)(8D / (random.nextDouble()*0.8D+0.2D));
            int lifetime = (int)Math.max(baseLifetime*SaltingTubParticleEffects.FINISH_SCALE,1F);
            float brightness = random.nextFloat()*0.4F+0.6F;
            int rgb = SaltingTubParticleEffects.FINISH_COLOR;
            int red = (int)((rgb >> 16 & 255)*brightness*(random.nextFloat()*0.2F+0.8F));
            int green = (int)((rgb >> 8 & 255)*brightness*(random.nextFloat()*0.2F+0.8F));
            int blue = (int)((rgb & 255)*brightness*(random.nextFloat()*0.2F+0.8F));
            if (ticks >= lifetime) continue;
            // Dust grows quickly at birth; vanilla does not fade or shrink the quad at the end.
            size *= Math.min(1, ticks/lifetime*32F);
            int frame = Math.min(7, (int)ticks * 7 / lifetime);
            var texture = Identifier.withDefaultNamespace("textures/particle/generic_"+(7-frame)+".png");
            var pos = position(emission,velocity,ticks,0F,0.96F,1F);
            billboard(poses,collector,texture,pos[0],pos[1],pos[2],size,0,0,1,1,
                    0xFF000000 | red << 16 | green << 8 | blue);
        }
    }

    /** Particle's randomized starting motion, followed by the 0.1 multiplier used by both effects. */
    private static double[] initialVelocity(RandomSource random) {
        double x = (random.nextFloat()*2-1)*0.4F;
        double y = (random.nextFloat()*2-1)*0.4F;
        double z = (random.nextFloat()*2-1)*0.4F;
        double speed = (random.nextFloat()+random.nextFloat()+1)*0.15F;
        double length = Math.max(1.0e-9, Math.sqrt(x*x+y*y+z*z));
        return new double[]{x/length*speed*0.04, (y/length*speed*0.4+0.1)*0.1, z/length*speed*0.04};
    }

    /** Vanilla's tick motion in block units, with local tub collisions and render interpolation. */
    private static float[] position(SaltingTubParticleEffects.Emission spawn, double[] motion,
                                     float ticks, float gravity, float friction, float surface) {
        double x=spawn.x(), y=spawn.y(), z=spawn.z();
        double vx=motion[0]+spawn.vx(), vy=motion[1]+spawn.vy(), vz=motion[2]+spawn.vz();
        double oldX=x, oldY=y, oldZ=z;
        int whole=(int)ticks;
        for (int step=0; step<=whole; step++) {
            oldX=x; oldY=y; oldZ=z;
            vy -= 0.04*gravity;
            x += vx; y += vy; z += vz;
            if (y < surface) { y=surface; vy=0; vx*=0.7; vz*=0.7; }
            // The preview's removed walls still bound the contents as they do in the world tub.
            if (y < 1) {
                x=Math.clamp(x,0.225,0.775);
                z=Math.clamp(z,0.225,0.775);
            }
            vx*=friction; vy*=friction; vz*=friction;
        }
        float partial=ticks-whole;
        return new float[]{(float)(oldX+(x-oldX)*partial), (float)(oldY+(y-oldY)*partial),
                (float)(oldZ+(z-oldZ)*partial)};
    }
    private void billboard(PoseStack poses, SubmitNodeCollector collector, Identifier texture,
                            float x,float y,float z,float size, float u0,float v0,float u1,float v1,int color) {
        poses.pushPose();
        poses.translate(x,y,z);
        // Undo the scene rotation so each fragment faces the JEI camera.
        poses.mulPose(Axis.YP.rotationDegrees(-SaltingRecipeRenderer.YAW));
        poses.mulPose(Axis.XP.rotationDegrees(-SaltingRecipeRenderer.PITCH));
        collector.submitCustomGeometry(poses, RenderTypes.entityTranslucent(texture), (pose,out) -> {
            out.addVertex(pose,-size,size,0).setColor(color).setUv(u0,v0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose,0,0,1);
            out.addVertex(pose,-size,-size,0).setColor(color).setUv(u0,v1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose,0,0,1);
            out.addVertex(pose,size,-size,0).setColor(color).setUv(u1,v1)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose,0,0,1);
            out.addVertex(pose,size,size,0).setColor(color).setUv(u1,v0)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose,0,0,1);
        });
        poses.popPose();
    }
}
