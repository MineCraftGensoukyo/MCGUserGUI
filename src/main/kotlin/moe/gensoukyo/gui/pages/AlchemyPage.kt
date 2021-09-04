package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WCooldingTag
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.lib.server.bukkit
import noppes.npcs.api.event.NpcEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class AlchemyPage :Page {
    companion object {
        private const val PLAYER_LEVEL_ID = 67

        private const val GUI_ALCHEMY_1 =
            "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs//ALCHEMY_GUI_01.png"

        //全局偏移，一般不要调
        private const val X_DEVIATION = 0
        private const val Y_DEVIATION = 0

        private const val BTN_1_URL1 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(3).png"
        private const val BTN_1_URL2 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(1).png"
        private const val BTN_1_URL3 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(2).png"
        private const val COLD_1 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/COLD_BG_ALCHEMY.png"
        private const val COLD_1_BG = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/COLD_ALCHEMY.png"
    }

    data class Pos(val x: Int, val y: Int, val w: Int, val h: Int) {
        val dx = x + X_DEVIATION
        val dy = y + Y_DEVIATION
    }

    private val air = ItemStack(Material.AIR)

    override fun getPage(): WxScreen {
        //GUI主体的坐标参数,-1为自动居中
        val posGuiTest1 = Pos(-1, -1, 249, 186)

        //GUI对象
        var Gui =
            WInventoryScreen(
                "炼金UI",
                GUI_ALCHEMY_1,
                posGuiTest1.dx,
                posGuiTest1.dy,
                posGuiTest1.w,
                posGuiTest1.h,
                7,
                105
            )
        val guiContainer = Gui.container

        //输入物品栏
        val itemInput1 = WSlot(guiContainer, "item_input_1", air, 16, 22)
        val itemInput2 = WSlot(guiContainer, "item_input_2", air, 53, 5)
        val itemInput3 = WSlot(guiContainer, "item_input_3", air, 86, 22)
        val itemInput4 = WSlot(guiContainer, "item_input_4", air, 86, 64)
        val itemInput5 = WSlot(guiContainer, "item_input_5", air, 53, 81)
        val itemInput6 = WSlot(guiContainer, "item_input_6", air, 16, 64)
        val itemInputBook = WSlot(guiContainer, "item_input_book", air, 125, 77)


        //输出物品栏
        val itemOutput = WSlot(guiContainer, "item_output_1", air, 174, 43)
        val inputList = listOf(
            itemInput1,
            itemInput2,
            itemInput3,
            itemInput4,
            itemInput5,
            itemInput6,
            itemInputBook
        )

        //按钮
        val btnOutput =
            WButton(guiContainer, "btn_output_1", "炼金做成", BTN_1_URL1, BTN_1_URL2, BTN_1_URL3, 173, 120)

        //冷却条
        val coolingTag = WCooldingTag(guiContainer, "cool_output_1", 122, 45, 46, 10, 0, 20, COLD_1, COLD_1_BG)

        //注册物品栏
        itemInputBook.tooltips = listOf("配方书")
        for (slot in inputList) {
            slot.isCanDrag = true
            guiContainer.add(slot)
        }
        itemOutput.isCanDrag = true
        guiContainer.add(itemOutput)
        //注册冷却条
        coolingTag.currentTime = 0
        guiContainer.add(coolingTag)
        //设置按钮参数
        btnOutput.url1 = BTN_1_URL1
        btnOutput.url2 = BTN_1_URL2
        btnOutput.url3 = BTN_1_URL3
        btnOutput.w = 70
        btnOutput.h = 19
        btnOutput.isCanPress = true
        guiContainer.add(btnOutput)

        return Gui
    }
}