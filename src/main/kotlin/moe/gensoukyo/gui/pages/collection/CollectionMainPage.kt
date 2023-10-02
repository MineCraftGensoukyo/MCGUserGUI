package moe.gensoukyo.gui.pages.collection

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.Container
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir


class CollectionMainPage : CollectionPage {
    private val guiTestPos = Pos(-1, -91, 512, 512, 0, 0)
    private val x0 = 175
    private val y0 = 246
    private val imageRoot = "location:mcgproject:textures/gui"
    private val backgroundImage = "$imageRoot/collection_mobs.png"

    private val gui = WInventoryScreen(
        getPageID(),
        backgroundImage,
        guiTestPos.dx,
        guiTestPos.dy,
        guiTestPos.w,
        guiTestPos.h,
        x0,
        y0 + 18 * 6 + 12
    )

    override fun getPageID() = "collection_mobs"
    override fun getNextPage() = "collection_mooncake"
    override fun getLastPage() = "collection_akyuu"

    override fun getPage(): WxScreen {
        for (l in 0..5)
            for (i in 0..8) {
                val slotName = "slot$l-$i"
                val x = x0 + 18 * i
                val y = y0 - 6 + 18 * l
                createEmptySlot(gui.container, slotName, x, y)
            }

        return gui
    }

    private fun createEmptySlot(container: Container, name: String, x: Int, y: Int) {
        WSlot(
            container, name, ItemStack(Material.AIR), x, y
        ).apply {
            isCanDrag = true
            container.add(this)
        }
    }


    companion object {
        @SubscribeEvent(EventPriority.HIGHEST)
        fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
            if (e.screen.id != "collection_mobs") return
            if (!e.component.id.startsWith("slot")) return

            val slot = e.component as WSlot
            if (slot.itemStack.isAir()) return

            if (slot.itemStack.type.name != "ARMOURERS_WORKSHOP_ITEMSKIN")
                return giveBackItem(slot, e.player)
        }

        private fun giveBackItem(slot: WSlot, player: Player) {
            player.sendMessage("这不是时装！")
            player.giveItem(slot.itemStack)
            slot.itemStack = ItemStack(Material.AIR)
            WuxieAPI.updateGui(player)
        }
    }
}