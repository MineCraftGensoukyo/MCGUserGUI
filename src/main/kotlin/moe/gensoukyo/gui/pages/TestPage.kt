package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.animation.ImageAnimationNode
import me.wuxie.wakeshow.wakeshow.ui.animation.ImageAnimationNodeList
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WImage
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitPlayer

class TestPage : Page{
    companion object {
        private const val GUI_BG_TEST1 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/GUI_BG_TEST1.png"
        private const val X_DEVIATION = 0
        private const val Y_DEVIATION = 0

        private const val BTN_1_URL1 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(3).png"
        private const val BTN_1_URL2 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(1).png"
        private const val BTN_1_URL3 = "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/btn_1%20(2).png"
    }



    private val posGuiTest1 = Pos(-1, -1, 176, 166, X_DEVIATION, Y_DEVIATION)
    private val testGui =
        WInventoryScreen("测试UI", GUI_BG_TEST1, posGuiTest1.dx, posGuiTest1.dy, posGuiTest1.w, posGuiTest1.h, 8, 84)
    private val guiContainer = testGui.container
    private val itemInput1 = WSlot(guiContainer, "item_input_1", ItemStack(Material.AIR), 30, 17)
    private val itemInput2 = WSlot(guiContainer, "item_input_2", ItemStack(Material.AIR), 48, 17)
    private val itemInput3 = WSlot(guiContainer, "item_input_3", ItemStack(Material.AIR), 66, 17)
    private val itemInput4 = WSlot(guiContainer, "item_input_4", ItemStack(Material.AIR), 30, 35)
    private val itemInput5 = WSlot(guiContainer, "item_input_5", ItemStack(Material.AIR), 48, 35)
    private val itemInput6 = WSlot(guiContainer, "item_input_6", ItemStack(Material.AIR), 66, 35)
    private val itemInput7 = WSlot(guiContainer, "item_input_7", ItemStack(Material.AIR), 30, 53)
    private val itemInput8 = WSlot(guiContainer, "item_input_8", ItemStack(Material.AIR), 48, 53)
    private val itemInput9 = WSlot(guiContainer, "item_input_9", ItemStack(Material.AIR), 66, 53)
    private val itemOutput = WSlot(guiContainer, "item_output_1", ItemStack(Material.AIR), 120, 32)
    val inputList = listOf(
        itemInput1,
        itemInput2,
        itemInput3,
        itemInput4,
        itemInput5,
        itemInput6,
        itemInput7,
        itemInput8,
        itemInput9
    )

    //按钮
    val btnOutput =
        WButton(guiContainer, "btn_output_1", "合成物品", BTN_1_URL1, BTN_1_URL2, BTN_1_URL3, 220, 120)

    override fun getPage(): WInventoryScreen {
        //节点动画
        val img1_a = ImageAnimationNodeList()
        val img1_a_list = img1_a.animationList
        val img1_anode1 = ImageAnimationNode()
        img1_anode1.rotateTo = -45.0f
        img1_anode1.animationFrame = 20
        val img1_anode2 = ImageAnimationNode()
        img1_anode2.rotateTo = 45.0f
        img1_anode2.animationFrame = 20
        img1_a_list.add(img1_anode1)
        img1_a_list.add(img1_anode2)
        img1_a_list.add(img1_anode1)
        img1_a_list.add(img1_anode2)
        img1_a.rotateModel = 0
        //注册组件
        val img1 = WImage(
            guiContainer,
            "img_1",
            "https://cdn.jsdelivr.net/gh/Zake-arias/myimgs//imgs/GUI_TEST1_img1.png",
            0,
            0,
            16,
            16
        )
        img1.animationNodeList = img1_a
        guiContainer.add(img1)
        inputList.forEach {
            it.isCanDrag = true
            guiContainer.add(it)
        }
        itemOutput.scale = 1.5f
        guiContainer.add(itemOutput)
        guiContainer.add(btnOutput)

        return testGui
    }

}