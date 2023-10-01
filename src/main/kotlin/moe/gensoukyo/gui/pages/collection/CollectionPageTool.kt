package moe.gensoukyo.gui.pages.collection

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.pages.PageTools
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.expansion.getDataContainer
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray

object CollectionPageTool : PageTools {
    private val idToPage = linkedMapOf(
        "collection_mobs" to CollectionMainPage(),
        "collection_mooncake" to MoonCakeCollection(),
        "collection_akyuu" to AkyuuCollection()
    )

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

        val slots = player.getDataContainer()[gui.id].run {
            Gson().fromJson<Map<String, String>>(
                this, object : TypeToken<Map<String, String>>() {}.type
            )
        }
        gui.container.componentMap.filter {
            it.key.startsWith("slot")
        }.map {
            (it.value as WSlot)
        }.forEach {
            it.itemStack = slots[it.id]?.run {
                val byteValues = this.substring(1, this.length - 1).split(",")
                val bytes = ByteArray(byteValues.size)

                for ((index, byteValue) in byteValues.withIndex()) {
                    bytes[index] = byteValue.trim().toByte()
                }
                bytes.deserializeToItemStack(true)
            } ?: ItemStack(Material.AIR)
        }
    }

    private const val x0 = 175
    private const val y0 = 240
    private const val imageRoot = "location:mcgproject:textures/gui"
    private const val buttonTageMoonCakeImage = "$imageRoot/collection_tag_mooncake"
    private const val buttonTageAkyuuImage = "$imageRoot/collection_tag_akyuu"
    private const val buttonTageMobsImage = "$imageRoot/collection_tag_mobs"
    private const val buttonLastPageImage = "$imageRoot/collection_lastpage"
    private const val buttonNextPageImage = "$imageRoot/collection_nextpage"

    fun addTageAndButton(gui: WxScreen) {
        WButton(
            gui.container,
            "button_tage_moon_cake",
            "§6§l月饼",
            "${buttonTageMoonCakeImage}_1.png",
            "${buttonTageMoonCakeImage}_2.png",
            "${buttonTageMoonCakeImage}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = x0 - w - 34
            y = y0 - h + 2
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
            "${buttonTageMobsImage}_1.png",
            "${buttonTageMobsImage}_2.png",
            "${buttonTageMobsImage}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = x0 + 18 * 9 + 32
            y = y0 - h + 2 + 27
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
            "${buttonTageAkyuuImage}_1.png",
            "${buttonTageAkyuuImage}_2.png",
            "${buttonTageAkyuuImage}_2.png",
            0,
            0
        ).apply {
            w = 64
            h = 27
            x = x0 - w - 34
            y = y0 - h + 2 + 27
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
            "${buttonLastPageImage}_1.png",
            "${buttonLastPageImage}_2.png",
            "${buttonLastPageImage}_3.png",
            0,
            0
        ).apply {
            w = 30
            h = 54
            x = x0 - this.w - 42
            y = y0 + 18 * 5
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
            "${buttonNextPageImage}_1.png",
            "${buttonNextPageImage}_2.png",
            "${buttonNextPageImage}_3.png",
            0,
            0
        ).apply {
            this.w = 30
            this.h = 54
            this.x = x0 + 18 * 9 + 40
            this.y = y0 + 18 * 5
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