package moe.gensoukyo.gui.util

import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

object LoreInfoTools {
    fun getEquipmentInfo(equip: ItemStack): HashMap<String, Int> {
        val equipLore = equip.itemMeta?.lore
        if (equipLore.isNullOrEmpty()) {
            return hashMapOf("validity" to 0)
        }
        var isWeapon = false
        var isArmor = false
        var limitLevel = -1
        var enhanceLevel = -1
        var quality = -1
        var proficiency = -2
        equipLore.forEach {
            val content = ChatColor.stripColor(it)!!
            if (content.contains("强化石")) {
                return hashMapOf("validity" to 0)
            }
            if (content.contains("装备类型")) {
                if (content.contains("武器")) {
                    isWeapon = true
                }
                if (content.contains("防具")) {
                    isArmor = true
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
            if (content.contains("熟练度")) {
                val spl = content.split(" ")
                if (spl.size >= 3) {
                    proficiency = when (spl[2]) {
                        "精炼" -> -1
                        else -> spl[2].toInt()
                    }
                }
            }
        }
        if (isWeapon) {
            if (limitLevel == -1 || enhanceLevel == -1 || quality == -1) {
                return hashMapOf("validity" to 0)
            }
            return hashMapOf(
                "validity" to 1,
                "limitLevel" to limitLevel,
                "enhanceLevel" to enhanceLevel,
                "quality" to quality,
                "proficiency" to proficiency
            )
        }
        if (isArmor) {
            if (limitLevel == -1 || quality == -1) {
                return hashMapOf("validity" to 0)
            }
            return hashMapOf(
                "validity" to 2,
                "limitLevel" to limitLevel,
                "quality" to quality,
                "proficiency" to proficiency
            )
        }
        return hashMapOf("validity" to 0)
    }

    fun getStoneInfo(stone: ItemStack): HashMap<String, Int> {
        val stoneLore = stone.itemMeta?.lore
        if (stoneLore.isNullOrEmpty()) {
            return hashMapOf("validity" to 0)
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
                return hashMapOf("validity" to 0)
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
            return hashMapOf("validity" to 0)
        }
        return hashMapOf(
            "validity" to 1,
            "enhanceLevelLow" to enhanceLevelLow,
            "enhanceLevelHigh" to enhanceLevelHigh,
            "limitLevelLow" to limitLevelLow,
            "limitLevelHigh" to limitLevelHigh,
            "successProb" to successProb
        )
    }

}
