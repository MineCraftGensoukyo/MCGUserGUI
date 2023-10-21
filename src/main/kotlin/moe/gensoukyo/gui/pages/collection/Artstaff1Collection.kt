package moe.gensoukyo.gui.pages.collection

import me.wuxie.wakeshow.wakeshow.ui.Container
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


class Artstaff1Collection : CollectionPage {
    private val imageRoot = "location:mcgproject:textures/gui"
    private val backgroundImage = "$imageRoot/collection_artstaff1.png"


    private val guiTestPos = Pos(-1, -91, 512, 512, 0, 0)
    private val x0 = 175
    private val y0 = 240


    override val needsLore = arrayOf<String>()
    override val onlyAllowHaveSingleStack = false
    override val unLegalNotice = "这不是时装！"
    override fun checkItemLegal(item: ItemStack): Boolean {
        return item.type.name == "ARMOURERS_WORKSHOP_ITEMSKIN"
    }

    override fun getLabel(container: Container) {
        WButton(
            container,
            "button_tage_artstaff1",
            "§2§l时装",
            "$imageRoot/collection_tag_artstaff1_1.png",
            "$imageRoot/collection_tag_artstaff1_2.png",
            "$imageRoot/collection_tag_artstaff1_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = x0 + 18 * 9 + 32
            y = y0 + 2 + 21 + 6
            container.add(this)
        }
    }

    override fun getPageID() = "collection_artstaff1"
    override fun getPage(): WxScreen {
        val gui = WInventoryScreen(
            getPageID(),
            backgroundImage,
            guiTestPos.dx,
            guiTestPos.dy,
            guiTestPos.w,
            guiTestPos.h,
            x0,
            y0 + 18 * 6 + 18
        )
        for (l in 0..5)
            for (i in 0..8) {
                val slotName = "slot$l-$i"
                val x = x0 + 18 * i
                val y = y0 + 18 * l
                createEmptySlot(gui.container, slotName, x, y)
            }

        return gui
    }

    private fun createEmptySlot(container: Container, name: String, x: Int, y: Int) {
        WSlot(
            container, name, ItemStack(Material.AIR), x, y
        ).apply {
            isCanDrag = true
            container.add(this)
        }
    }
}