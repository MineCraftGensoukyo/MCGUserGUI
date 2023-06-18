package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.util.LoreInfoTools
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

object ProficiencyPageTools {
    fun refreshWeaponTransfer(weapon: ItemStack?, weaponText: WTextList) {
        if (weapon == null || weapon.isAir) {
            weaponText.scale = 0.0
            return
        }
        val equipInfo = LoreInfoTools.getEquipmentInfo(weapon)
        weaponText.scale = 0.7
        if (equipInfo["validity"] == 0) {
            weaponText.content = listOf("§4§l无法转移")
            return
        }
        val proficiency = equipInfo["proficiency"]!!
        if (proficiency == -1) {
            weaponText.content = listOf("§4§l无法转移")
            return
        }
        weaponText.content = listOf("§1§l熟练: ${proficiency}")
        return
    }

    fun refreshWeaponExtract(extract: ItemStack?, extractText: WTextList) {
        if (extract == null || extract.isAir) {
            extractText.scale = 0.0
            return
        }
        val equipInfo = LoreInfoTools.getEquipmentInfo(extract)
        extractText.scale = 0.7
        if (equipInfo["validity"] == 0) {
            extractText.content = listOf("§4§l无法提取")
            return
        }
        if (equipInfo["proficiency"] != -1) {
            extractText.content = listOf("§4§l无法提取")
            return
        }
        val quality = equipInfo["quality"]!!
        extractText.content = listOf("§1§l品阶: ${quality}")
        return
    }

}
