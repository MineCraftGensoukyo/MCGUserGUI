package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EquipmentEnhance
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

object EnhancePageTools {
    fun refreshEquip(equip: ItemStack?, equipText: WTextList) {
        if (equip == null || equip.isAir) {
            equipText.scale = 0.0
            return
        }
        val equipInfo = EquipmentEnhance.getEquipmentInfo(equip)
        equipText.scale = conf["enhance.enhanceLevel.s"] as Double
        if (equipInfo["validity"] == 0) {
            val color = conf["enhance.enhanceLevel.fc"] as String
            equipText.content = listOf("${color}§l无法强化")
            return
        }
        val enhanceLevel = equipInfo["enhanceLevel"]!!
        if (enhanceLevel == 15) {
            val color = conf["enhance.enhanceLevel.fc"] as String
            equipText.content = listOf("${color}§l强化已满")
            return
        }
        val color = conf["enhance.enhanceLevel.pc"] as String
        equipText.content = listOf("${color}§l等级: ${enhanceLevel}")
        return
    }

    fun refreshStone(stone: ItemStack?, stoneLevelText: WTextList, stoneProbText: WTextList) {
        if (stone == null || stone.isAir) {
            stoneLevelText.scale = 0.0
            stoneProbText.scale = 0.0
            return
        }
        val stoneInfo = EquipmentEnhance.getStoneInfo(stone)
        stoneLevelText.scale = conf["enhance.stoneLevel.s"] as Double
        stoneProbText.scale = conf["enhance.stoneProb.s"] as Double
        if (stoneInfo["validity"] == 0) {
            val color = conf["enhance.stoneLevel.fc"] as String
            stoneLevelText.content = listOf("${color}§l无法强化")
            stoneProbText.content = listOf("")
            return
        }
        val color = conf["enhance.stoneLevel.pc"] as String
        stoneLevelText.content =
            listOf("${color}§l等级: ${stoneInfo["limitLevelLow"]!!}-${stoneInfo["limitLevelHigh"]!!}")
        stoneProbText.content = listOf("§b§l${stoneInfo["successProb"]!!}%")
    }

}
