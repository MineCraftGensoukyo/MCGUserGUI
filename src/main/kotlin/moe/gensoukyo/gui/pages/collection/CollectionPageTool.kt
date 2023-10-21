package moe.gensoukyo.gui.pages.collection

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPreClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.pages.PageTools
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.severe
    import taboolib.expansion.getDataContainer
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray
import java.util.*

object CollectionPageTool : PageTools {
    val tempData = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
    private val playerPageList = mutableMapOf<UUID, LinkedList<String>>()
    val idToPage = linkedMapOf(
        "collection_mobs" to MobsCollection(),
        "collection_mooncake" to MoonCakeCollection(),
        "collection_akyuu" to AkyuuCollection()
    )

    private fun CollectionPage.nextPage(uuid: UUID): String? {
        val pages = playerPageList[uuid] ?: return null
        val iterator = pages.iterator()
        while (iterator.hasNext()) {
            if (iterator.next() == this.getPageID()) {
                return if (iterator.hasNext()) iterator.next() else pages.first
            }
        }
        return null
    }

    private fun CollectionPage.lastPage(uuid: UUID): String? {
        val pages = playerPageList[uuid] ?: return null
        val iterator = pages.iterator()
        var lastPage: String? = null
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == this.getPageID())
                return lastPage ?: pages.last

            lastPage = next
        }
        return null
    }

    @SubscribeEvent(EventPriority.HIGHEST)
    fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
        val openedGui = idToPage[e.screen.id] ?: return
        val targetPage = when {
            e.component.id == "button_last_page" -> idToPage[openedGui.lastPage(e.player.uniqueId)]
            e.component.id == "button_next_page" -> idToPage[openedGui.nextPage(e.player.uniqueId)]
            e.component.id.startsWith("button_tage_") -> {
                val name = e.component.id.replace("button_tage_", "collection_")
                if (e.screen.id == name) return
                idToPage[name]
            }

            e.component.id.startsWith("slot") -> {
                val slot = e.component as WSlot
                if (e.mouseButton == 1 && !slot.itemStack.isAir() && !e.screen.cursor.isAir()) {
                    e.player.giveItem(slot.itemStack)
                    slot.itemStack = ItemStack(Material.AIR)
                    WuxieAPI.updateGui(e.player)
                    tempData[e.player.uniqueId.toString()]?.get(openedGui.getPageID())?.remove(slot.id)
                    return
                }

                if (slot.itemStack.isAir()) {
                    tempData[e.player.uniqueId.toString()]?.get(openedGui.getPageID())?.remove(slot.id)
                    return
                }

                val itemStackString = slot.itemStack.serializeToByteArray(true).contentToString().replace(" ", "")
                tempData.getOrPut(e.player.uniqueId.toString()) { mutableMapOf() }
                    .getOrPut(openedGui.getPageID()) { mutableMapOf() }[slot.id] = itemStackString
                return
            }

            else -> return
        } ?: return

        if (targetPage.getPageID() != e.screen.id) {
            if (!e.screen.cursor.isAir()) {
                e.player.giveItem(e.screen.cursor)
                e.screen.cursor = null
            }
            tempData[e.player.uniqueId.toString()]?.forEach {
                e.player.getDataContainer()[it.key] = Gson().toJson(it.value)
            }
            tempData.remove(e.player.uniqueId.toString())
            targetPage.showCachePage(e.player)
        }
    }

    @SubscribeEvent(EventPriority.HIGHEST)
    fun playerPreClickComponentEventListener(e: PlayerPreClickComponentEvent) {
        if (!e.component.id.startsWith("slot")) return
        val page = idToPage[e.screen.id] ?: return severe("非收藏品UI ${e.screen.id} 可以点到收藏品的UI界面")

        val itemStack = e.screen.cursor
        if (itemStack == null || itemStack.isAir)
            return
        if (!page.checkItemLegal(itemStack)) return cancelledClickSlot(e, e.player, page.unLegalNotice)
        if (page.needsLore.isNotEmpty()) {
            val lore = itemStack.itemMeta?.lore ?: return cancelledClickSlot(e, e.player, page.unLegalNotice)
            page.needsLore.forEach { need ->
                lore.find { it.contains(need) } ?: return cancelledClickSlot(e, e.player, page.unLegalNotice)
            }
        }
        if (page.onlyAllowHaveSingleStack) e.screen.container.componentMap.filter {
            it.key.startsWith("slot") && it.key != e.component.id
        }.map {
            (it.value as WSlot).itemStack
        }.filterNot {
            it.isAir()
        }.find {
            it.itemMeta?.displayName == itemStack.itemMeta?.displayName
        }.run {
            if (this != null) return cancelledClickSlot(e, e.player, "不能放两打！")
        }
    }

    private fun cancelledClickSlot(e: PlayerPreClickComponentEvent, player: Player, notice: String) {
        e.isCancelled = true
        player.sendMessage(notice)
    }

    override fun giveBackItems(pl: Player, gui: WxScreen) {
        gui.cursor = null
        tempData[pl.uniqueId.toString()]?.forEach {
            pl.getDataContainer()[it.key] = Gson().toJson(it.value)
        }
        tempData.remove(pl.uniqueId.toString())
        playerPageList.remove(pl.uniqueId)
    }

    override fun guiPrepare(player: Player, gui: WxScreen) {
        if (playerPageList[player.uniqueId] == null)
            idToPage.keys.filter {
                player.hasPermission("mcggui.page.$it")
            }.let {
                playerPageList[player.uniqueId] = LinkedList<String>().apply {
                    addAll(it)
                }
            }
        addTageAndButton(gui, playerPageList[player.uniqueId]!!)
        val slots = tempData.getOrPut(player.uniqueId.toString()) {
            mutableMapOf()
        }.getOrPut(gui.id) {
            player.getDataContainer()[gui.id]?.run {
                Gson().fromJson<Map<String, String>>(
                    this, object : TypeToken<Map<String, String>>() {}.type
                )
            }?.toMutableMap() ?: mutableMapOf()
        }.mapValues {
            val byteValues = it.value.substring(1, it.value.length - 1).split(",")
            val bytes = ByteArray(byteValues.size)

            for ((index, byteValue) in byteValues.withIndex()) {
                bytes[index] = byteValue.trim().toByte()
            }
            bytes
        }

        gui.container.componentMap.filter {
            it.key.startsWith("slot")
        }.map {
            (it.value as WSlot)
        }.filter {
            it.itemStack.isAir()
        }.forEach {
            it.itemStack = slots[it.id]?.run {
                this.deserializeToItemStack(true)
            } ?: ItemStack(Material.AIR)
        }
    }

    private const val X0 = 175
    private const val Y0 = 240
    private const val IMAGE_ROOT = "location:mcgproject:textures/gui"
    private const val BUTTON_LAST_PAGE_IMAGE = "$IMAGE_ROOT/collection_lastpage"
    private const val BUTTON_NEXT_PAGE_IMAGE = "$IMAGE_ROOT/collection_nextpage"

    private fun addTageAndButton(gui: WxScreen, pages: LinkedList<String>) {
        pages.forEach { id ->
            idToPage[id]?.getLabel(gui.container)
        }
        WButton(
            gui.container,
            "button_last_page",
            "",
            "${BUTTON_LAST_PAGE_IMAGE}_1.png",
            "${BUTTON_LAST_PAGE_IMAGE}_2.png",
            "${BUTTON_LAST_PAGE_IMAGE}_3.png",
            0,
            0
        ).apply {
            w = 30
            h = 54
            x = X0 - this.w - 42
            y = Y0 + 18 * 5
            gui.container.add(this)
        }

        WButton(
            gui.container,
            "button_next_page",
            "",
            "${BUTTON_NEXT_PAGE_IMAGE}_1.png",
            "${BUTTON_NEXT_PAGE_IMAGE}_2.png",
            "${BUTTON_NEXT_PAGE_IMAGE}_3.png",
            0,
            0
        ).apply {
            this.w = 30
            this.h = 54
            this.x = X0 + 18 * 9 + 40
            this.y = Y0 + 18 * 5
            gui.container.add(this)
        }
    }
}