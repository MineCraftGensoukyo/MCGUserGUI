package moe.gensoukyo.gui.util

import moe.gensoukyo.gui.config.MainConfig
import noppes.npcs.api.entity.IPlayer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.getItemStack

class RecipeCheck {
    companion object {
        private val levelUp = intArrayOf(0, 10, 100, 1000, 100000000)
    }

    fun RecipeCheck(pl: IPlayer<*>, slots: List<ItemStack>): ItemStack? {
        var inPotIngredients: HashMap<String, Int> = HashMap<String, Int>()
        slots.forEach {
            var ingredient = it.itemMeta?.displayName
            if (it.type == Material.APPLE) {
                ingredient = "红苹果"
            } else {
                if (ingredient == null) {
                    return null
                }
                ingredient = ChatColor.stripColor(ingredient)
            }
            if (MainConfig.alchemyItems.contains(ingredient)) {
                val cnt = inPotIngredients[ingredient]
                if (cnt != null) {
                    inPotIngredients[ingredient!!] = cnt + 1
                } else {
                    inPotIngredients[ingredient!!] = 1
                }
            } else {
                return null
            }
        }
        MainConfig.alchemyRecipes.recipeList.forEach nextRecipe@{ recipe ->
            //配方匹配
            if (recipe.size != inPotIngredients.size + 4) return@nextRecipe
            if (recipe["level"]!! > pl.getFactionPoints(143)) return@nextRecipe
            recipe.forEach nextIngredient@{
                val ingredient = it.key
                if (ingredient == "name" || ingredient == "level") return@nextIngredient
                if (ingredient == "exp" || ingredient == "quantity") return@nextIngredient
                val cnt = inPotIngredients[ingredient] ?: return@nextRecipe
                if (cnt != it.value) return@nextRecipe
            }

            //经验与升级
            pl.addFactionPoints(144, recipe["exp"]!!)
            val currentLevel = pl.getFactionPoints(143)
            if (pl.getFactionPoints(144) >= levelUp[currentLevel]) {
                pl.addFactionPoints(143, 1)
                pl.addFactionPoints(144, -levelUp[currentLevel])
                pl.message("您的炼金等级已升至${currentLevel + 1}级！")
            }

            //返回item
            val itemName = MainConfig.alchemyRecipes.recipeNameList[recipe["name"]!!]
            return MainConfig.alchemyItems.getItemStack(itemName!!)
        }
        return null
    }
}