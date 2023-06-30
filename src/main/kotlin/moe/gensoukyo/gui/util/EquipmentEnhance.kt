package moe.gensoukyo.gui.util

import moe.gensoukyo.gui.util.LoreInfoTools.getEquipmentInfo
import moe.gensoukyo.gui.util.LoreInfoTools.getStoneInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

object EquipmentEnhance {
    private const val MAX_ENHANCE_LEVEL = 15
    private val ENHANCE_COLOR_LIST = mapOf(
        1..3 to "§f",
        4..6 to "§a",
        7..9 to "§9",
        10..12 to "§5",
        13..15 to "§6"
    )
    private val ENHANCE_PROPERTY_LIST = listOf(
        "力量", "法术", "敏捷"
    )

    private fun getEnhanceColor(level: Int): String {
        ENHANCE_COLOR_LIST.forEach { (range, color) ->
            if (level in range) {
                return color
            }
        }
        return "§f"
    }

    // pair.first: 0:未强化 1:强化失败 2:强化成功
    fun run(pl: Player, equip: ItemStack?, stone: ItemStack?): Pair<Int, ItemStack?> {
        if (equip == null || equip.isAir) {
            pl.sendMessage("§c请放入武器！")
            return Pair(0, null)
        }
        if (stone == null || stone.isAir) {
            pl.sendMessage("§c请放入强化石！")
            return Pair(0, null)
        }
        val equipInfo = getEquipmentInfo(equip)
        if (equipInfo["validity"] != 1) {
            pl.sendMessage("§c请放入合法武器！")
            return Pair(0, null)
        }
        val limitLevel = equipInfo["limitLevel"]!!
        val enhanceLevel = equipInfo["enhanceLevel"]!!
        val quality = equipInfo["quality"]!!
        if (enhanceLevel == MAX_ENHANCE_LEVEL) {
            pl.sendMessage("§c武器已经强化到最高等级！")
            return Pair(0, null)
        }
        val stoneInfo = getStoneInfo(stone)
        if (stoneInfo["validity"] == 0) {
            pl.sendMessage("§c请放入合法强化石！")
            return Pair(0, null)
        }
        val enhanceLevelLow = stoneInfo["enhanceLevelLow"]!!
        val enhanceLevelHigh = stoneInfo["enhanceLevelHigh"]!!
        val limitLevelLow = stoneInfo["limitLevelLow"]!!
        val limitLevelHigh = stoneInfo["limitLevelHigh"]!!
        val successProb = stoneInfo["successProb"]!!
        if (enhanceLevelLow > enhanceLevel || enhanceLevelHigh < enhanceLevel) {
            pl.sendMessage("§c强化石与武器当前强化等级不符！")
            return Pair(0, null)
        }
        if (limitLevelLow > limitLevel || limitLevelHigh < limitLevel) {
            pl.sendMessage("§c强化石与武器等级不符！")
            return Pair(0, null)
        }
        val equipLore = equip.itemMeta?.lore!!
        val newEquip = equip.clone()
        val isSucceed = (1..100).random() <= successProb
        if (!isSucceed) {
            pl.sendMessage("§c强化失败！")
            return Pair(1, null)
        }
        val newEnhanceLevel = enhanceLevel + 1
        val newEquipLore = mutableListOf<String>()
        var tmpFlag = false
        equipLore.forEach { line ->
            if (tmpFlag) {
                newEquipLore.add(
                    "§f §f‖${getEnhanceColor(newEnhanceLevel)}" +
                    "█".repeat(newEnhanceLevel) +
                    "§8${"█".repeat(MAX_ENHANCE_LEVEL - newEnhanceLevel)}§f‖"
                )
                tmpFlag = false
                return@forEach
            }
            if (line.contains("强化等级")) {
                newEquipLore.add("§a§l！ §c强化等级: §f${newEnhanceLevel}")
                return@forEach
            }
            var isEnhanceProperty = false
            ENHANCE_PROPERTY_LIST.forEach {
                if (line.contains(it)) {
                    val spl = line.split("+")
                    val value = spl[1].substring(3).trim().toInt()
                    val newValue = value + quality
                    newEquipLore.add("${spl[0]}+ §f${newValue}")
                    isEnhanceProperty = true
                }
            }
            if (isEnhanceProperty) return@forEach
            if (line.contains("基础属性")) {
                newEquipLore.add(line)
                tmpFlag = true
                return@forEach
            }
            newEquipLore.add(line)
        }
        val newMeta = equip.itemMeta!!.clone()
        newMeta.lore = newEquipLore
        newEquip.itemMeta = newMeta
        pl.sendMessage("§a强化成功！")
        return Pair(2, newEquip)
    }
}
