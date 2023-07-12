package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.ProficiencyOperations
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.platform.util.isAir

class ProficiencyPage : Page {
    private val BG_URL =
        "https://img.gensoukyo.moe:843/images/Proficiency_BG.png"
    private val EXT_BTN_URL_1 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Extract_BTN_1.png"
    private val EXT_BTN_URL_2 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Extract_BTN_2.png"
    private val EXT_BTN_URL_3 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Extract_BTN_3.png"
    private val TRS_BTN_URL_1 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Transfer_BTN_1.png"
    private val TRS_BTN_URL_2 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Transfer_BTN_2.png"
    private val TRS_BTN_URL_3 =
        "https://img.gensoukyo.moe:843/images/Proficiency_Transfer_BTN_3.png"

    private val proficiencyGui =
        WInventoryScreen("熟练度UI", BG_URL, -1, -1, 190, 190, 15, 110)
    private val guiContainer = proficiencyGui.getContainer()
    private val transferWeaponFrom =
        WSlot(guiContainer, "weapon_from", ItemStack(Material.AIR), 56, 30)
    private val transferWeaponTo =
        WSlot(guiContainer, "weapon_to", ItemStack(Material.AIR), 119, 30)
    private val extractWeapon =
        WSlot(guiContainer, "weapon_extract", ItemStack(Material.AIR), 56, 54)
    private val extractStone =
        WSlot(guiContainer, "stone_extract", ItemStack(Material.AIR), 119, 54)
    private val transferButton =
        WButton(guiContainer, "transfer_button", "",
            TRS_BTN_URL_1, TRS_BTN_URL_2, TRS_BTN_URL_3, 69, 26)
    private val extractButton =
        WButton(guiContainer, "extract_button", "",
            EXT_BTN_URL_1, EXT_BTN_URL_2, EXT_BTN_URL_3, 75, 50)
    private val titleText =
        WTextList(guiContainer, "title_text", listOf("§1§l精炼锻造"), 75, 4, 40, 20)
    private val weaponFromText =
        WTextList(guiContainer, "weapon_from_text", listOf(""), 20, 34, 60, 0)
    private val weaponToText =
        WTextList(guiContainer, "weapon_to_text", listOf(""), 142, 34, 60, 0)
    private val weaponExtractText =
        WTextList(guiContainer, "weapon_extract_text", listOf(""), 20, 58, 60, 0)

    override fun getPage(): WxScreen {
        transferWeaponFrom.isCanDrag = true
        transferWeaponFrom.emptyTooltips = listOf("§f请放入需要转出熟练度的武器")
        guiContainer.add(transferWeaponFrom)
        transferWeaponTo.isCanDrag = true
        transferWeaponTo.emptyTooltips = listOf("§f请放入需要转入熟练度的武器")
        guiContainer.add(transferWeaponTo)
        extractWeapon.isCanDrag = true
        extractWeapon.emptyTooltips = listOf("§f请放入需要提取精炼原石的武器")
        guiContainer.add(extractWeapon)
        extractStone.isCanDrag = true
        guiContainer.add(extractStone)
        transferButton.w = 50
        transferButton.h = 25
        transferButton.tooltips = listOf("§f转移")
        transferButton.setFunction { _, pl ->
            try {
                val (newWeaponFrom, newWeaponTo) =
                    ProficiencyOperations.transfer(pl, transferWeaponFrom.itemStack, transferWeaponTo.itemStack)
                if (newWeaponFrom != null && newWeaponTo != null) {
                    transferWeaponFrom.itemStack = newWeaponFrom
                    transferWeaponTo.itemStack = newWeaponTo
                    ProficiencyPageTools.refreshWeaponTransfer(transferWeaponFrom.itemStack, weaponFromText)
                    ProficiencyPageTools.refreshWeaponTransfer(transferWeaponTo.itemStack, weaponToText)
                    WuxieAPI.updateGui(pl)
                }
            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        guiContainer.add(transferButton)
        extractButton.w = 24
        extractButton.h = 24
        extractButton.tooltips = listOf("§f提取")
        extractButton.setFunction { _, pl ->
            try {
                if (extractStone.itemStack != null && !extractStone.itemStack.isAir) {
                    pl.sendMessage("§c请先取出精炼原石")
                } else {
                    val (newWeapon, stone) =
                        ProficiencyOperations.extract(pl, extractWeapon.itemStack)
                    if (stone != null) {
                        extractStone.itemStack = stone
                        extractWeapon.itemStack = newWeapon
                        ProficiencyPageTools.refreshWeaponExtract(newWeapon, weaponExtractText)
                        WuxieAPI.updateGui(pl)
                    }
                }
            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        guiContainer.add(extractButton)
        titleText.scale = 1.2
        guiContainer.add(titleText)
        weaponFromText.scale = 0.0
        guiContainer.add(weaponFromText)
        weaponToText.scale = 0.0
        guiContainer.add(weaponToText)
        weaponExtractText.scale = 0.0
        guiContainer.add(weaponExtractText)
        return proficiencyGui
    }

}