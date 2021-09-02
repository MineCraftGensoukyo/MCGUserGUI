package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.type.BukkitPlayer

interface Page {
    fun getPage(): WxScreen
    fun showPage(player: ProxyPlayer){
        player as BukkitPlayer
        WuxieAPI.openGui(player.player,this.getPage())
    }
}