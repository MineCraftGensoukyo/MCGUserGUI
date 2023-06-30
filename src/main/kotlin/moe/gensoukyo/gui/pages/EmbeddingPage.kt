package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.ClickFunction
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WImage
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EmbeddingTools
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EmbeddingPage : Page {
    private val VERSION = conf["imageVersion"] as String
    private val GUI_BACKGROUND = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BG.png"
    private val BTN_1 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_1.png"
    private val BTN_2 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_2.png"
    private val BTN_3 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_BTN_3.png"
    private val SUCCESS_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_Success.png"
    private val FAIL_URL = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Enhance_Fail.png"
    private val guiTestPos = Pos(-1, -1, 190, 190, 0, 0)
    private val gui = WInventoryScreen(
        "镶嵌UI",
        GUI_BACKGROUND,
        guiTestPos.dx, guiTestPos.dy, guiTestPos.w, guiTestPos.h, 15, guiTestPos.h - 80
    )
    private val guiContainer = gui.container
    private val button = WButton(
        guiContainer, "embedding_button", "",
        BTN_1, BTN_2, BTN_3,
        120, 35
    )
    private val imageSuccess = WImage(
        guiContainer, "image_success",
        SUCCESS_URL, 152, 38, 0, 0
    )
    private val imageFail = WImage(
        guiContainer, "image_fail",
        FAIL_URL, 152, 38, 0, 0)
    private val titleText = WTextList(guiContainer, "title_text", listOf("§1§l镶  嵌"), 80, 4, 40, 20)
    private val equipmentTipsText = WTextList(
        guiContainer, "equipment_tips", listOf(),
        149, 30, 60, 20
    )
    private val stoneTipsText = WTextList(
        guiContainer, "stone_tips", listOf(),
        9, 20, 60, 20
    )
    private val stoneSlot = WSlot(guiContainer, "stone_slot", ItemStack(Material.AIR), 51, 41)
    private val equipmentSlot = WSlot(guiContainer, "equipment_slot", ItemStack(Material.AIR), 101, 41)

    override fun getPage(): WxScreen {
        equipmentSlot.isCanDrag = true
        stoneSlot.isCanDrag = true
        equipmentSlot.emptyTooltips = listOf("§f请放入装备")
        stoneSlot.emptyTooltips = listOf("§f请放入强化石")
        equipmentTipsText.scale = 0.7
        stoneTipsText.scale = 0.7
        button.tooltips = listOf("§f确认镶嵌")
        button.w = 0
        button.h = 0
        button.function = ClickFunction { t: Int, pl: Player? ->
            imageSuccess.w = 0
            imageSuccess.h = 0
            imageFail.w = 0
            imageFail.h = 0

            val tips = EmbeddingTools.embeddingApprovalCheck(equipmentSlot.itemStack, stoneSlot.itemStack)
            if (tips.isEmpty()) {
                val newEquipment = EmbeddingTools.embedding(equipmentSlot.itemStack, stoneSlot.itemStack)
                equipmentSlot.itemStack = newEquipment
                val newStone = stoneSlot.itemStack.clone()
                newStone.amount = newStone.amount - 1
                stoneSlot.itemStack = newStone

                if(!EmbeddingTools.embeddingEquipmentCheck(newEquipment).isEmpty()) button.w = 0
                button.h = 0

                imageSuccess.w = 24
                imageSuccess.h = 24
                equipmentTipsText.content = listOf("§a§l镶嵌成功")

            } else {
                imageFail.w = 24
                imageFail.h = 24
                equipmentTipsText.content = listOf(tips)
                stoneTipsText.content = listOf(tips)
            }
            WuxieAPI.updateGui(pl)
        }
        guiContainer.add(button)
        titleText.scale = 1.2
        guiContainer.add(titleText)
        guiContainer.add(stoneTipsText)
        guiContainer.add(equipmentTipsText)
        guiContainer.add(equipmentSlot)
        guiContainer.add(stoneSlot)
        guiContainer.add(imageSuccess)
        guiContainer.add(imageFail)

        return gui
    }
}
