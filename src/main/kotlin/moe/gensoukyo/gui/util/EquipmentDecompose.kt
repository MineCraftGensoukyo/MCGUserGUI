package moe.gensoukyo.gui.util

import moe.gensoukyo.gui.config.MainConfig.items
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

object EquipmentDecompose {
    private val OUTPUT_AMOUNT = mapOf(
        1 to listOf(0, 2, 3, 5, 7, 10),
        2 to listOf(0, 1, 2, 3, 5, 7)
    )
    fun run(pl:Player, equip: ItemStack?, output: ItemStack?)
        : Pair<Boolean, ItemStack?> {
        if (output != null && !output.isAir) {
            pl.sendMessage("§c请取出已分解的物品！")
            return Pair(false, null)
        }
        if (equip == null || equip.isAir) {
            pl.sendMessage("§c请放入装备！")
            return Pair(false, null)
        }
        val equipInfo = LoreInfoTools.getEquipmentInfo(equip)
        val validity = equipInfo["validity"]!!
        if (validity == 0) {
            pl.sendMessage("§c请放入合法装备！")
            return Pair(false, null)
        }
        val quality = equipInfo["quality"]!!
        pl.sendMessage("§a分解成功！")
        return if (validity == 1) {
            val product = items.getItemStack("金属锭")!!.clone()
            product.amount = OUTPUT_AMOUNT[validity]!![quality]
            Pair(true, product)
        } else {
            val product = items.getItemStack("布匹")!!.clone()
            product.amount = OUTPUT_AMOUNT[validity]!![quality]
            Pair(true, product)
        }
    }
}
