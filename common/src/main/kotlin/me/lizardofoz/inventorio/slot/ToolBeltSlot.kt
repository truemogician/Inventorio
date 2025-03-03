package me.lizardofoz.inventorio.slot

import me.lizardofoz.inventorio.api.ToolBeltSlotTemplate
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.util.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

open class ToolBeltSlot(private val template: ToolBeltSlotTemplate, private val inventoryAddon: PlayerInventoryAddon, index: Int, x: Int, y: Int) : Slot(inventoryAddon, index, x, y)
{
    override fun canInsert(stack: ItemStack): Boolean
    {
        return template.test(stack, inventoryAddon)
    }

    companion object
    {
        fun getGuiPosition(deepPocketsRows: Int, index: Int, totalCount: Int): Rectangle
        {
            val columnCapacity = getColumnCapacity(deepPocketsRows)
            val heightOffset = 177 - Math.min(totalCount, columnCapacity) * SLOT_UI_SIZE + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows)
            val relativeIndex = index % columnCapacity
            return Rectangle(
                173 + (index / columnCapacity) * (SLOT_UI_SIZE + 2),
                heightOffset + (relativeIndex - 1) * SLOT_UI_SIZE,
                18,
                90)
        }

        fun getSlotPosition(deepPocketsRows: Int, index: Int, totalCount: Int): Rectangle
        {
            val guiPos = getGuiPosition(deepPocketsRows, index, totalCount)
            return Rectangle(guiPos.x + 1, guiPos.y + 1, guiPos.width, guiPos.height)
        }

        fun getColumnCapacity(deepPocketsRows: Int): Int
        {
            return 6 + deepPocketsRows
        }
    }
}