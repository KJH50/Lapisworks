package com.luxof.lapisworks.interop.hexcessible;

import com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport;

import dev.tizu.hexcessible.entries.PatternEntries;

import net.minecraft.util.Identifier;

public class LapiscessibleInterface {
    public static void recalibratePWShapeUnlocksInHexcessible() {
        try {
            Object instance = PatternEntries.INSTANCE;
            if (instance instanceof HexcessiblePWShapeSupport) {
                ((HexcessiblePWShapeSupport)instance).calibratePWShapeUnlocks();
            }
        } catch (Throwable t) {
            // Silently fail if Hexcessible integration is not available
        }
    }

    public static void unlockPWShapeInHexcessibleByAdvancement(Identifier advancementId) {
        try {
            Object instance = PatternEntries.INSTANCE;
            if (instance instanceof HexcessiblePWShapeSupport) {
                ((HexcessiblePWShapeSupport)instance).unlockPWShapeByAdvancement(advancementId);
            }
        } catch (Throwable t) {
            // Silently fail if Hexcessible integration is not available
        }
    }
}
