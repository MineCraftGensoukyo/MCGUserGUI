package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WImage
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EquipmentEnhance
import taboolib.common.platform.function.warning

class EnhancePage : Page{
    private val VERSION = conf["imageVersion"] as String
    private val BG_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BG.png"
    private val BTN_URL_1 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_1.png"
    private val BTN_URL_2 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_2.png"
    private val BTN_URL_3 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_3.png"
    private val SUCCESS_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_Success.png"
    private val FAIL_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_Fail.png"

    private val enhanceGui =
        WInventoryScreen("强化UI", BG_URL, -1, -1, 190, 190, 15, 110)
    private val guiContainer = enhanceGui.container
    private val stoneSlot =
        WSlot(guiContainer, "stone", ItemStack(Material.AIR), 50, 41)
    private val equipmentSlot =
        WSlot(guiContainer, "equipment", ItemStack(Material.AIR), 101, 41)
    private val buttonOutput =
        WButton(guiContainer, "button_output", "",
            BTN_URL_1, BTN_URL_2, BTN_URL_3, 117, 37)
    private val imageSuccess =
        WImage(guiContainer, "image_success",
            SUCCESS_URL, 152, 38, 0, 0)
    private val imageFail =
        WImage(guiContainer, "image_fail",
            FAIL_URL, 152, 38, 0, 0)
    private val enhanceLevelText =
        WTextList(guiContainer, "enhance_level_text", listOf(), 150, 30, 60, 0)
    private val stoneLevelText =
        WTextList(guiContainer, "stone_level_text", listOf(), 9, 20, 60, 0)
    private val stoneProbText =
        WTextList(guiContainer, "stone_prob_text", listOf(), 25, 30, 20, 0)
    private val titleText =
        WTextList(guiContainer, "title_text", listOf("§1§l强  化"), 80, 4, 40, 20)

    override fun getPage(): WxScreen {
        equipmentSlot.isCanDrag = true
        equipmentSlot.emptyTooltips = listOf("§f请放入武器")
        guiContainer.add(equipmentSlot)
        stoneSlot.isCanDrag = true
        stoneSlot.emptyTooltips = listOf("§f请放入强化石")
        guiContainer.add(stoneSlot)
        buttonOutput.setFunction { _, pl ->
            try {
                imageSuccess.w = 0
                imageSuccess.h = 0
                imageFail.w = 0
                imageFail.h = 0
                val (code, ret) = EquipmentEnhance.run(pl, equipmentSlot.itemStack, stoneSlot.itemStack)
                if (ret != null) {
                    equipmentSlot.itemStack = ret
                    val newStoneStack = stoneSlot.itemStack.clone()
                    newStoneStack.amount -= 1
                    stoneSlot.itemStack = newStoneStack
                    imageSuccess.w = 24
                    imageSuccess.h = 24
                    EnhancePageTools.refreshEquip(equipmentSlot.itemStack, enhanceLevelText)
                    EnhancePageTools.refreshStone(stoneSlot.itemStack, stoneLevelText, stoneProbText)
                } else {
                    if (code == 1) {
                        val newStoneStack = stoneSlot.itemStack.clone()
                        newStoneStack.amount -= 1
                        stoneSlot.itemStack = newStoneStack
                    }
                    imageFail.w = 24
                    imageFail.h = 24
                }
                WuxieAPI.updateGui(pl)
            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        buttonOutput.w = 25
        buttonOutput.h = 25
        buttonOutput.tooltips = listOf("§f强化")
        guiContainer.add(buttonOutput)
        guiContainer.add(imageSuccess)
        guiContainer.add(imageFail)
        enhanceLevelText.scale = 0.0
        guiContainer.add(enhanceLevelText)
        stoneLevelText.scale = 0.0
        guiContainer.add(stoneLevelText)
        stoneProbText.scale = 0.0
        guiContainer.add(stoneProbText)
        titleText.scale = 1.2
        guiContainer.add(titleText)
        return enhanceGui
    }

}