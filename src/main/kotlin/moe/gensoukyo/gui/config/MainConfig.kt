package moe.gensoukyo.gui.config

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

object MainConfig {

    @Config
    lateinit var conf: SecuredFile
        private set

    @Awake(LifeCycle.ENABLE)
    fun loadConfig() {}

}