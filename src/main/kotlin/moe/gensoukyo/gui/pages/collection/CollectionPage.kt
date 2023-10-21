package moe.gensoukyo.gui.pages.collection

import me.wuxie.wakeshow.wakeshow.ui.Container
import moe.gensoukyo.gui.pages.Page
import org.bukkit.inventory.ItemStack

interface CollectionPage : Page {
    fun getPageID(): String
    fun checkItemLegal(item: ItemStack): Boolean
    fun getLabel(container: Container)

    val onlyAllowHaveSingleStack: Boolean
    val needsLore: Array<String>
    val unLegalNotice: String
}