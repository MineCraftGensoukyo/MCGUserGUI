package moe.gensoukyo.gui.config

import sun.applet.Main
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.platform.function.warning
import taboolib.common.reflect.Ref
import taboolib.module.configuration.ConfigLoader
import java.io.File
import java.lang.reflect.Field
import java.util.function.Supplier

object CsvConfigLoader {
    private var recipeList: MutableList<HashMap<String, Int>> = mutableListOf()
    private val recipeNameList: HashMap<Int, String> = HashMap<Int, String>()
    val version = "1.0.0dev"

    @Awake(LifeCycle.ENABLE)
    fun load() {
        info("§6CSV配置加载中！")
        val name = MainConfig.conf["alchemyRecipesPath"].toString()
        try {
            val file = File(name)
            val lines = file.readLines()
            var cnt = 0
            lines.forEach { line ->
                if (line.isEmpty()) return
                val contents = line.split(",")
                var currentRecipe: HashMap<String, Int> = HashMap<String, Int>()
                var index = 0
                var name = ""
                contents.forEach {
                    index++
                    when (index) {
                        1 -> {
                            currentRecipe["name"] = cnt
                            recipeNameList[cnt] = it
                        }
                        2 -> currentRecipe["level"] = it.toInt()
                        3 -> currentRecipe["exp"] = it.toInt()
                        4 -> currentRecipe["quantity"] = it.toInt()
                        else -> {
                            if (index % 2 == 1) {
                                name = it
                            } else {
                                currentRecipe[name] = it.toInt()
                            }
                        }
                    }
                }
                recipeList.add(currentRecipe)
                cnt++
            }
            val result = CsvConfiguration()
            result.recipeList = recipeList
            result.recipeNameList = recipeNameList
            MainConfig.alchemyRecipes = result
            info("§6CSV配置加载完毕！")
        } catch (e: Exception) {
            warning("[CSVLoaderError]${e}")
        }
    }
}

