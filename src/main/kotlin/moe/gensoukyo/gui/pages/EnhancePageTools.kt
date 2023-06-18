package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.util.LoreInfoTools.getEquipmentInfo
import moe.gensoukyo.gui.util.LoreInfoTools.getStoneInfo
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

object EnhancePageTools {
    fun refreshEquip(equip: ItemStack?, equipText: WTextList) {
        if (equip == null || equip.isAir) {
            equipText.scale = 0.0
            return
        }
        val equipInfo = getEquipmentInfo(equip)
        equipText.scale = 0.7
        if (equipInfo["validity"] == 0) {
            equipText.content = listOf("§4§l无法强化")
            return
        }
        val enhanceLevel = equipInfo["enhanceLevel"]!!
        if (enhanceLevel == 15) {
            equipText.content = listOf("§4§l强化已满")
            return
        }
        equipText.content = listOf("§1§l等级: ${enhanceLevel}")
        return
    }

    fun refreshStone(stone: ItemStack?, stoneLevelText: WTextList, stoneProbText: WTextList) {
        if (stone == null || stone.isAir) {
            stoneLevelText.scale = 0.0
            stoneProbText.scale = 0.0
            return
        }
        val stoneInfo = getStoneInfo(stone)
        stoneLevelText.scale = 0.7
        stoneProbText.scale = 1.0
        if (stoneInfo["validity"] == 0) {
            stoneLevelText.content = listOf("§4§l无法强化")
            stoneProbText.content = listOf("")
            return
        }
        stoneLevelText.content =
            listOf("§1§l等级: ${stoneInfo["limitLevelLow"]!!}-${stoneInfo["limitLevelHigh"]!!}")
        stoneProbText.content = listOf("§b§l${stoneInfo["successProb"]!!}%")
    }

}
