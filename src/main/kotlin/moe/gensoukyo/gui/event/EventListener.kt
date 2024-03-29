package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerOpenScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.pages.DecomposePageTools
import moe.gensoukyo.gui.pages.EnhancePageTools
import moe.gensoukyo.gui.pages.Pages.pages
import moe.gensoukyo.gui.pages.ProficiencyPageTools
import moe.gensoukyo.gui.util.ClearCache
import moe.gensoukyo.gui.util.EmbeddingTools
import moe.gensoukyo.lib.maps.DataToken
import moe.gensoukyo.lib.server.npcApi
import moe.gensoukyo.gui.config.MainConfig.printDebugInfo
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer

object EventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerOpenScreenEventListener(e: PlayerOpenScreenEvent) {
        if (printDebugInfo) {
            info("${e.player.name}打开${e.screen.id} - ${e.screen}")
        }
        pages.forEach {
            if (e.screen.id == it.key && it.value != null)
                it.value!!.guiPrepare(e.player, e.screen)
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerCloseScreenEventListener(e: PlayerCloseScreenEvent) {
        if (printDebugInfo) {
            info("${e.player.name}关闭${e.screen.id} - ${e.screen}")
        }
        pages.forEach {
            if (e.screen.id == it.key && it.value != null)
                it.value!!.giveBackItems(e.player, e.screen)
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
        if (printDebugInfo) {
            info("${e.player.name}点击${e.component.id} - ${e.component}")
        }
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
        if (e.screen.id == "分解UI" && e.component.id == "equipment" || e.component.id == "output") {
            val equip = (e.screen.container.getComponent("equipment") as WSlot).itemStack
            val output = (e.screen.container.getComponent("output") as WSlot).itemStack
            val equipText = e.screen.container.getComponent("equipment_text") as WTextList
            val outputText = e.screen.container.getComponent("output_text") as WTextList
            DecomposePageTools.refresh(equip, output, equipText, outputText)
            WuxieAPI.updateGui(e.player)
            return
        }

        if (e.screen.id == "镶嵌UI") {
            if (e.component.id == "equipment_slot") {
                e.screen.container.getComponent("image_success").w = 0
                e.screen.container.getComponent("image_success").h = 0
                e.screen.container.getComponent("image_fail").w = 0
                e.screen.container.getComponent("image_fail").h = 0

                val equip = (e.component as WSlot).itemStack
                val button = e.screen.container.getComponent("embedding_button") as WButton
                val equipmentTipsText = e.screen.container.getComponent("equipment_tips") as WTextList
                val stoneTipsText = e.screen.container.getComponent("stone_tips") as WTextList
                EmbeddingTools.equipmentSlotCheck(e.player, equip, button, equipmentTipsText, stoneTipsText)
                return
            }
            if (e.component.id == "stone_slot") {
                e.screen.container.getComponent("image_success").w = 0
                e.screen.container.getComponent("image_success").h = 0
                e.screen.container.getComponent("image_fail").w = 0
                e.screen.container.getComponent("image_fail").h = 0

                val stone = (e.component as WSlot).itemStack
                val button = e.screen.container.getComponent("embedding_button") as WButton
                val equipmentTipsText = e.screen.container.getComponent("equipment_tips") as WTextList
                val stoneTipsText = e.screen.container.getComponent("stone_tips") as WTextList
                val stoneValueText = e.screen.container.getComponent("stone_value") as WTextList
                EmbeddingTools.stoneSlotCheck(
                    e.player, stone, button, equipmentTipsText,
                    stoneTipsText, stoneValueText
                )
                return
            }
        }
        if (e.screen.id == "摘除镶嵌UI" && e.component.id == "equipment_slot") {
            val equip = (e.component as WSlot).itemStack
            EmbeddingTools.unEmbeddingCheck(equip, e.screen.container, e.player)
            return
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(e: PlayerLoginEvent) {
        ClearCache.run(e.player)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        e.player.setupDataContainer()
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val iPlayer = e.player.npcApi
        pages.forEach {
            if (it.value != null) {
                val guiData =
                    DataToken("${iPlayer.name}_${it.key}_Gui", WxScreen::class.java) { null }
                if (guiData[iPlayer.tempdata] != null) {
                    val gui = guiData[iPlayer.tempdata] as WxScreen
                    it.value!!.giveBackItems(e.player, gui)
                }
            }
        }
        e.player.releaseDataContainer()
    }
}