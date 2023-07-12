package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EquipmentDecompose
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

class DecomposePage : Page {
    private val BG_URL = "https://img.gensoukyo.moe:843/images/Decompose_BG.png"
    private val BTN_URL_1 = "https://img.gensoukyo.moe:843/images/Decompose_BTN_1.png"
    private val BTN_URL_2 = "https://img.gensoukyo.moe:843/images/Decompose_BTN_2.png"
    private val BTN_URL_3 = "https://img.gensoukyo.moe:843/images/Decompose_BTN_3.png"

    private val decomposeGui =
        WInventoryScreen("分解UI", BG_URL, -1, -1, 190, 190, 15, 110)
    private val guiContainer = decomposeGui.getContainer()
    private val equipmentSlot =
        WSlot(guiContainer, "equipment", ItemStack(Material.AIR), 56, 39)
    private val outputSlot =
        WSlot(guiContainer, "output", ItemStack(Material.AIR), 118, 39)
    private val decomposeButton =
        WButton(guiContainer, "decompose", "",
            BTN_URL_1, BTN_URL_2, BTN_URL_3, 76, 39)
    private val equipText =
        WTextList(guiContainer, "equipment_text", listOf(), 22, 44, 60, 0)
    private val outputText =
        WTextList(guiContainer, "output_text", listOf(), 142, 44, 60, 0)
    private val titleText =
        WTextList(guiContainer, "title_text", listOf("§1§l分  解"), 80, 4, 40, 20)

    override fun getPage(): WxScreen {
        equipmentSlot.isCanDrag = true
        equipmentSlot.emptyTooltips = listOf("§f请放入装备")
        guiContainer.add(equipmentSlot)
        outputSlot.isCanDrag = true
        guiContainer.add(outputSlot)
        decomposeButton.tooltips = listOf("§f分解")
        decomposeButton.w = 27
        decomposeButton.h = 18
        decomposeButton.setFunction { _, pl ->
            try {
                val equip = equipmentSlot.itemStack
                val output = outputSlot.itemStack
                val (success, product) = EquipmentDecompose.run(pl, equip, output)
                if (success) {
                    outputSlot.itemStack = product
                    equipmentSlot.itemStack = ItemStack(Material.AIR)
                    DecomposePageTools.refresh(null, product, equipText, outputText)
                    WuxieAPI.updateGui(pl)
                }
            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        guiContainer.add(decomposeButton)
        equipText.scale = 0.0
        guiContainer.add(equipText)
        outputText.scale = 0.0
        guiContainer.add(outputText)
        titleText.scale = 1.2
        guiContainer.add(titleText)
        return decomposeGui
    }
}
