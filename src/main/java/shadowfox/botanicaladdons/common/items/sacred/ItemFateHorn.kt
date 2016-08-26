package shadowfox.botanicaladdons.common.items.sacred

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.IInventory
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.stats.Achievement
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.world.World
import shadowfox.botanicaladdons.common.BotanicalAddons
import shadowfox.botanicaladdons.common.achievements.ModAchievements
import shadowfox.botanicaladdons.common.items.base.ItemMod
import shadowfox.botanicaladdons.common.potions.ModPotions
import shadowfox.botanicaladdons.common.potions.base.ModPotionEffect
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.mana.IManaUsingItem
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.achievement.ICraftAchievement

/**
 * @author WireSegal
 * Created at 5:26 PM on 4/27/16.
 */
class ItemFateHorn(name: String) : ItemMod(name), IManaUsingItem, ICraftAchievement {

    init {
        setMaxStackSize(1)
    }

    val RANGE = 7.0

    override fun usesMana(p0: ItemStack?) = true

    override fun getItemUseAction(stack: ItemStack) = EnumAction.BOW
    override fun getMaxItemUseDuration(stack: ItemStack?) = 72000

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World?, playerIn: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack>? {
        playerIn.activeHand = hand
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand)
    }

    override fun getRarity(stack: ItemStack?) = BotaniaAPI.rarityRelic

    override fun onUsingTick(stack: ItemStack, player: EntityLivingBase, count: Int) {
        val entities = player.worldObj.getEntitiesWithinAABB(EntityLiving::class.java, player.entityBoundingBox.expandXyz(RANGE), { it?.isNonBoss ?: false })
        var doit = true
        if (entities.size > 0 && player is EntityPlayer && !player.worldObj.isRemote)
            doit = ManaItemHandler.requestManaExact(stack, player, 2, true)
        if (doit)
            for (entity in entities)
                entity.addPotionEffect(ModPotionEffect(ModPotions.rooted, 200, 0, true, false))

        // Gold color is 0xdfb412
        val r = 0xDF / 255f
        val g = 0xB4 / 255f
        val b = 0x12 / 255f

        BotanicalAddons.PROXY.particleRing(player.posX, player.posY, player.posZ, RANGE, r, g, b)

        if (!player.worldObj.isRemote)
            player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_BASS, SoundCategory.BLOCKS, 1f, 0.001f)

    }

    override fun getAchievementOnCraft(p0: ItemStack?, p1: EntityPlayer?, p2: IInventory?): Achievement? {
        return ModAchievements.sacredHorn
    }
}
