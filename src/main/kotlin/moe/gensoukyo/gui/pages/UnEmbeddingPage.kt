package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.ClickFunction
import me.wuxie.wakeshow.wakeshow.ui.Component
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.*
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.util.EmbeddingTools
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class UnEmbeddingPage : Page {
    private val VERSION = conf["imageVersion"] as String
    private val GUI_BACKGROUND =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BG.png", VERSION)
    private val BTN_1 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_1.png", VERSION)
    private val BTN_2 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_2.png", VERSION)
    private val BTN_3 =
        String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_3.png", VERSION)
    private val GUI_POS = Pos(-1, -1, 190, 190, 0, 0)
    private val gui = WInventoryScreen(
        "摘除镶嵌UI",
        GUI_BACKGROUND,
        GUI_POS.dx, GUI_POS.dy, GUI_POS.w, GUI_POS.h, 15, GUI_POS.h - 80
    )
    private val guiContainer = gui.container
    private val scroll = WScrollingContainer(
        guiContainer, "choose_scroll",
        147, 4, 39, 68, 200
    )
    private val titleText = WTextList(guiContainer, "title_list", listOf("§9§l摘除镶嵌"), 78, 5, 60, 20)
    private val tipsText = WTextList(guiContainer, "tips_list", listOf(), 35, 62, 60, 20)
    private val equipmentSlot = WSlot(guiContainer, "equipment_slot", ItemStack(Material.AIR), 88, 42)
    private val decide_button = WButton(
        guiContainer, "decide_button", "",
        BTN_1, BTN_2, BTN_3,
        25, 35
    )

    override fun getPage(): WxScreen {
        tipsText.scale = 0.9
        equipmentSlot.emptyTooltips = listOf("§f请放入装备")
        decide_button.w = 45
        decide_button.h = 13
        decide_button.isCanPress = false
        decide_button.tooltips = listOf("§f摘除镶嵌")
        decide_button.function = ClickFunction { t: Int, pl: Player ->
            val scrollContainer = scroll.container
            val item = equipmentSlot.itemStack
            val itemLore = item.itemMeta!!.lore!!
            val unembeddingList: MutableList<String> = ArrayList()
            scrollContainer.componentMap.forEach { (key: String, value: Component?) ->
                if (value !is WCheckBox) return@forEach
                if (value.isSelect) unembeddingList.add(key)
            }
            if (unembeddingList.isEmpty()) {
                tipsText.content = listOf("§c未选择")
            } else {
                val returnList = EmbeddingTools.unEmbedding(itemLore, unembeddingList)
                val newLore = returnList.first
                val primordialStoneList = returnList.second
                if (EmbeddingTools.inventoryPlentyFor(pl, primordialStoneList)) {
                    val newEquipment = item.clone()
                    val newMeta = item.itemMeta!!.clone()
                    newMeta.lore = newLore
                    newEquipment.setItemMeta(newMeta)
                    equipmentSlot.itemStack = newEquipment
                    primordialStoneList.forEach(Consumer { itemStacks: ItemStack? -> pl.inventory.addItem(itemStacks) })
                    decide_button.isCanPress = false
                    scrollContainer.componentMap.keys.forEach(Consumer { component: String? ->
                        scrollContainer.remove(
                            component
                        )
                    })

                    EmbeddingTools.unEmbeddingCheck(newEquipment,guiContainer,pl)
                    tipsText.content = listOf("§a摘除成功")
                } else {
                    tipsText.content = listOf("§c背包不足")
                }
            }
            WuxieAPI.updateGui(pl)
        }
        equipmentSlot.isCanDrag = true
        scroll.barWidth = 5
        scroll.isShowScrollBar = false
        guiContainer.add(decide_button)
        guiContainer.add(titleText)
        guiContainer.add(tipsText)
        guiContainer.add(scroll)
        guiContainer.add(equipmentSlot)

        return gui
    }
}