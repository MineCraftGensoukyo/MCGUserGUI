package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WScrollingContainer
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import org.bukkit.entity.Player
import taboolib.platform.util.giveItem
import java.util.function.Consumer

object UnEmbeddingPageTools : PageTools{
    override fun giveBackItems(pl: Player, gui: WxScreen) {
        val equip =
            (gui.container.getComponent("equipment_slot") as WSlot).itemStack
        if (equip != null) pl.giveItem(equip)
        equip?.amount = 0
    }

    override fun guiPrepare(gui: WxScreen) {
        gui.cursor = null
        (gui.container.getComponent("tips_list") as WTextList).content = listOf()
        (gui.container.getComponent("decide_button") as WButton).isCanPress = false
        val scrollContainer =
            (gui.container.getComponent("choose_scroll") as WScrollingContainer).container
        scrollContainer.componentMap.keys.forEach(
            Consumer { stone: String? -> scrollContainer.remove(stone) })
    }
}
