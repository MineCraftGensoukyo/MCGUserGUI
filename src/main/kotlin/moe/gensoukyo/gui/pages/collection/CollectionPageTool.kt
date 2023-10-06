package moe.gensoukyo.gui.pages.collection

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.pages.PageTools
import moe.gensoukyo.lib.reflection.remove
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.expansion.getDataContainer
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray

object CollectionPageTool : PageTools {
    private val tempData = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
    val idToPage = linkedMapOf(
        "collection_mobs" to CollectionMainPage(),
        "collection_mooncake" to MoonCakeCollection(),
        "collection_akyuu" to AkyuuCollection()
    )

    @SubscribeEvent(EventPriority.HIGHEST)
    fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
        val page = idToPage[e.screen.id] ?: return
        when (e.component.id) {
            "button_last_page" -> {
                val openedGui = idToPage[e.screen.id]
                if (openedGui == null) {
                    severe("非收藏品UI ${e.screen.id} 可以点到收藏品的UI界面")
                    return
                }
                val lastPage = idToPage[openedGui.getLastPage()] ?: return warning("未找到相对页面")
                lastPage.showCachePage(e.player)
            }

            "button_next_page" -> {
                val openedGui = idToPage[e.screen.id]
                if (openedGui == null) {
                    severe("非收藏品UI ${e.screen.id} 可以点到收藏品的UI界面")
                    return
                }
                val nextPage = idToPage[openedGui.getNextPage()] ?: return warning("未找到相对页面")
                nextPage.showCachePage(e.player)
            }
        }

        if (e.component.id.startsWith("button_tage_")) {
            val name = e.component.id.replace("button_tage_", "collection_")
            if (e.screen.id == name) return
            val targetPage = idToPage[name] ?: return warning("未找到相对页面")
            targetPage.showCachePage(e.player)
        }
        if (e.component.id.startsWith("slot")) slotClick(e, page)
    }

    private fun slotClick(e: PlayerPostClickComponentEvent, page: CollectionPage) {
        val slot = e.component as WSlot
        if (slot.itemStack.isAir()) {
            info("设置${e.player.name} ${page.getPage()}-${slot.id}为空")
            tempData[e.player.uniqueId.toString()]?.get(page.getPageID())?.remove(slot.id)
            return
        }
        if (!page.checkItemLegal(slot.itemStack)) return giveBackItem(slot, e.player, page.unLegalNotice)
        if (page.needsLore.isNotEmpty()) {
            val lore = slot.itemStack.itemMeta?.lore ?: return giveBackItem(slot, e.player, page.unLegalNotice)
            page.needsLore.forEach { need ->
                lore.find { it.contains(need) } ?: return giveBackItem(slot, e.player, page.unLegalNotice)
            }
        }
        if (page.onlyAllowHaveSingleStack) e.screen.container.componentMap.filter {
            it.key.startsWith("slot") && it.key != e.component.id
        }.map {
            (it.value as WSlot).itemStack
        }.filterNot {
            it.isAir()
        }.find {
            it.itemMeta?.displayName == slot.itemStack.itemMeta?.displayName
        }.run {
            if (this != null)
                return giveBackItem(slot, e.player, "不能放两打！")
        }
        info("设置${e.player.name} ${page.getPage()}-${slot.id}为${slot.itemStack.itemMeta?.displayName}")
        val itemStackString = slot.itemStack.serializeToByteArray(true).contentToString().replace(" ", "")
        tempData.getOrPut(e.player.uniqueId.toString()) { mutableMapOf() }
            .getOrPut(page.getPageID()) { mutableMapOf() }[slot.id] = itemStackString
    }

    private fun giveBackItem(slot: WSlot, player: Player, notice: String) {
        player.sendMessage(notice)
        player.giveItem(slot.itemStack)
        slot.itemStack = ItemStack(Material.AIR)
        WuxieAPI.updateGui(player)
    }

    override fun giveBackItems(pl: Player, gui: WxScreen) {
        tempData[pl.uniqueId.toString()]?.forEach {
            pl.getDataContainer()[it.key] = Gson().toJson(it.value)
        }
        tempData.remove(pl.uniqueId.toString())
    }

    override fun guiPrepare(player: Player, gui: WxScreen) {
        gui.cursor = null
        addTageAndButton(gui)
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
        }.forEach {
            it.itemStack = slots[it.id]?.run {
                this.deserializeToItemStack(true)
            } ?: ItemStack(Material.AIR)
        }
    }

    private const val X0 = 175
    private const val Y0 = 240
    private const val IMAGE_ROOT = "location:mcgproject:textures/gui"
    private const val BUTTON_TAGE_MOON_CAKE_IMAGE = "$IMAGE_ROOT/collection_tag_mooncake"
    private const val BUTTON_TAGE_AKYUU_IMAGE = "$IMAGE_ROOT/collection_tag_akyuu"
    private const val BUTTON_TAGE_MOBS_IMAGE = "$IMAGE_ROOT/collection_tag_mobs"
    private const val BUTTON_LAST_PAGE_IMAGE = "$IMAGE_ROOT/collection_lastpage"
    private const val BUTTON_NEXT_PAGE_IMAGE = "$IMAGE_ROOT/collection_nextpage"

    private fun addTageAndButton(gui: WxScreen) {
        WButton(
            gui.container,
            "button_tage_mooncake",
            "§6§l月饼",
            "${BUTTON_TAGE_MOON_CAKE_IMAGE}_1.png",
            "${BUTTON_TAGE_MOON_CAKE_IMAGE}_2.png",
            "${BUTTON_TAGE_MOON_CAKE_IMAGE}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = X0 - w - 34
            y = Y0 - h + 2
            gui.container.add(this)
        }

        WButton(
            gui.container,
            "button_tage_mobs",
            "§c§l时装",
            "${BUTTON_TAGE_MOBS_IMAGE}_1.png",
            "${BUTTON_TAGE_MOBS_IMAGE}_2.png",
            "${BUTTON_TAGE_MOBS_IMAGE}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = X0 + 18 * 9 + 32
            y = Y0 - h + 2 + 27
            gui.container.add(this)
        }

        WButton(
            gui.container,
            "button_tage_akyuu",
            "§5§l藏品",
            "${BUTTON_TAGE_AKYUU_IMAGE}_1.png",
            "${BUTTON_TAGE_AKYUU_IMAGE}_2.png",
            "${BUTTON_TAGE_AKYUU_IMAGE}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = X0 - w - 34
            y = Y0 - h + 2 + 27
            gui.container.add(this)
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