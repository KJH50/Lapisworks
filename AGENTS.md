# Lapisworks - Agent Guidelines

## Project Overview
Lapisworks is a Minecraft mod for Hex Casting that adds new spells, items, and mechanics.
It uses Fabric mod loader with Mixin for bytecode manipulation.

## Build Commands

### Gradle Tasks
```bash
# Build the mod
./gradlew build

# Run Minecraft with the mod (client)
./gradlew runClient

# Run Minecraft with the mod (server)
./gradlew runServer

# Clean build artifacts
./gradlew clean

# Rebuild mappings and sources
./gradlew remapSourcesJar

# Build without tests
./gradlew build -x test
```

### Single File/Small Changes
```bash
# Build only the main jar
./gradlew jar

# Build and install to local maven
./gradlew publishToMavenLocal
```

### Development
```bash
# Generate IDE project files
./gradlew idea        # IntelliJ IDEA
./gradlew eclipse      # Eclipse

# Watch mode (auto-rebuild on changes)
./gradlew build --watch

# Debug run with suspend
./gradlew runClient --suspend
```

## Code Style Guidelines

### General Principles
- **Readability First**: Code is read more than written
- **Consistency**: Follow existing patterns in the codebase
- **Simplicity**: Prefer simple solutions over clever ones
- **Documentation**: Document non-obvious decisions

### Java Conventions

#### Naming Conventions
| Element | Convention | Example |
|---------|-----------|---------|
| Classes | PascalCase | `LivingEntityMixin` |
| Methods | camelCase | `applyPendingAttributes` |
| Fields | camelCase | `needsAttrSync` |
| Constants | camelCase (no underscores) | `fireyFists` |
| Packages | lowercase | `com.luxof.lapisworks.mixin` |
| Mixin methods | Prefix with mod name | `lapisworks$tickApplyPendingAttributes` |

#### Import Organization
1. Standard library imports
2. Third-party imports (Fabric, Mixin, etc.)
3. Minecraft imports
4. Project imports
5. Static imports

Use wildcard imports sparingly (`java.util.*` acceptable).

#### Formatting Rules
- **Indentation**: 4 spaces (no tabs)
- **Line length**: Soft limit 120 characters
- **Braces**: Same-line style
  ```java
  if (condition) {
      doSomething();
  }
  ```
- **Spacing**: Space after keywords, around operators
  ```java
  if (a != null && b > 0) { ... }
  ```
- **Final fields**: Always use `final` for constants and constructor-assigned fields

#### Mixin Conventions
- Use `@Unique` annotation for all mixin-added fields and methods
- Use mod-prefixed names for injected methods: `lapisworks$methodName`
- Prefer `@Inject(at = @At("HEAD"))` for initialization logic
- Use `@At("TAIL")` only when necessary (e.g., ensuring other mods are initialized)
- Avoid modifying `@Final` shadow fields directly

#### Annotations Style
```java
// Method annotations - one per line
@Inject(at = @At("HEAD"), method = "methodName")
public void methodName(CallbackInfo ci) { }

// Field annotations - inline when simple
@Shadow @Final
private AttributeContainer attributes;

@Unique
private boolean needsAttrSync = false;
```

### Performance Guidelines

#### Do's
- Use `List.copyOf()` when returning internal lists
- Add null checks before expensive operations
- Use primitive types where possible (`int` not `Integer`)
- Cache computed values when they're used repeatedly

#### Don'ts
- Don't allocate new objects in hot paths (tick, render)
- Don't iterate with for-each on primitive arrays
- Don't create streams for simple operations
- Avoid reflection in frequently-called code

### Error Handling

#### Null Checks
```java
// Prefer early returns
if (attributes == null) return;

// Use Optional for methods that may not have a value
Optional<EntityAttributeInstance> attr = attributes.getCustomInstance(attrType);
```

#### Exception Handling
- Use specific exceptions, not `Exception`
- Log exceptions with context before rethrowing
- Never swallow exceptions silently

### Mod Compatibility Guidelines

#### Attribute Manipulation
- **CRITICAL**: Never modify `AttributeContainer` during entity initialization
- Use deferred application pattern: store data → apply in `tick()`
- Check `this.getWorld() != null && !this.getWorld().isClient` before server-side operations

#### Mixin Conflicts
- Test with other major mods (Create, Architectury, etc.)
- If target class not found, the mixin is silently skipped
- Use conditional loading with `MixinConfigPlugin` when possible

#### NBT Data
- Always handle missing/corrupted NBT gracefully
- Use default values when NBT data is invalid
- Validate data ranges before applying

## Architecture Notes

### Key Classes
| Class | Purpose |
|-------|---------|
| `LapisworksInterface` | Main interface for Amel enhancements |
| `LivingEntityMixin` | Core attribute/enchantment logic |
| `EntityMixin` | Spell effect handling |
| `ServerWorldMixin` | Ritual and tuneable tracking |

### Data Flow
1. Player casts spell → NBT stored on item
2. Item used on target → `LapisworksInterface` methods called
3. World save → `writeCustomDataToNbt` saves state
4. World load → `readCustomDataFromNbt` restores state
5. Entity tick → Deferred attributes applied

## Testing Checklist

Before submitting changes:
- [ ] Build succeeds without errors
- [ ] Mod loads in Minecraft without crashes
- [ ] New spells/items work correctly
- [ ] NBT save/load works
- [ ] No console errors or warnings
- [ ] Compatible with Create-Delight-Remake modpack
- [ ] No performance regressions (profile if uncertain)
- [ ] Cross-mod interactions tested (if applicable)

## Useful Resources
- [Fabric Wiki](https://fabricmc.net/wiki/)
- [Mixin Documentation](https://github.com/SpongePowered/Mixin/wiki)
- [Hex Casting Modding Guide](https://github.com/gamma-delta/HexMod)
- [Fabric Loom Documentation](https://fabricmc.net/wiki/documentation:fabric_loom)

## Contact
- Author: Luxof
- GitHub: https://github.com/Real-Luxof/Lapisworks
- Issues: Report bugs via GitHub Issues
