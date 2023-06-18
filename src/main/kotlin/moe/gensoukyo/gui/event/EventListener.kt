package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerOpenScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.pages.EnhancePageTools
import moe.gensoukyo.gui.pages.ProficiencyPageTools
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
        if (e.screen.id == "熟练度UI") {
            val weaponFrom =
                (e.screen.container.getComponent("weapon_from") as WSlot).itemStack
            val weaponTo =
                (e.screen.container.getComponent("weapon_to") as WSlot).itemStack
            val weaponExtract =
                (e.screen.container.getComponent("weapon_extract") as WSlot).itemStack
            val stoneExtract =
                (e.screen.container.getComponent("stone_extract") as WSlot).itemStack
            if (weaponFrom != null) e.player.giveItem(weaponFrom)
            if (weaponTo != null) e.player.giveItem(weaponTo)
            if (weaponExtract != null) e.player.giveItem(weaponExtract)
            if (stoneExtract != null) e.player.giveItem(stoneExtract)
            weaponFrom?.amount = 0
            weaponTo?.amount = 0
            weaponExtract?.amount = 0
            stoneExtract?.amount = 0
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
        if (e.screen.id == "熟练度UI") {
            if (e.component.id == "weapon_from") {
                val from = (e.component as WSlot).itemStack
                val fromText = e.screen.container.getComponent("weapon_from_text") as WTextList
                ProficiencyPageTools.refreshWeaponTransfer(from, fromText)
                WuxieAPI.updateGui(e.player)
                return
            }
            if (e.component.id == "weapon_to") {
                val to = (e.component as WSlot).itemStack
                val toText = e.screen.container.getComponent("weapon_to_text") as WTextList
                ProficiencyPageTools.refreshWeaponTransfer(to, toText)
                WuxieAPI.updateGui(e.player)
                return
            }
            if (e.component.id == "weapon_extract") {
                val extract = (e.component as WSlot).itemStack
                val extractText = e.screen.container.getComponent("weapon_extract_text") as WTextList
                ProficiencyPageTools.refreshWeaponExtract(extract, extractText)
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
        if (e.screen.id == "熟练度UI") {
            (e.screen.container.getComponent("weapon_from_text") as WTextList).scale = 0.0
            (e.screen.container.getComponent("weapon_to_text") as WTextList).scale = 0.0
            (e.screen.container.getComponent("weapon_extract_text") as WTextList).scale = 0.0
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        ClearCache.run(e.player)
    }
}