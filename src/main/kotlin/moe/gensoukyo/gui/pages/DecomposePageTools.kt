package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.util.LoreInfoTools
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

object DecomposePageTools : PageTools{
    private val OUTPUT_AMOUNT = mapOf(
        1 to listOf(0, 2, 3, 5, 7, 10),
        2 to listOf(0, 1, 2, 3, 5, 7)
    )

    override fun giveBackItems(pl: Player, gui: WxScreen) {
        val equip =
            (gui.container.getComponent("equipment") as WSlot).itemStack
        val output =
            (gui.container.getComponent("output") as WSlot).itemStack
        if (equip != null) pl.giveItem(equip)
        if (output != null) pl.giveItem(output)
        equip?.amount = 0
        output?.amount = 0
    }

    override fun guiPrepare(player:Player, gui: WxScreen) {
        gui.cursor = null
        (gui.container.getComponent("equipment_text") as WTextList).scale = 0.0
        (gui.container.getComponent("output_text") as WTextList).scale = 0.0
    }

    fun refresh(equip: ItemStack?, output: ItemStack?,
                equipText: WTextList, outputText: WTextList) {
        var outputInfo = false
        if (output != null && !output.isAir) {
            outputText.scale = 0.7
            outputText.content = listOf("§4§l取回物品")
        } else {
            outputInfo = true
        }
        if (equip == null || equip.isAir) {
            equipText.scale = 0.0
            if (outputInfo) outputText.scale = 0.0
            return
        }
        val equipInfo = LoreInfoTools.getEquipmentInfo(equip)
        equipText.scale = 0.7
        val validity = equipInfo["validity"]!!
        if (validity == 0) {
            equipText.content = listOf("§4§l无法分解")
            if (outputInfo) outputText.scale = 0.0
            return
        }
        val quality = equipInfo["quality"]!!
        if (validity == 1) {
            equipText.content = listOf("§1§l品阶: ${quality}")
            if (outputInfo) {
                outputText.scale = 0.7
                outputText.content = listOf("§1§l金属锭: " +
                        "${OUTPUT_AMOUNT[validity]!![quality]}")
            }
        } else {
            equipText.content = listOf("§1§l品阶: ${quality}")
            if (outputInfo) {
                outputText.scale = 0.7
                outputText.content = listOf("§1§l布匹: " +
                        "${OUTPUT_AMOUNT[validity]!![quality]}")
            }
        }
    }
}