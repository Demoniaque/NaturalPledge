package shadowfox.botanicaladdons.common.core.helper;

import com.google.common.base.Throwables;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.Item;
import net.minecraft.util.CooldownTracker;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import shadowfox.botanicaladdons.common.lib.LibObfuscation;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.invoke.MethodHandles.publicLookup;

/**
 @author WireSegal
         Created at 10:50 PM on 5/28/16.
 */
@SuppressWarnings("unchecked")
public class BAMethodHandles {

    @Nonnull
    private static final MethodHandle cooldownsGetter;
    @Nonnull
    public static Map getCooldowns(@Nonnull CooldownTracker cooldownTracker) {
        try {
            return (Map) cooldownsGetter.invokeExact(cooldownTracker);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }

    public static void addNewCooldown(@Nonnull CooldownTracker cooldownTracker, @Nonnull Item item, int createTicks, int expireTicks) {
        Map cooldowns = getCooldowns(cooldownTracker);
        cooldowns.put(item, newCooldown(cooldownTracker, createTicks, expireTicks));
    }
    @Nonnull
    private static final MethodHandle cooldownTicksGetter;
    public static int getCooldownTicks(@Nonnull CooldownTracker cooldownTracker) {
        try {
            return (int) cooldownTicksGetter.invokeExact(cooldownTracker);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
    @Nonnull
    private static final MethodHandle cooldownMaker;
    public static @Nonnull Object newCooldown(@Nonnull CooldownTracker tracker, int createTicks, int expireTicks) {
        try {
            return (Object) cooldownMaker.invokeExact(tracker, createTicks, expireTicks);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
    @Nonnull
    public static final Class cooldownClass;
    @Nonnull
    private static final MethodHandle expireTicksGetter;
    public static int getExpireTicks(@Nonnull Object cooldown) {
        try {
            return (int) expireTicksGetter.invokeExact(cooldown);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
    @Nonnull
    private static final MethodHandle createTicksGetter;
    public static int getCreateTicks(@Nonnull Object cooldown) {
        try {
            return (int) createTicksGetter.invokeExact(cooldown);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }

    @Nonnull
    private static final MethodHandle swingTicksGetter;
    public static int getSwingTicks(@Nonnull EntityLivingBase entity) {
        try {
            return (int) swingTicksGetter.invokeExact(entity);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
    @Nonnull
    private static final MethodHandle swingTicksSetter;
    public static void setSwingTicks(@Nonnull EntityLivingBase entity, int ticks) {
        try {
            swingTicksSetter.invokeExact(entity, ticks);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
    @Nonnull
    private static final MethodHandle lightningEffectGetter;
    public static boolean getEffectOnly(@Nonnull EntityLightningBolt entity) {
        try {
            return (boolean) lightningEffectGetter.invokeExact(entity);
        } catch (Throwable t) {
            FMLLog.severe("[BA]: Methodhandle failed!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }

    static {
        try {
            Field f = ReflectionHelper.findField(CooldownTracker.class, LibObfuscation.COOLDOWNTRACKER_COOLDOWNS);
            f.setAccessible(true);
            cooldownsGetter = publicLookup().unreflectGetter(f);

            f = ReflectionHelper.findField(CooldownTracker.class, LibObfuscation.COOLDOWNTRACKER_TICKS);
            f.setAccessible(true);
            cooldownTicksGetter = publicLookup().unreflectGetter(f);

            cooldownClass = Class.forName("net.minecraft.util.CooldownTracker$Cooldown");
            Constructor ctor = cooldownClass.getDeclaredConstructor(CooldownTracker.class, int.class, int.class);
            ctor.setAccessible(true);
            cooldownMaker = publicLookup().unreflectConstructor(ctor).asType(MethodType.methodType(Object.class, CooldownTracker.class, int.class, int.class));

            f = ReflectionHelper.findField(cooldownClass, LibObfuscation.COOLDOWN_EXPIRETICKS);
            f.setAccessible(true);
            expireTicksGetter = publicLookup().unreflectGetter(f).asType(MethodType.methodType(int.class, Object.class));

            f = ReflectionHelper.findField(cooldownClass, LibObfuscation.COOLDOWN_CREATETICKS);
            f.setAccessible(true);
            createTicksGetter = publicLookup().unreflectGetter(f).asType(MethodType.methodType(int.class, Object.class));

            f = ReflectionHelper.findField(EntityLivingBase.class, LibObfuscation.ENTITYLIVINGBASE_TICKSSINCELASTSWING);
            f.setAccessible(true);
            swingTicksGetter = publicLookup().unreflectGetter(f);
            swingTicksSetter = publicLookup().unreflectSetter(f);

            f = ReflectionHelper.findField(EntityLightningBolt.class, LibObfuscation.ENTITYLIGHTNINGBOLT_EFFECTONLY);
            f.setAccessible(true);
            lightningEffectGetter = publicLookup().unreflectGetter(f);

        } catch (Throwable t) {
            FMLLog.severe("[BA]: Couldn't initialize methodhandles! Things will be broken!");
            t.printStackTrace();
            throw Throwables.propagate(t);
        }
    }
}
