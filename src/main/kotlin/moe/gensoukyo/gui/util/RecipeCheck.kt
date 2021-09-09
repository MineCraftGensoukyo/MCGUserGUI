package moe.gensoukyo.gui.util

import moe.gensoukyo.gui.config.MainConfig
import noppes.npcs.api.entity.IPlayer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning

class RecipeCheck(private val pl: IPlayer<*>, private val slots: List<ItemStack>) {
    companion object {
        private val levelUp = intArrayOf(0, 100000000, 100000000, 100000000, 100000000)
    }

    fun run(): ItemStack? {
        var inPotIngredients: HashMap<String, Int> = HashMap<String, Int>()
        val alchemyExpId = MainConfig.conf.getInt("alchemyExpId")
        val alchemyLevelId = MainConfig.conf.getInt("alchemyLevelId")
        try {
            slots.forEach {
                if (it == null || it == ItemStack(Material.AIR)) return@forEach
                var ingredient = it.itemMeta?.displayName
                if (it.type == Material.APPLE) {
                    ingredient = "红苹果"
                } else {
                    if (ingredient == null) {
                        return null
                    }
                    ingredient = ChatColor.stripColor(ingredient)
                }
                if (MainConfig.alchemyItems.contains(ingredient!!)) {
                    val cnt = inPotIngredients[ingredient]
                    if (cnt != null) {
                        inPotIngredients[ingredient!!] = cnt + it.amount
                        //info("debug:${it.amount}[${this}]")
                        //info("debug:${inPotIngredients}[${this}]")
                    } else {
                        inPotIngredients[ingredient!!] = it.amount
                        //info("debug:${it.amount}[${this}]")
                        //info("debug:${inPotIngredients}[${this}]")
                    }
                } else {
                    return null
                }
            }
            MainConfig.alchemyRecipes.recipeList.forEach nextRecipe@{ recipe ->
                //配方匹配
                if (recipe.size != inPotIngredients.size + 4) return@nextRecipe
                val unlockId = recipe["unlockId"]!!
                if (unlockId > 900) {
                    if (!pl.hasReadDialog(unlockId)) return@nextRecipe
                } else {
                    if (!pl.hasFinishedQuest(unlockId)) return@nextRecipe
                }
                recipe.forEach nextIngredient@{
                    val ingredient = it.key
                    if (ingredient == "name" || ingredient == "level") return@nextIngredient
                    if (ingredient == "exp" || ingredient == "quantity") return@nextIngredient
                    val cnt = inPotIngredients[ingredient] ?: return@nextRecipe
                    if (cnt != it.value) return@nextRecipe
                }

                //经验与升级
                pl.addFactionPoints(alchemyExpId, recipe["exp"]!!)
                val currentLevel = pl.getFactionPoints(alchemyLevelId)
                if (pl.getFactionPoints(alchemyExpId) >= levelUp[currentLevel]) {
                    pl.addFactionPoints(alchemyLevelId, 1)
                    pl.addFactionPoints(alchemyExpId, -levelUp[currentLevel])
                    pl.message("您的炼金等级已升至${currentLevel + 1}级！")
                }

                //返回item
                val itemName = MainConfig.alchemyRecipes.recipeNameList[recipe["name"]!!]
                //info("debug:${recipe["name"]}[${this}]")
                //info("debug:$itemName[${this}]")
                val res = MainConfig.alchemyItems.getItemStack(itemName!!)
                //info("debug:$res[${this}]")
                return res
            }
        } catch (e: Exception) {
            warning(e, e.stackTrace.first())
        }
        return null
    }
}