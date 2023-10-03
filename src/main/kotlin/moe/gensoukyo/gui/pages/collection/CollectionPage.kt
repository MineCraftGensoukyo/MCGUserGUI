package moe.gensoukyo.gui.pages.collection

import moe.gensoukyo.gui.pages.Page
import org.bukkit.inventory.ItemStack

interface CollectionPage : Page {
    fun getPageID(): String
    fun getLastPage(): String
    fun getNextPage(): String
    fun checkItemLegal(item: ItemStack): Boolean

    val onlyAllowHaveSingleStack: Boolean
    val needsLore: Array<String>
    val unLegalNotice: String
}