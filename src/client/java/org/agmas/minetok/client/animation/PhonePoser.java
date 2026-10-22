package org.agmas.minetok.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;
import org.agmas.minetok.api.PhoneTapInformation;
import org.agmas.minetok.client.MinetokClient;

public class PhonePoser {
    public static void poseMainArm(ModelPart arm, float castTime) {
        arm.xRot *= 0.15f;
        arm.zRot *= 0.1f;
        arm.xRot *= 0.1f;
        arm.yRot -= 0.5f;
        arm.xRot -= 1.2f;
    }

    public static void poseHeadDown(ModelPart arm, PhoneTapInformation mouse, float tickDelta, boolean right) {
        arm.xRot = Mth.lerp(Mth.lerp(tickDelta, mouse.prevY, mouse.y)/1280f, -0.1f, 0.5f);
        arm.yRot = Mth.lerp(Mth.lerp(tickDelta, mouse.prevX, mouse.x)/720f, (right ? 1 : -1) * -0.3f, (right ? 1 : -1) * 0.3f);
    }
}
