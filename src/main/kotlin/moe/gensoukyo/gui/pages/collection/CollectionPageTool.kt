package moe.gensoukyo.gui.pages.collection

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
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
import taboolib.common.platform.function.warning
import taboolib.expansion.getDataContainer
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray

object CollectionPageTool : PageTools {
    private val idToPage = linkedMapOf(
        "collection_mobs" to CollectionMainPage(),
        "collection_mooncake" to MoonCakeCollection(),
        "collection_akyuu" to AkyuuCollection()
    )

    @SubscribeEvent(EventPriority.HIGHEST)
    fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
        val page = idToPage[e.screen.id] ?: return
        if (!e.component.id.startsWith("slot")) return
        val slot = e.component as WSlot
        if (slot.itemStack.isAir()) return
        if (!page.checkItemLegal(slot.itemStack)) return giveBackItem(slot, e.player, page.unLegalNotice)
        if (page.needsLore.isNotEmpty()) {
            val lore = slot.itemStack.itemMeta?.lore ?: return giveBackItem(slot, e.player, page.unLegalNotice)
            page.needsLore.forEach { need ->
                lore.find { it.contains(need) } ?: return giveBackItem(slot, e.player, page.unLegalNotice)
            }
        }
        if (!page.onlyAllowHaveSingleStack)
            return

        e.screen.container.componentMap.filter {
            it.key.startsWith("slot") && it.key != e.component.id
        }.map {
            (it.value as WSlot).itemStack
        }.filterNot {
            it.isAir()
        }.find {
            it.itemMeta?.displayName == slot.itemStack.itemMeta?.displayName
        }.run {
            if (this != null) {
                e.player.sendMessage("不能放两打！")
                e.player.giveItem(slot.itemStack)
                slot.itemStack = ItemStack(Material.AIR)
                WuxieAPI.updateGui(e.player)
            }
        }
    }

    private fun giveBackItem(slot: WSlot, player: Player, notice: String) {
        player.sendMessage(notice)
        player.giveItem(slot.itemStack)
        slot.itemStack = ItemStack(Material.AIR)
        WuxieAPI.updateGui(player)
    }

    override fun giveBackItems(pl: Player, gui: WxScreen) {
        gui.container.componentMap.filter {
            it.key.startsWith("slot")
        }.mapValues {
            (it.value as WSlot).itemStack
        }.filterNot {
            it.value.isAir()
        }.mapValues {
            it.value.serializeToByteArray(true).contentToString().replace(" ", "")
        }.let {
            pl.getDataContainer()[gui.id] = Gson().toJson(it)
        }
    }

    override fun guiPrepare(player: Player, gui: WxScreen) {
        gui.cursor = null
        addTageAndButton(gui)
        val slots = player.getDataContainer()[gui.id]?.run {
            Gson().fromJson<Map<String, String>>(
                this, object : TypeToken<Map<String, String>>() {}.type
            )
        }?.mapValues {
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
            it.itemStack = slots?.get(it.id)?.run {
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
            "button_tage_moon_cake",
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
            setFunction { _, player ->
                if (WuxieAPI.getOpenedGui(player).screen.id == "collection_mooncake")
                    return@setFunction
                WuxieAPI.closeGui(player)
                MoonCakeCollection().showCachePage(player)
            }
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
            setFunction { _, player ->
                if (WuxieAPI.getOpenedGui(player).screen.id == "collection_mobs")
                    return@setFunction
                WuxieAPI.closeGui(player)
                CollectionMainPage().showCachePage(player)
            }
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
            setFunction { _, player ->
                if (WuxieAPI.getOpenedGui(player).screen.id == "collection_akyuu")
                    return@setFunction
                WuxieAPI.closeGui(player)
                AkyuuCollection().showCachePage(player)
            }
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
            setFunction { _, player ->
                val openedGuiID = WuxieAPI.getOpenedGui(player).screen.id
                val openedGui = idToPage[openedGuiID]
                if (openedGui == null) {
                    severe("非收藏品UI $openedGuiID 可以点到收藏品的UI界面")
                    return@setFunction
                }
                WuxieAPI.closeGui(player)
                val lastPage =
                    idToPage[openedGui.getNextPage()] ?: return@setFunction warning("未找到相对页面")
                lastPage.showCachePage(player)
            }
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
            setFunction { _, player ->
                val openedGuiID = WuxieAPI.getOpenedGui(player).screen.id
                val openedGui = idToPage[openedGuiID]
                if (openedGui == null) {
                    severe("非收藏品UI $openedGuiID 可以点到收藏品的UI界面")
                    return@setFunction
                }
                WuxieAPI.closeGui(player)
                val nextPage =
                    idToPage[openedGui.getNextPage()] ?: return@setFunction warning("未找到相对页面")
                nextPage.showCachePage(player)
            }
        }
    }
}