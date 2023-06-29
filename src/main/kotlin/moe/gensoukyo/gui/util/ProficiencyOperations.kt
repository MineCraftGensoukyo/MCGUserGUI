package moe.gensoukyo.gui.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir
import moe.gensoukyo.gui.config.MainConfig.items

object ProficiencyOperations {
    private val limitMap = mapOf(
        1 to 200,
        2 to 983,
        3 to 2497,
        4 to 4839,
        5 to 8084
    )

    fun changeProficiency(weapon: ItemStack, amount: Int): ItemStack {
        val newWeaponLore = mutableListOf<String>()
        val weaponLore = weapon.itemMeta!!.lore!!
        weaponLore.forEach {
            if (it.contains("熟练度")) {
                if (amount == -1) {
                    newWeaponLore.add("§f 熟练度: §b精炼")
                } else {
                    newWeaponLore.add("§f 熟练度: §b${amount}")
                }
            } else {
                newWeaponLore.add(it)
            }
        }
        val newWeapon = weapon.clone()
        val newWeaponMeta = newWeapon.itemMeta!!.clone()
        newWeaponMeta.lore = newWeaponLore
        newWeapon.itemMeta = newWeaponMeta
        return newWeapon
    }

    fun transfer(pl: Player, weaponFrom: ItemStack?, weaponTo: ItemStack?)
        : Pair<ItemStack?, ItemStack?> {
        if (weaponFrom == null || weaponFrom.isAir) {
            pl.sendMessage("§c请放入需要转出熟练度的武器！")
            return Pair(null, null)
        }
        if (weaponTo == null || weaponTo.isAir) {
            pl.sendMessage("§c请放入需要转入熟练度的武器！")
            return Pair(null, null)
        }
        val equipInfoFrom = LoreInfoTools.getEquipmentInfo(weaponFrom)
        val equipInfoTo = LoreInfoTools.getEquipmentInfo(weaponTo)
        if (equipInfoFrom["validity"] == 0) {
            pl.sendMessage("§c请放入合法武器！")
            return Pair(null, null)
        }
        if (equipInfoTo["validity"] == 0) {
            pl.sendMessage("§c请放入合法武器！")
            return Pair(null, null)
        }
        if (equipInfoFrom["proficiency"] == -1) {
            pl.sendMessage("§c转出武器熟练度已满，无法转出！")
            return Pair(null, null)
        }
        if (equipInfoTo["proficiency"] == -1) {
            pl.sendMessage("§c转入武器熟练度已满，无法转入！")
            return Pair(null, null)
        }
        if (equipInfoFrom["proficiency"] == 0) {
            pl.sendMessage("§c转出武器熟练度为0，无法转出！")
            return Pair(null, null)
        }
        val proficiencyFrom = equipInfoFrom["proficiency"]!!
        val proficiencyTo = equipInfoTo["proficiency"]!!
        val qualityTo = equipInfoTo["quality"]!!
        val maxTo = limitMap[qualityTo]!!
        val transferAmount = if (proficiencyFrom + proficiencyTo > maxTo) {
            maxTo - proficiencyTo
        } else {
            proficiencyFrom
        }
        val newProficiencyFrom = proficiencyFrom - transferAmount
        val newProficiencyTo = if (proficiencyTo + transferAmount == maxTo) {
            -1
        } else {
            proficiencyTo + transferAmount
        }
        val newWeaponFrom = changeProficiency(weaponFrom, newProficiencyFrom)
        val newWeaponTo = changeProficiency(weaponTo, newProficiencyTo)
        pl.sendMessage("§a转移成功！")
        return Pair(newWeaponFrom, newWeaponTo)
    }

    fun extract(pl: Player, weapon: ItemStack?) : Pair<ItemStack?, ItemStack?> {
        if (weapon == null || weapon.isAir) {
            pl.sendMessage("§c请放入武器！")
            return Pair(null, null)
        }
        val equipInfo = LoreInfoTools.getEquipmentInfo(weapon)
        if (equipInfo["validity"] == 0) {
            pl.sendMessage("§c请放入合法武器！")
            return Pair(null, null)
        }
        if (equipInfo["proficiency"] != -1) {
            pl.sendMessage("§c熟练度未满，无法提取！")
            return Pair(null, null)
        }
        val quality = equipInfo["quality"]!!
        val stone = items.getItemStack("精炼原石")!!.clone()
        var itemName = stone.itemMeta!!.displayName
        itemName += "${quality}"
        val itemMeta = stone.itemMeta!!.clone()
        itemMeta.setDisplayName(itemName)
        stone.itemMeta = itemMeta
        pl.sendMessage("§a提取成功！")
        val newWeapon = changeProficiency(weapon, 0)
        return Pair(newWeapon, stone)
    }

}
