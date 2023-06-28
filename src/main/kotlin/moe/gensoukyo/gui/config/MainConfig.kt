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

    lateinit var weapon_types: HashSet<String>
    lateinit var armor_types: HashSet<String>

    @Awake(LifeCycle.ENABLE)
    fun loadItems() {
        items = YamlConfiguration.loadConfiguration(File(conf["itemsPath"] as String))
    }

    @Awake(LifeCycle.ENABLE)
    fun loadEquipmentTypes() {
        weapon_types = conf.getStringList("equipType.weapon").toHashSet()
        armor_types = conf.getStringList("equipType.armor").toHashSet()
    }

    fun YamlConfiguration.reload() = loadItems()

}