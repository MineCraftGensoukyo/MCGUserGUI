package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import org.bukkit.entity.Player

interface PageTools {
    fun giveBackItems(pl: Player, gui: WxScreen)

    fun guiPrepare(gui: WxScreen)
}