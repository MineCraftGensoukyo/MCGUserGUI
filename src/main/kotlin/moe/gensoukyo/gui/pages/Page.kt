package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import moe.gensoukyo.gui.util.CheckModExistence
import moe.gensoukyo.lib.maps.DataToken
import moe.gensoukyo.lib.server.npcApi
import org.bukkit.entity.Player
import taboolib.common.platform.function.warning

interface Page {
    fun getPage(): WxScreen
    fun showPage(player: Player) {
        if (!CheckModExistence.run(player)) {
            player.sendTitle(
                "§c未安装WakeShow模组",
                "§f请使用启动游戏.exe更新", 10, 70, 20
            )
            return
        }
        WuxieAPI.openGui(player.player, this.getPage())
    }

    fun showCachePage(player: Player): WxScreen? {
        if (!CheckModExistence.run(player)) {
            player.sendTitle(
                "§c未安装WakeShow模组",
                "§f请使用启动游戏.exe更新", 10, 70, 20
            )
            return null
        }
        val iPlayer = player.npcApi
        try {
            val guiData =
                DataToken("${iPlayer.name}_${getPage().id}_Gui", WxScreen::class.java) { null }
            val thisGui: WxScreen

            if (guiData[iPlayer.tempdata] == null) {
                thisGui = this.getPage()
                guiData.put(iPlayer.tempdata, thisGui)
            } else {
                thisGui = guiData[iPlayer.tempdata]
            }
            WuxieAPI.openGui(player.player, thisGui)
            return thisGui
        } catch (e: Exception) {
            warning(e)
        }
        return null
    }

}