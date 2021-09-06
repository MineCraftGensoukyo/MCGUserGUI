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

    //@Config("AlchemyItems.yml")
    lateinit var alchemyItems: YamlConfiguration

    //@CsvConfig(".\\mcg_data\\items\\AlchemyRecipes.csv")
    lateinit var alchemyRecipes: CsvConfiguration

    @Awake(LifeCycle.ENABLE)
    fun loadAlchemyItems() {
        alchemyItems = YamlConfiguration.loadConfiguration(File(conf["alchemyItemsPath"].toString()))
    }

    fun YamlConfiguration.reload() = loadAlchemyItems()


}