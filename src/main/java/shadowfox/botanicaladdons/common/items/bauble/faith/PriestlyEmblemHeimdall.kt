package shadowfox.botanicaladdons.common.items.bauble.faith

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.math.BlockPos
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shadowfox.botanicaladdons.api.IFaithVariant
import shadowfox.botanicaladdons.api.SpellRegistry
import shadowfox.botanicaladdons.common.lib.LibMisc
import shadowfox.botanicaladdons.common.potions.ModPotions
import shadowfox.botanicaladdons.common.potions.base.ModPotionEffect
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.block.ModBlocks
import vazkii.botania.common.block.tile.TileBifrost
import vazkii.botania.common.item.ModItems
import java.awt.Color

/**
 * @author WireSegal
 * Created at 9:49 AM on 4/18/16.
 */
class PriestlyEmblemHeimdall : IFaithVariant {

    init {
        SpellRegistry.registerSpell("iridescence", Spells.Heimdall.Iridescence())
        SpellRegistry.registerSpell("bifrostWave", Spells.Heimdall.BifrostWave())
    }

    override fun getName(): String = "heimdall"

    override fun hasSubscriptions(): Boolean = true

    override fun getSpells(stack: ItemStack, player: EntityPlayer): MutableList<String> {
        return mutableListOf("${LibMisc.MOD_ID}:iridescence", "${LibMisc.MOD_ID}:bifrostWave")
    }

    @SideOnly(Side.CLIENT)
    override fun getColor(): IItemColor? =
        IItemColor { itemStack, i ->
            if (i == 1)
                Color.HSBtoRGB((Botania.proxy.worldElapsedTicks * 2L % 360L).toFloat() / 360.0f, 1.0f, 1.0f)
            else
                0xFFFFFF
        }

    override fun onUpdate(stack: ItemStack, player: EntityPlayer) {
        if (ManaItemHandler.requestManaExact(stack, player, 1, true))
            player.addPotionEffect(PotionEffect(MobEffects.nightVision, 610, 0, true, false))

    }

    override fun punishTheFaithless(stack: ItemStack, player: EntityPlayer) {
        player.addPotionEffect(ModPotionEffect(ModPotions.drab, 600))
        player.removeActivePotionEffect(MobEffects.nightVision)
    }

    @SubscribeEvent
    fun bifrostPlatform(e: LivingEvent.LivingUpdateEvent) {
        val player = e.entityLiving
        val world = player.worldObj
        if (world.isRemote) return
        if (player is EntityPlayer) {
            if ((player.heldItemMainhand?.item == ModItems.rainbowRod ?: false) || (player.heldItemOffhand?.item == ModItems.rainbowRod ?: false)) {
                val emblem = ItemFaithBauble.getEmblem(player, PriestlyEmblemHeimdall::class.java) ?: return
                if (ManaItemHandler.requestManaExact(emblem, player, 10, false)) {
                    val pos = BlockPos(player.posX + player.motionX, Math.ceil(player.posY + if (player.isSneaking) -3.25 else -1.25), player.posZ + player.motionZ)
                    val state = world.getBlockState(pos)
                    val block = state.block
                    if (block.isAir(state, world, pos) || block.isReplaceable(world, pos) || block.getMaterial(state).isLiquid) {
                        world.setBlockState(pos, ModBlocks.bifrost.defaultState)
                        val tileBifrost = world.getTileEntity(pos) as TileBifrost
                        tileBifrost.ticks = 5
                        player.fallDistance = 0f
                        ManaItemHandler.requestManaExact(emblem, player, 10, true)
                    } else if (block == ModBlocks.bifrost) {
                        val tileBifrost = world.getTileEntity(pos) as TileBifrost
                        if (tileBifrost.ticks < 2) {
                            tileBifrost.ticks = 5
                            ManaItemHandler.requestManaExact(emblem, player, 10, true)
                        }
                    }
                }
            }
        }
    }
}
