package moe.gensoukyo.gui.util

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

class EquipmentEnhance(private val pl: Player,
                       private val equip: ItemStack?,
                       private val stone: ItemStack?) {
    companion object {
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
    }

    private fun getEnhanceColor(level: Int): String {
        ENHANCE_COLOR_LIST.forEach { (range, color) ->
            if (level in range) {
                return color
            }
        }
        return "§f"
    }

    fun run(): ItemStack? {
        if (equip == null || equip.isAir) {
            pl.sendMessage("§c请放入武器！")
            return null
        }
        if (stone == null || stone.isAir) {
            pl.sendMessage("§c请放入强化石！")
            return null
        }
        val equipLore = equip.itemMeta?.lore
        if (equipLore.isNullOrEmpty()) {
            pl.sendMessage("§c请放入合法武器！")
            return null
        }
        val stoneLore = stone.itemMeta?.lore
        if (stoneLore.isNullOrEmpty()) {
            pl.sendMessage("§c请放入合法强化石！")
            return null
        }
        var isWeapon = false
        var limitLevel = -1
        var enhanceLevel = -1
        var quality = -1
        equipLore.forEach {
            val content = ChatColor.stripColor(it)!!
            if (content.contains("强化石")) {
                pl.sendMessage("§c请放入合法武器！")
                return null
            }
            if (content.contains("装备类型")) {
                if (content.contains("武器")) {
                    isWeapon = true
                }
            }
            if (content.contains("需要等级")) {
                val spl = content.split(" ")
                if (spl.size >= 3) {
                    limitLevel = spl[2].toInt()
                }
            }
            if (content.contains("强化等级")) {
                val spl = content.split(" ")
                if (spl.size >= 3) {
                    enhanceLevel = spl[2].toInt()
                }
            }
            if (content.contains("品阶")) {
                val spl = content.split(" ")
                if (spl.size >= 5) {
                    quality = spl[4].toInt()
                }
            }
        }
        if (!isWeapon || limitLevel == -1 || enhanceLevel == -1 || quality == -1) {
            pl.sendMessage("§c请放入合法武器！")
            return null
        }
        if (enhanceLevel == MAX_ENHANCE_LEVEL) {
            pl.sendMessage("§c武器已经强化到最高等级！")
            return null
        }
        var isStone = false
        var enhanceLevelLow = -1
        var enhanceLevelHigh = -1
        var limitLevelLow = -1
        var limitLevelHigh = -1
        var successProb = -1
        stoneLore.forEach {
            val content = ChatColor.stripColor(it)!!
            if (content.contains("装备类型")) {
                pl.sendMessage("§c请放入合法强化石！")
                return null
            }
            if (content.contains("强化石")) {
                isStone = true
            }
            if (content.contains("强化等级")) {
                val spl = content.split(" ")
                if (spl.size >= 2) {
                    val tmp = spl[1].split("-")
                    if (tmp.size >= 2) {
                        enhanceLevelLow = tmp[0].toInt()
                        enhanceLevelHigh = tmp[1].toInt()
                    }
                }
            }
            if (content.contains("适用等级")) {
                val spl = content.split(" ")
                if (spl.size >= 2) {
                    val tmp = spl[1].split("-")
                    if (tmp.size >= 2) {
                        limitLevelLow = tmp[0].toInt()
                        limitLevelHigh = tmp[1].toInt()
                    }
                }
            }
            if (content.contains("成功概率")) {
                val spl = content.split(" ")
                if (spl.size >= 2) {
                    successProb = spl[1].substring(0, spl[1].length - 1).toInt()
                }
            }
        }
        if (!isStone || enhanceLevelLow == -1 || limitLevelLow == -1 || successProb == -1) {
            pl.sendMessage("§c请放入合法强化石！")
            return null
        }
        if (enhanceLevelLow > enhanceLevel || enhanceLevelHigh < enhanceLevel) {
            pl.sendMessage("§c强化石与武器当前强化等级不符！")
            return null
        }
        if (limitLevelLow > limitLevel || limitLevelHigh < limitLevel) {
            pl.sendMessage("§c强化石与武器等级不符！")
            return null
        }
        val newEquip = equip.clone()
        val isSucceed = (1..100).random() <= successProb
        if (!isSucceed) {
            pl.sendMessage("§c强化失败！")
            return newEquip
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
                    val value = spl[1].substring(3, spl[1].length - 1).toDouble()
                    val newValue = value + quality
                    newEquipLore.add("${spl[0]}+ §f${"%.2f".format(newValue)}")
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
        return newEquip
    }
}
