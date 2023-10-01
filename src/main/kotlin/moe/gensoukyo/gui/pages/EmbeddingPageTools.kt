package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import org.bukkit.entity.Player
import taboolib.platform.util.giveItem

object EmbeddingPageTools : PageTools{
    override fun giveBackItems(pl: Player, gui: WxScreen) {
        val equip =
            (gui.container.getComponent("equipment_slot") as WSlot).itemStack
        val stoneIn =
            (gui.container.getComponent("stone_slot") as WSlot).itemStack
        if (equip != null) pl.giveItem(equip)
        if (stoneIn != null) pl.giveItem(stoneIn)
        equip?.amount = 0
        stoneIn?.amount = 0
    }

    override fun guiPrepare(player:Player, gui: WxScreen) {
        gui.cursor = null
        gui.container.getComponent("image_success").w = 0
        gui.container.getComponent("image_success").h = 0
        gui.container.getComponent("image_fail").w = 0
        gui.container.getComponent("image_fail").h = 0
        (gui.container.getComponent("equipment_tips") as WTextList).content = listOf()
        (gui.container.getComponent("stone_tips") as WTextList).content = listOf()
        gui.container.getComponent("embedding_button").w = 0
        gui.container.getComponent("embedding_button").h = 0
    }
}
