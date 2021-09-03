package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import moe.gensoukyo.lib.maps.DataToken
import moe.gensoukyo.lib.server.npcApi
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning

interface Page {
    fun getPage(): WxScreen
    fun showPage(player: Player) {
        val iPlayer = player.npcApi
        try {
            val guiData =
                DataToken("${iPlayer.name}_${getPage().id}_Gui", WxScreen::class.java) { null }
            val thisGui: WxScreen =
                if (guiData[iPlayer.tempdata] == null) {
                    guiData.put(iPlayer.tempdata, this.getPage())
                    this.getPage()
                } else {
                    guiData[iPlayer.tempdata]
                }
            WuxieAPI.openGui(player.player, thisGui)
        } catch (e: Exception) {
            warning(e)
        }
    }
}