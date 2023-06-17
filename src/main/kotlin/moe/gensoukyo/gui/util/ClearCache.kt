package moe.gensoukyo.gui.util

import moe.gensoukyo.lib.server.npcApi
import org.bukkit.entity.Player
import moe.gensoukyo.gui.pages.Pages.pages

object ClearCache {
    fun run(pl: Player) {
        val iPlayer = pl.npcApi
        pages.forEach {
            iPlayer.tempdata.remove("${iPlayer.name}_${it}_Gui")
        }
    }
}
