package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerOpenScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.pages.EnhancePageTools
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
            val equip =
                (e.screen.container.getComponent("equipment") as WSlot).itemStack
            val stoneIn =
                (e.screen.container.getComponent("stone") as WSlot).itemStack
            if (equip != null) e.player.giveItem(equip)
            if (stoneIn != null) e.player.giveItem(stoneIn)
            equip?.amount = 0
            stoneIn?.amount = 0
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
        info("${e.player.name}点击${e.component.id} - ${e.component}")
        if (e.screen.id == "强化UI") {
            if (e.component.id == "equipment") {
                e.screen.container.getComponent("image_success").w = 0
                e.screen.container.getComponent("image_success").h = 0
                e.screen.container.getComponent("image_fail").w = 0
                e.screen.container.getComponent("image_fail").h = 0
                val equip = (e.component as WSlot).itemStack
                val equipText = e.screen.container.getComponent("enhance_level_text") as WTextList
                EnhancePageTools.refreshEquip(equip, equipText)
                WuxieAPI.updateGui(e.player)
                return
            }
            if (e.component.id == "stone") {
                e.screen.container.getComponent("image_success").w = 0
                e.screen.container.getComponent("image_success").h = 0
                e.screen.container.getComponent("image_fail").w = 0
                e.screen.container.getComponent("image_fail").h = 0
                val stone = (e.component as WSlot).itemStack
                val stoneLevelText = e.screen.container.getComponent("stone_level_text") as WTextList
                val stoneProbText = e.screen.container.getComponent("stone_prob_text") as WTextList
                EnhancePageTools.refreshStone(stone, stoneLevelText, stoneProbText)
                WuxieAPI.updateGui(e.player)
                return
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerOpenScreenEventListener(e: PlayerOpenScreenEvent) {
        if (e.screen.id == "强化UI") {
            e.screen.container.getComponent("image_success").w = 0
            e.screen.container.getComponent("image_success").h = 0
            e.screen.container.getComponent("image_fail").w = 0
            e.screen.container.getComponent("image_fail").h = 0
            (e.screen.container.getComponent("enhance_level_text") as WTextList).scale = 0.0
            (e.screen.container.getComponent("stone_level_text") as WTextList).scale = 0.0
            (e.screen.container.getComponent("stone_prob_text") as WTextList).scale = 0.0
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        ClearCache.run(e.player)
    }
}