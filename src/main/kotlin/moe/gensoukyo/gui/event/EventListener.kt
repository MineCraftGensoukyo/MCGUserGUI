package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.util.ClearCache
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.giveItem

object EventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerCloseScreenEventListener(e: PlayerCloseScreenEvent) {
        info("${e.player.name}关闭${e.screen.id} - ${e.screen}")
        if (e.screen.id == "强化UI") {
            val equipIn =
                (e.screen.container.getComponent("equipment_input") as WSlot).itemStack
            val stoneIn =
                (e.screen.container.getComponent("stone_input") as WSlot).itemStack
            val equipOut =
                (e.screen.container.getComponent("equipment_output") as WSlot).itemStack
            if (equipIn != null) e.player.giveItem(equipIn)
            if (stoneIn != null) e.player.giveItem(stoneIn)
            if (equipOut != null) e.player.giveItem(equipOut)
            equipIn?.amount = 0
            stoneIn?.amount = 0
            equipOut?.amount = 0
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        ClearCache.run(e.player)
    }
}