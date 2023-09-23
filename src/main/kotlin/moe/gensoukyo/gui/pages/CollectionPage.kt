package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent

class CollectionPage : Page {
    private val GUI_BACKGROUND = "http://index.xiaoyv404.com/ftp/Enhance_BG.png"
    private val guiTestPos = Pos(-1, -1, 190, 190, 0, 0)

    private val gui = WInventoryScreen(
        "收集UI",
        GUI_BACKGROUND,
        guiTestPos.dx,
        guiTestPos.dy, guiTestPos.w, guiTestPos.h, 15, guiTestPos.h - 80
    )

    override fun getPage(): WxScreen {
        for (i in 0..10) {
            WSlot(gui.container, "slot$i", ItemStack(Material.AIR), 51 + 20 * i, 41).let {
                it.isCanDrag = true
                gui.container.add(it)
            }
        }
        return gui
    }

    companion object {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun close(e: PlayerCloseScreenEvent) {
            println("${e.player.name}关闭收集图册，保存物品中")
            e.screen.container.componentMap.filter {
                it.key.startsWith("slot")
            }.forEach { k, v ->
                (v as WSlot).itemStack.serialize()
            }
        }
    }
}