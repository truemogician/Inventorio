package me.lizardofoz.inventorio.player.inventory

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.extra.InventorioSharedConfig
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.util.isNotEmpty
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.Util

abstract class PlayerInventoryHandFeatures protected constructor(player: PlayerEntity) : PlayerInventoryInjects(player)
{
    var displayToolTimeStamp = 0L
        protected set

    var displayTool = ItemStack.EMPTY!!
        set(value) {
            field = value
            displayToolTimeStamp = Util.getMeasuringTimeMs() + 1000
        }

    var swappedHands = false
        set(value)
        {
            field = InventorioSharedConfig.allowSwappedHands && value
            if (player.world.isClient)
                InventorioNetworking.INSTANCE.c2sSetSwappedHands(value)
        }

    init
    {
        if (player.world.isClient)
            swappedHands = InventorioConfig.swappedHands
    }

    /**
     * Returns an item that will be ultimately displayed in the player's main hand.
     *
     * Affected by [swappedHands]
     *
     * Unlike [getActualMainHandItem], will return [displayTool] if present
     */
    fun getDisplayedMainHandStack(): ItemStack?
    {
        return when
        {
            displayTool.isNotEmpty -> displayTool
            swappedHands -> getSelectedUtilityStack()
            else -> null
        }
    }

    /**
     * Returns an item that will be ultimately displayed in the player's offhand.
     *
     * Affected by [swappedHands]
     */
    fun getDisplayedOffHandStack(): ItemStack
    {
        if (!swappedHands)
            return getSelectedUtilityStack()
        return getSelectedHotbarStack()
    }

    /**
     * Returns the item selected to be placed in the main hand.
     *
     * Affected by [swappedHands]
     *
     * Unlike [getDisplayedMainHandStack], it's not affected by [displayTool]
     */
    fun getActualMainHandItem(): ItemStack
    {
        return if (swappedHands)
            getSelectedUtilityStack()
        else
            getSelectedHotbarStack()
    }

    /**
     * Returns the item selected on the hotbar, regardless of [swappedHands] or [displayTool]
     */
    fun getSelectedHotbarStack(): ItemStack
    {
        if (PlayerInventory.isValidHotbarIndex(player.inventory.selectedSlot))
            return player.inventory.getStack(player.inventory.selectedSlot)
        return ItemStack.EMPTY
    }

    /**
     * Returns the item selected on the utility belt, regardless of [swappedHands] or [displayTool]
     */
    fun getSelectedUtilityStack(): ItemStack
    {
        return utilityBelt[selectedUtility]
    }

    /**
     * Replaces the currently selected item on the hotbar, regardless of [swappedHands] or [displayTool]
     */
    fun setSelectedHotbarStack(itemStack: ItemStack)
    {
        if (PlayerInventory.isValidHotbarIndex(player.inventory.selectedSlot))
            player.inventory.setStack(player.inventory.selectedSlot, itemStack)
    }

    /**
     * Replaces the currently selected item on the utility belt, regardless of [swappedHands] or [displayTool]
     */
    fun setSelectedUtilityStack(itemStack: ItemStack)
    {
        utilityBelt[selectedUtility] = itemStack
    }

    /**
     * Returns 3 utility belt items to display on the HUD
     */
    fun getDisplayedUtilities(): Array<ItemStack>
    {
        return arrayOf(findNextUtility(-1).first, getSelectedUtilityStack(), findNextUtility(1).first)
    }
}