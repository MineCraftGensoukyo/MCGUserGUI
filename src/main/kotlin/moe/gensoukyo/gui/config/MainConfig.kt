package moe.gensoukyo.gui.config

import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import java.io.File

object MainConfig {

    @Config
    lateinit var conf: SecuredFile
        private set

    lateinit var items: YamlConfiguration

    @Awake(LifeCycle.ENABLE)
    fun loadItems() {
        items = YamlConfiguration.loadConfiguration(File(conf["itemsPath"] as String))
    }

    fun YamlConfiguration.reload() = loadItems()

}