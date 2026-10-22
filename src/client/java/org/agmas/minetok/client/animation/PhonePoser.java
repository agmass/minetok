package org.agmas.minetok.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;
import org.agmas.minetok.client.MinetokClient;

public class PhonePoser {
    public static void poseMainArm(ModelPart arm, float castTime) {
        arm.xRot *= 0.15f;
        arm.zRot *= 0.1f;
        arm.xRot *= 0.1f;
        arm.yRot -= 0.5f;
        arm.xRot -= 1.2f;
    }

    public static void poseHeadDown(ModelPart arm) {
        arm.xRot = Mth.lerp(MinetokClient.phone.mouse.y/1280f, -0.1f, 0.5f);
        arm.yRot = Mth.lerp(MinetokClient.phone.mouse.x/720f, -0.3f, 0.3f);
    }
}
