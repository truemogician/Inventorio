package me.lizardofoz.inventorio.util

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*

data class Point2I(val x: Int, val y: Int)
data class Point2F(val x: Float, val y: Float)
data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

enum class SegmentedHotbar
{
    OFF, ONLY_VISUAL, ON
}

interface ScreenHandlerDuck
{
    var screenHandlerAddon: PlayerScreenHandlerAddon?
}

interface InventoryDuck
{
    val inventorioAddon: PlayerInventoryAddon?
}

val ItemStack.isNotEmpty: Boolean
    get() = !this.isEmpty

val toolBeltSlotFilters = generateToolBeltPredicates()

val usageDisplayToolWhiteList = { it: ItemStack ->
    val itemClass = it.item.javaClass
    itemClass === AxeItem::javaClass || itemClass === ShovelItem::javaClass
}

val usageHotbarBlackList = { it: ItemStack ->
    !InventorioConfig.canThrowUnloyalTrident && (it.item is TridentItem) && EnchantmentHelper.getLoyalty(it) <= 0 && EnchantmentHelper.getRiptide(it) <= 0
}

private fun generateToolBeltPredicates(): List<(ItemStack) -> Boolean>
{
    val toolBeltItems = mutableListOf<(ItemStack) -> Boolean>()
    toolBeltItems.add { it.item is PickaxeItem }
    toolBeltItems.add { it.item is SwordItem || it.item is TridentItem }
    toolBeltItems.add { it.item is AxeItem }
    toolBeltItems.add { it.item is ShovelItem }
    toolBeltItems.add { it.item is HoeItem || it.item is ShearsItem }

    return ImmutableList.copyOf(toolBeltItems)
}