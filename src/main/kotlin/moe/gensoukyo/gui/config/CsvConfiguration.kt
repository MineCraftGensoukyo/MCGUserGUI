package moe.gensoukyo.gui.config

import taboolib.common.platform.function.warning

class CsvConfiguration {
    var recipeList: MutableList<HashMap<String, Int>> = mutableListOf()
    var recipeNameList: HashMap<Int, String> = HashMap<Int, String>()

    fun reload() {
        try {
            CsvConfigLoader.load()
        } catch (e: Exception) {
            warning(e)
        }
    }
}