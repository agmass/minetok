package org.agmas.minetok.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.KeyboardHandler;
//? if >1.21.1 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//? }
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.agmas.minetok.Minetok;
import org.agmas.minetok.client.MinetokClient;
import org.agmas.minetok.item.PhoneItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class CaptureKeyboardMixin {
    @Inject(method = "keyPress", at = @At(value = "HEAD"), cancellable = true)
    //? if >1.21.1
    public void overrideKeyboard(long l, int i, KeyEvent keyEvent, CallbackInfo ci) {
    //? if <=1.21.1
    //public void overrideKeyboard(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (MinetokClient.phone != null && MinetokClient.phone.selectedElement != null && (i == 1 || i == 2)) {
            MinetokClient.phone.selectedElement.keyPressed(
                    //? if >1.21.1
                    keyEvent
                    //? if <=1.21.1
                    //j,k,m
            );
            MinetokClient.phone.setDirty();
            ci.cancel();
        }
    }

    @Inject(method = "charTyped", at = @At(value = "HEAD"), cancellable = true)
    //? if >1.21.1
    public void overrideKeyboard2(long l, CharacterEvent characterEvent, CallbackInfo ci) {
    //? if <=1.21.1
    //public void overrideKeyboard2(long l, int i, int j, CallbackInfo ci) {
        if (MinetokClient.phone != null && MinetokClient.phone.selectedElement != null) {
            MinetokClient.phone.selectedElement.charTyped(
                    //? if >1.21.1
                    characterEvent
                    //? if <=1.21.1
                    //(char)i,j
            );
            MinetokClient.phone.setDirty();
            ci.cancel();
        }
    }
}
