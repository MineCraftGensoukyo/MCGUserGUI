package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EquipmentEnhance
import taboolib.common.platform.function.warning
import taboolib.platform.util.isAir

class EnhancePage : Page{
    companion object {
        private const val BG_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages/img/Enhance_BG.png"
        private const val BTN_URL_1 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages/img/Enhance_BTN_1.png"
        private const val BTN_URL_2 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages/img/Enhance_BTN_2.png"
        private const val BTN_URL_3 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages/img/Enhance_BTN_3.png"
        private const val BLANK_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages/img/Blank.png"
    }

    private val GUI_W = conf["enhance.gui.w"] as Int
    private val GUI_H = conf["enhance.gui.h"] as Int
    private val GUI_SL = conf["enhance.gui.sl"] as Int
    private val GUI_ST = conf["enhance.gui.st"] as Int
    private val EQUIP_X = conf["enhance.equip.x"] as Int
    private val EQUIP_Y = conf["enhance.equip.y"] as Int
    private val STONE_X = conf["enhance.stone.x"] as Int
    private val STONE_Y = conf["enhance.stone.y"] as Int
    private val OUTPUT_X = conf["enhance.output.x"] as Int
    private val OUTPUT_Y = conf["enhance.output.y"] as Int
    private val BTN_X = conf["enhance.btn.x"] as Int
    private val BTN_Y = conf["enhance.btn.y"] as Int
    private val BTN_W = conf["enhance.btn.w"] as Int
    private val BTN_H = conf["enhance.btn.h"] as Int
    private val TITLE_X = conf["enhance.title.x"] as Int
    private val TITLE_Y = conf["enhance.title.y"] as Int
    private val TITLE_C = conf["enhance.title.color"] as String

    private val enhanceGui =
        WInventoryScreen("强化UI", BG_URL, -1, -1, GUI_W, GUI_H, GUI_SL, GUI_ST)
    private val guiContainer = enhanceGui.container
    private val stoneInput =
        WSlot(guiContainer, "stone_input", ItemStack(Material.AIR), STONE_X, STONE_Y)
    private val equipmentInput =
        WSlot(guiContainer, "equipment_input", ItemStack(Material.AIR), EQUIP_X, EQUIP_Y)
    private val equipmentOutput =
        WSlot(guiContainer, "equipment_output", ItemStack(Material.AIR), OUTPUT_X, OUTPUT_Y)
    private val buttonOutput =
        WButton(guiContainer, "button_output", "", BTN_URL_1, BTN_URL_2, BTN_URL_3, BTN_X, BTN_Y)
    private val titleText =
        WButton(guiContainer, "title_text", "§${TITLE_C}§l强化", BLANK_URL, BLANK_URL, BLANK_URL, TITLE_X, TITLE_Y)

    override fun getPage(): WxScreen {
        equipmentInput.isCanDrag = true
        equipmentInput.emptyTooltips = listOf("§f请放入武器")
        guiContainer.add(equipmentInput)
        stoneInput.isCanDrag = true
        stoneInput.emptyTooltips = listOf("§f请放入强化石")
        guiContainer.add(stoneInput)
        equipmentOutput.isCanDrag = true
        guiContainer.add(equipmentOutput)
        buttonOutput.setFunction { _, pl ->
            try {
                if (equipmentOutput.itemStack != null && !equipmentOutput.itemStack.isAir) {
                    pl.sendMessage("§c请先取出强化后的装备")
                } else {
                    val ret = EquipmentEnhance(
                        pl, equipmentInput.itemStack, stoneInput.itemStack).run()
                    if (ret != null) {
                        equipmentOutput.itemStack = ret
                        equipmentInput.itemStack = ItemStack(Material.AIR)
                        val newStoneStack = stoneInput.itemStack.clone()
                        newStoneStack.amount -= 1
                        stoneInput.itemStack = newStoneStack
                        WuxieAPI.updateGui(pl)
                    }
                }

            } catch (e: Exception) {
                warning(e, e.stackTrace.first())
            }
        }
        buttonOutput.w = BTN_W
        buttonOutput.h = BTN_H
        buttonOutput.tooltips = listOf("§f强化")
        guiContainer.add(buttonOutput)
        guiContainer.add(titleText)
        return enhanceGui
    }

}