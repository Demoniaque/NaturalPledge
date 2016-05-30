package shadowfox.botanicaladdons.common.block.dendrics.thunder

import net.minecraft.block.material.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.effect.EntityWeatherEffect
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.EnumDifficulty
import net.minecraft.world.World
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityStruckByLightningEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import shadowfox.botanicaladdons.common.core.helper.BAMethodHandles
import java.util.*

/**
 * @author WireSegal
 * Created at 6:33 PM on 5/28/16.
 */
object ThunderEventHandler {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    val MAXRANGE = 8
    val FIRERANGE = 3

    @SubscribeEvent
    fun catchWorldTick(e: TickEvent.WorldTickEvent) {
        val toRemove = mutableListOf<Entity>()
        val toAdd = mutableListOf<Entity>()
        for (effect in e.world.weatherEffects) if (effect is EntityLightningBolt && !BAMethodHandles.getEffectOnly(effect)) {

            if (effect.ticksExisted > 5) continue

            val thunderabsorbers = mutableListOf<BlockPos>()
            for (pos in BlockPos.getAllInBox(effect.position.add(-MAXRANGE, -MAXRANGE, -MAXRANGE), effect.position.add(MAXRANGE, MAXRANGE, MAXRANGE))) {
                val state = e.world.getBlockState(pos)
                if (state.block is IThunderAbsorber)
                    thunderabsorbers.add(pos)
            }
            if (thunderabsorbers.size == 0) continue

            val absorber = thunderabsorbers[e.world.rand.nextInt(thunderabsorbers.size)]
            toAdd.add(EntityLightningBolt(e.world, absorber.x.toDouble(), absorber.y.toDouble(), absorber.z.toDouble(), true))
            toRemove.add(effect)

            for (pos in BlockPos.getAllInBox(effect.position.add(-FIRERANGE, -FIRERANGE, -FIRERANGE), effect.position.add(FIRERANGE, FIRERANGE, FIRERANGE))) {
                if (e.world.getBlockState(pos).block == Blocks.FIRE)
                    e.world.setBlockState(pos, Blocks.AIR.defaultState)
            }
        }

        for (effect in toAdd) {
            e.world.addWeatherEffect(effect)
        }

        for (effect in toRemove) {
            e.world.removeEntityDangerously(effect)
            e.world.weatherEffects.remove(effect)
        }
    }

    @SubscribeEvent
    fun catchPlayerStruck(e: EntityStruckByLightningEvent) {
        for (pos in BlockPos.getAllInBox(e.lightning.position.add(-MAXRANGE, -MAXRANGE, -MAXRANGE), e.lightning.position.add(MAXRANGE, MAXRANGE, MAXRANGE))) {
            val state = e.lightning.worldObj.getBlockState(pos)
            if (state.block is IThunderAbsorber) {
                e.isCanceled = true
                return
            }
        }
    }
}
