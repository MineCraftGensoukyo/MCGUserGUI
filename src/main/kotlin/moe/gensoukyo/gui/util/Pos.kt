package moe.gensoukyo.gui.util

import moe.gensoukyo.gui.pages.TestPage

data class Pos(val x: Int, val y: Int, val w: Int, val h: Int , val deviation_x: Int ,val deviation_y: Int) {
    val dx = x + deviation_x
    val dy = y + deviation_y
}
