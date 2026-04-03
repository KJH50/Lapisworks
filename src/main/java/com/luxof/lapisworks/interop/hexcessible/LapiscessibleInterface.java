package com.luxof.lapisworks.interop.hexcessible;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LapiscessibleInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger("Lapisworks/Hexcessible");
    
    public static void recalibratePWShapeUnlocksInHexcessible() {
        try {
            Class<?> patternEntriesClass = Class.forName("dev.tizu.hexcessible.entries.PatternEntries");
            Class<?> supportClass = Class.forName("com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport");
            Object instance = patternEntriesClass.getField("INSTANCE").get(null);
            
            if (supportClass.isInstance(instance)) {
                java.lang.reflect.Method method = supportClass.getMethod("calibratePWShapeUnlocks");
                method.invoke(instance);
            }
        } catch (ClassNotFoundException e) {
            // Hexcessible not present, ignore
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn("Hexcessible integration unavailable, PWShape patterns may not be unlocked properly");
        } catch (Exception e) {
            LOGGER.warn("Failed to calibrate PWShape unlocks in Hexcessible: {}", e.getMessage());
        }
    }

    public static void unlockPWShapeInHexcessibleByAdvancement(Identifier advancementId) {
        try {
            Class<?> patternEntriesClass = Class.forName("dev.tizu.hexcessible.entries.PatternEntries");
            Class<?> supportClass = Class.forName("com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport");
            Object instance = patternEntriesClass.getField("INSTANCE").get(null);
            
            if (supportClass.isInstance(instance)) {
                java.lang.reflect.Method method = supportClass.getMethod("unlockPWShapeByAdvancement", Identifier.class);
                method.invoke(instance, advancementId);
            }
        } catch (ClassNotFoundException e) {
            // Hexcessible not present, ignore
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn("Hexcessible integration unavailable");
        } catch (Exception e) {
            LOGGER.warn("Failed to unlock PWShape in Hexcessible: {}", e.getMessage());
        }
    }
}
