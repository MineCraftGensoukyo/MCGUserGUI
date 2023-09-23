package moe.gensoukyo.gui

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.expansion.setupPlayerDatabase
import java.io.File

object MCGUserGUI : Plugin() {
    override fun onEnable() {
        MCGUserGUICommand().register()
        setupPlayerDatabase(File(getDataFolder(), "data.db"))
        info("MCGUserGUI加载完毕！")
        info(
            """§6
            =====================================================================
                 __  __  _____ _____ _    _                _____ _    _ _____ 
                |  \/  |/ ____/ ____| |  | |              / ____| |  | |_   _|
                | \  / | |   | |  __| |  | |___  ___ _ __| |  __| |  | | | |  
                | |\/| | |   | | |_ | |  | / __|/ _ \ '__| | |_ | |  | | | |  
                | |  | | |___| |__| | |__| \__ \  __/ |  | |__| | |__| |_| |_ 
                |_|  |_|\_____\_____|\____/|___/\___|_|   \_____|\____/|_____|
                                $pluginId
                                Author  - DavidWang19, ZakeArias, satorishi, xiaoyv404
                                Version - $pluginVersion
             ====================================================================
        """.trimIndent()
        )
    }
}