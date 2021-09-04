package moe.gensoukyo.gui.config

import taboolib.common.platform.function.warning

class CsvConfiguration {
    var recipeList: MutableList<HashMap<String, Int>> = mutableListOf()

    fun reload(){
        try {
            CsvConfigLoader.load()
        } catch (e: Exception) {
            warning(e)
        }
    }
}