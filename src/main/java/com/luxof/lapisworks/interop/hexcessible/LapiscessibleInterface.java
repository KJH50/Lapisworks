package com.luxof.lapisworks.interop.hexcessible;

import com.luxof.lapisworks.init.ThemConfigFlags;

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
            unlockAllPWShapePatterns();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.warn("Hexcessible integration unavailable, PWShape patterns may not be unlocked properly");
            unlockAllPWShapePatterns();
        } catch (Exception e) {
            LOGGER.warn("Failed to calibrate PWShape unlocks in Hexcessible: {}", e.getMessage());
            unlockAllPWShapePatterns();
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
    
    @SuppressWarnings("unchecked")
    private static void unlockAllPWShapePatterns() {
        try {
            Class<?> patternEntriesClass = Class.forName("dev.tizu.hexcessible.entries.PatternEntries");
            Object instance = patternEntriesClass.getField("INSTANCE").get(null);
            
            java.lang.reflect.Field entriesField = patternEntriesClass.getDeclaredField("entries");
            entriesField.setAccessible(true);
            java.util.List<Object> entriesList = (java.util.List<Object>) entriesField.get(instance);
            
            for (String patternId : ThemConfigFlags.pwShapePatterns) {
                Class<?> entryClass = Class.forName("dev.tizu.hexcessible.entries.PatternEntries$Entry");
                Object entry = entryClass.getDeclaredConstructor(String.class).newInstance(patternId + "0");
                entriesList.add(entry);
            }
            LOGGER.info("Unlocked {} PWShape patterns", ThemConfigFlags.pwShapePatterns.size());
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Hexcessible PatternEntries class not found, PWShape patterns will not be unlocked");
        } catch (Exception e) {
            LOGGER.warn("Failed to unlock PWShape patterns: {}", e.getMessage());
        }
    }
}
