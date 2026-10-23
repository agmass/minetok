package org.agmas.minetok.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.agmas.minetok.init.ModAttachments;

public class RingtoneSoundInstance extends AbstractTickableSoundInstance {
    Player player;
    public RingtoneSoundInstance(Player player, SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
        super(soundEvent, soundSource, randomSource);
        this.player = player;
        looping = true;
        delay = 0;
        volume = 1f;
        relative = false;
        pitch = randomSource.nextIntBetweenInclusive(95, 105)/100f;

    }

    @Override
    public void tick() {
        if (player == null || player.isRemoved()) {
            stop();
            return;
        }
        if (player.getAttached(ModAttachments.RINGING_ATTACHMENT_TYPE) == null) {
            stop();
            return;
        }
        x = player.getX();
        y = player.getY();
        z = player.getZ();
    }
}
