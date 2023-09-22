package moe.gensoukyo.gui.util

import org.bukkit.entity.Player

object CheckModExistence {
    fun run(pl: Player): Boolean {
        val channelSet = pl.listeningPluginChannels
        return channelSet.contains("WakeShow_client") ||
               channelSet.contains("WakeShow_server")
    }
}