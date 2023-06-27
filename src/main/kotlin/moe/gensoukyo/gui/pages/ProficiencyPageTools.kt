package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.util.LoreInfoTools
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

object ProficiencyPageTools : PageTools{
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

    override fun giveBackItems(pl: Player, gui: WxScreen) {
        val weaponFrom =
            (gui.container.getComponent("weapon_from") as WSlot).itemStack
        val weaponTo =
            (gui.container.getComponent("weapon_to") as WSlot).itemStack
        val weaponExtract =
            (gui.container.getComponent("weapon_extract") as WSlot).itemStack
        val stoneExtract =
            (gui.container.getComponent("stone_extract") as WSlot).itemStack
        if (weaponFrom != null) pl.giveItem(weaponFrom)
        if (weaponTo != null) pl.giveItem(weaponTo)
        if (weaponExtract != null) pl.giveItem(weaponExtract)
        if (stoneExtract != null) pl.giveItem(stoneExtract)
        weaponFrom?.amount = 0
        weaponTo?.amount = 0
        weaponExtract?.amount = 0
        stoneExtract?.amount = 0
    }

}
