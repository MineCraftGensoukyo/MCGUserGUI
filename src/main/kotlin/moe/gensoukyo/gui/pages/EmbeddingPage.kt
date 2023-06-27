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
    private val GUI_BACKGROUND =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BG.png", VERSION)
    private val BTN_1 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_1.png", VERSION)
    private val BTN_2 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_2.png", VERSION)
    private val BTN_3 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_3.png", VERSION)
    private val SUCCESS_URL =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_Success.png", VERSION)
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
    private val titleList: MutableList<String> = ArrayList()

    init {
        titleList.add("§e§l镶嵌")
    }

    private val titleText = WTextList(guiContainer, "title_text", titleList, 90, 10, 600, 20)
    private val equipmentTipsText = WTextList(
        guiContainer, "equipment_tips", ArrayList(),
        150, 30, 60, 20
    )
    private val stoneTipsText = WTextList(
        guiContainer, "stone_tips", ArrayList(),
        9, 20, 60, 20
    )
    private val stoneSlot = WSlot(guiContainer, "stone_slot", ItemStack(Material.AIR), 51, 41)
    private val equipmentSlot = WSlot(guiContainer, "equipment_slot", ItemStack(Material.AIR), 101, 41)

    init {
        equipmentSlot.isCanDrag = true
        stoneSlot.isCanDrag = true
        button.tooltips = listOf("确认镶嵌")
        button.w = 25
        button.h = 25
        button.function = ClickFunction { t: Int, pl: Player? ->
            val tips = EmbeddingTools.embeddingApprovalCheck(equipmentSlot.itemStack, stoneSlot.itemStack)
            if (tips.isEmpty()) {
                val newEquipment = EmbeddingTools.embedding(equipmentSlot.itemStack, stoneSlot.itemStack)
                equipmentSlot.itemStack = newEquipment
                val newStone = stoneSlot.itemStack.clone()
                newStone.amount = newStone.amount - 1
                stoneSlot.itemStack = newStone
                button.w = 0
                button.h = 0
                equipmentTipsText.content = listOf("§a镶嵌成功")
            } else {
                equipmentTipsText.content = listOf(tips)
            }
            WuxieAPI.updateGui(pl)
        }
        guiContainer.add(button)
        guiContainer.add(titleText)
        guiContainer.add(stoneTipsText)
        guiContainer.add(equipmentTipsText)
        guiContainer.add(equipmentSlot)
        guiContainer.add(stoneSlot)
    }

    override fun getPage(): WxScreen {
        return gui
    }
}
