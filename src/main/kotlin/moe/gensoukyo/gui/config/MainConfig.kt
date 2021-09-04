package moe.gensoukyo.gui.config

import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

object MainConfig{

    @Config
    lateinit var conf: SecuredFile
        private set

    @Config("AlchemyItems.yml")
    lateinit var alchemyItems: SecuredFile
        private set
}