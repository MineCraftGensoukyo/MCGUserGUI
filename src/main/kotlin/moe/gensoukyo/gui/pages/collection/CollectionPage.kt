package moe.gensoukyo.gui.pages.collection

import moe.gensoukyo.gui.pages.Page

interface CollectionPage : Page {
    fun getPageID(): String
    fun getLastPage(): String

    fun getNextPage(): String
}