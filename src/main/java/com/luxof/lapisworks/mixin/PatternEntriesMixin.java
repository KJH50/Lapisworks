package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexDir;

import com.luxof.lapisworks.mixinsupport.AccessPWBookEntries;
import com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport;

import static com.luxof.lapisworks.init.Mutables.Mutables.wizardDiariesGainableAdvancements;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.isPWShapePattern;
import static com.luxof.lapisworks.init.ThemConfigFlags.specificToGenericId;

import dev.tizu.hexcessible.entries.BookEntries;
import dev.tizu.hexcessible.entries.PatternEntries;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(value = PatternEntries.class, remap = false)
public class PatternEntriesMixin implements HexcessiblePWShapeSupport {

    @Shadow private List<PatternEntries.Entry> entries;
    @Shadow private List<String> perWorld;
    @Unique public HashMap<String, PatternEntries.Entry> pwShapePatterns = new HashMap<>();

    @Inject(
        method = "reindex",
        at = @At("HEAD")
    )
    public void lapisworks$clearThosePWShapePatternEntries(CallbackInfo ci) {
        pwShapePatterns.clear();
    }

    @Inject(
        method = "reindex",
        at = @At(
            value = "INVOKE",
            target = "Ldev/tizu/hexcessible/entries/BookEntries;get(Lnet/minecraft/util/Identifier;)Ljava/util/List;"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        cancellable = true
    )
    private void lapisworks$registerPWShapePatternsDifferently(
        RegistryKey<ActionRegistryEntry> key,
        CallbackInfo ci,
        Identifier id,
        String name,
        Supplier<Boolean> checkLock,
        HexDir dir,
        List<List<HexAngle>> sig
    ) {
        String specificId = id.toString();
        String genericId = specificToGenericId.get(specificId);
        if (genericId == null) return;

        if (isPWShapePattern(specificId)) {
            List<BookEntries.Entry> bookEntries = getEntriesForPWShapePattern(genericId);

            pwShapePatterns.put(
                specificId,
                new PatternEntries.Entry(specificId, name, checkLock, dir, sig, bookEntries, 0)
            );
            ci.cancel();
        }
    }

    @Unique
    private static List<BookEntries.Entry> getEntriesForPWShapePattern(String genericId) {
        return ((AccessPWBookEntries)BookEntries.INSTANCE).getEntriesOfPWShapePattern(genericId);
    }

    @Override
    public void unlockPWShapeByAdvancement(Identifier advancementId) {
        String genericId = wizardDiariesGainableAdvancements.get(advancementId);
        if (genericId == null) return;
        int chosen = chosenFlags.get(genericId);

        PatternEntries.Entry entry = pwShapePatterns.get(genericId + String.valueOf(chosen));
        if (entry != null) {
            entries.add(entry);
        }
    }

    @Override
    public void calibratePWShapeUnlocks() {
        for (Identifier advancement : wizardDiariesGainableAdvancements.keySet()) {
            if (ClientAdvancements.hasDone(advancement.toString()))
                unlockPWShapeByAdvancement(advancement);
        }
    }
}
