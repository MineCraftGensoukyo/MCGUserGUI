package moe.gensoukyo.gui.pages

import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen

class WxAlchemyScreen(id: String?, background: String?, x: Int, y: Int, w: Int, h: Int, slotLeft: Int, slotTop: Int) :
    WInventoryScreen(
        id, background, x, y,
        w,
        h,
        slotLeft,
        slotTop
    ) {
    var isSuccess: Boolean = false
}