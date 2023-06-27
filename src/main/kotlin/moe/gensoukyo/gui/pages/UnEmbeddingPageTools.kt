package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import org.bukkit.entity.Player
import taboolib.platform.util.giveItem

object UnEmbeddingPageTools : PageTools{
    override fun giveBackItems(pl: Player, gui: WxScreen) {
        val equip =
            (gui.container.getComponent("equipment_slot") as WSlot).itemStack
        if (equip != null) pl.giveItem(equip)
        equip?.amount = 0
    }
}
