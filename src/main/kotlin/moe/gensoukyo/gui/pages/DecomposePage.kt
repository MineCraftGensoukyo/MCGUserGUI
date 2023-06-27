package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.config.MainConfig.conf
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DecomposePage : Page {
    private val VERSION = conf["imageVersion"] as String
    private val BG_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Decompose_BG.png"
    private val BTN_URL_1 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Decompose_BTN_1.png"
    private val BTN_URL_2 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Decompose_BTN_2.png"
    private val BTN_URL_3 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Decompose_BTN_3.png"

    private val EQUIP_X = conf["decompose.equip.x"] as Int
    private val EQUIP_Y = conf["decompose.equip.y"] as Int
    private val OUTPUT_X = conf["decompose.output.x"] as Int
    private val OUTPUT_Y = conf["decompose.output.y"] as Int
    private val BUTTON_X = conf["decompose.button.x"] as Int
    private val BUTTON_Y = conf["decompose.button.y"] as Int
    private val BUTTON_W = conf["decompose.button.w"] as Int
    private val BUTTON_H = conf["decompose.button.h"] as Int

    private val decomposeGui =
        WInventoryScreen("分解UI", BG_URL, -1, -1, 190, 190, 15, 110)
    private val guiContainer = decomposeGui.getContainer()
    private val equipmentSlot =
        WSlot(guiContainer, "equipment", ItemStack(Material.AIR), EQUIP_X, EQUIP_Y)
    private val outputSlot =
        WSlot(guiContainer, "output", ItemStack(Material.AIR), OUTPUT_X, OUTPUT_Y)
    private val decomposeButton =
        WButton(guiContainer, "decompose", "",
            BTN_URL_1, BTN_URL_2, BTN_URL_3, BUTTON_X, BUTTON_Y)

    override fun getPage(): WxScreen {
        equipmentSlot.isCanDrag = true
        equipmentSlot.emptyTooltips = listOf("§f请放入装备")
        guiContainer.add(equipmentSlot)
        outputSlot.isCanDrag = true
        guiContainer.add(outputSlot)
        decomposeButton.tooltips = listOf("§f分解")
        decomposeButton.w = BUTTON_W
        decomposeButton.h = BUTTON_H
        guiContainer.add(decomposeButton)
        return decomposeGui
    }
}
