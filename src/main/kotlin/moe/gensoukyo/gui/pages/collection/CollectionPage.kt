package moe.gensoukyo.gui.pages.collection

import me.wuxie.wakeshow.wakeshow.ui.Container
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WSubScreen
import me.wuxie.wakeshow.wakeshow.ui.inventory.InvSlotProxyScreen

fun interface CollectionPage {
    fun getPage(): WInventoryScreen

}