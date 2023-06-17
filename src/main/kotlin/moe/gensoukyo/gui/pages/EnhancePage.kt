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

    private val GUI_W = conf["enhance.gui.w"] as Int
    private val GUI_H = conf["enhance.gui.h"] as Int
    private val GUI_SL = conf["enhance.gui.sl"] as Int
    private val GUI_ST = conf["enhance.gui.st"] as Int
    private val EQUIP_X = conf["enhance.equip.x"] as Int
    private val EQUIP_Y = conf["enhance.equip.y"] as Int
    private val STONE_X = conf["enhance.stone.x"] as Int
    private val STONE_Y = conf["enhance.stone.y"] as Int
    private val BTN_X = conf["enhance.btn.x"] as Int
    private val BTN_Y = conf["enhance.btn.y"] as Int
    private val BTN_W = conf["enhance.btn.w"] as Int
    private val BTN_H = conf["enhance.btn.h"] as Int
    private val STATUS_X = conf["enhance.status.x"] as Int
    private val STATUS_Y = conf["enhance.status.y"] as Int
    private val STATUS_W = conf["enhance.status.w"] as Int
    private val STATUS_H = conf["enhance.status.h"] as Int
    private val ENHANCE_LEVEL_X = conf["enhance.enhanceLevel.x"] as Int
    private val ENHANCE_LEVEL_Y = conf["enhance.enhanceLevel.y"] as Int
    private val ENHANCE_LEVEL_W = conf["enhance.enhanceLevel.w"] as Int
    private val ENHANCE_LEVEL_H = conf["enhance.enhanceLevel.h"] as Int
    private val STONE_LEVEL_X = conf["enhance.stoneLevel.x"] as Int
    private val STONE_LEVEL_Y = conf["enhance.stoneLevel.y"] as Int
    private val STONE_LEVEL_W = conf["enhance.stoneLevel.w"] as Int
    private val STONE_LEVEL_H = conf["enhance.stoneLevel.h"] as Int
    private val STONE_PROB_X = conf["enhance.stoneProb.x"] as Int
    private val STONE_PROB_Y = conf["enhance.stoneProb.y"] as Int
    private val STONE_PROB_W = conf["enhance.stoneProb.w"] as Int
    private val STONE_PROB_H = conf["enhance.stoneProb.h"] as Int

    private val enhanceGui =
        WInventoryScreen("强化UI", BG_URL, -1, -1, GUI_W, GUI_H, GUI_SL, GUI_ST)
    private val guiContainer = enhanceGui.container
    private val stoneSlot =
        WSlot(guiContainer, "stone", ItemStack(Material.AIR), STONE_X, STONE_Y)
    private val equipmentSlot =
        WSlot(guiContainer, "equipment", ItemStack(Material.AIR), EQUIP_X, EQUIP_Y)
    private val buttonOutput =
        WButton(guiContainer, "button_output", "",
            BTN_URL_1, BTN_URL_2, BTN_URL_3, BTN_X, BTN_Y)
    private val imageSuccess =
        WImage(guiContainer, "image_success",
            SUCCESS_URL, STATUS_X, STATUS_Y, 0, 0)
    private val imageFail =
        WImage(guiContainer, "image_fail",
            FAIL_URL, STATUS_X, STATUS_Y, 0, 0)
    private val enhanceLevelText =
        WTextList(guiContainer, "enhance_level_text", listOf(),
            ENHANCE_LEVEL_X, ENHANCE_LEVEL_Y, ENHANCE_LEVEL_W, ENHANCE_LEVEL_H)
    private val stoneLevelText =
        WTextList(guiContainer, "stone_level_text", listOf(),
            STONE_LEVEL_X, STONE_LEVEL_Y, STONE_LEVEL_W, STONE_LEVEL_H)
    private val stoneProbText =
        WTextList(guiContainer, "stone_prob_text", listOf(),
            STONE_PROB_X, STONE_PROB_Y, STONE_PROB_W, STONE_PROB_H)

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
                    imageSuccess.w = STATUS_W
                    imageSuccess.h = STATUS_H
                    EnhancePageTools.refreshEquip(equipmentSlot.itemStack, enhanceLevelText)
                    EnhancePageTools.refreshStone(stoneSlot.itemStack, stoneLevelText, stoneProbText)
                } else {
                    if (code == 1) {
                        val newStoneStack = stoneSlot.itemStack.clone()
                        newStoneStack.amount -= 1
                        stoneSlot.itemStack = newStoneStack
                    }
                    imageFail.w = STATUS_W
                    imageFail.h = STATUS_H
                }
                WuxieAPI.updateGui(pl)
            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        buttonOutput.w = BTN_W
        buttonOutput.h = BTN_H
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
        return enhanceGui
    }

}