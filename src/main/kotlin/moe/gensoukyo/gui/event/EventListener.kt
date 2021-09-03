package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

object EventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerCloseScreenEventListener(e:PlayerCloseScreenEvent){
        info("${e.player.name}关闭${e.screen.id}")
        e.player.sendMessage("${e.player.name}关闭${e.screen.id}")
    }
}