package moe.gensoukyo.gui.pages

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerOpenScreenEvent
import me.wuxie.wakeshow.wakeshow.api.event.PlayerPostClickComponentEvent
import me.wuxie.wakeshow.wakeshow.ui.Container
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen
import me.wuxie.wakeshow.wakeshow.ui.WxScreen
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.pages.collection.MoonCakeCollection
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.getDataContainer
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray


class CollectionMainPage : Page {
    private val guiTestPos = Pos(-1, -85, 512, 512, 0, 0)
    private val x0 = 175
    private val y0 = 240
    private val imageRoot = "location:mcgproject:textures/gui"
    private val backgroundImage = "$imageRoot/collection_mobs.png"
    private val buttonTageMoonCakeImage = "$imageRoot/collection_tag_mooncake"
    private val buttonLastPageImage = "$imageRoot/collection_lastpage"
    private val buttonNextPageImage = "$imageRoot/collection_nextpage"

    private val gui = WInventoryScreen(
        "collection_mobs",
        backgroundImage,
        guiTestPos.dx,
        guiTestPos.dy,
        guiTestPos.w,
        guiTestPos.h,
        x0,
        y0 + 18 * 6 + 12
    )

    override fun getPage(): WxScreen {
        for (l in 0..5)
            for (i in 0..8) {
                val slotName = "slot$l-$i"
                val x = x0 + 18 * i
                val y = y0 - 6 + 18 * l
                createEmptySlot(gui.container, slotName, x, y)
            }
        createTagButton(gui.container, "button_tage_moon_cake", buttonTageMoonCakeImage).apply {
            w = 64
            h = 27
            x = x0 - w - 34
            y = y0 - h + 2
            gui.container.add(this)
        }

        createButton(gui.container, "button_last_page", buttonLastPageImage).let {
            it.w = 30
            it.h = 54
            it.x = x0 - it.w - 42
            it.y = y0 + 18 * 5
            gui.container.add(it)
        }


        createButton(gui.container, "button_next_page", buttonNextPageImage).let {
            it.w = 30
            it.h = 54
            it.x = x0 + 18 * 9 + 40
            it.y = y0 + 18 * 5
            gui.container.add(it)
        }
        return gui
    }

    private fun createEmptySlot(container: Container, name: String, x: Int, y: Int) {
        WSlot(
            container, name, ItemStack(Material.AIR), x, y
        ).apply {
            isCanDrag = true
            container.add(this)
        }
    }

    private fun createTagButton(container: Container, id: String, imagePath: String): WButton {
        return WButton(
            container,
            id,
            "",
            "${imagePath}_1.png",
            "${imagePath}_1.png",
            "${imagePath}_2.png",
            0,
            0
        ).apply {
            setFunction { _, player ->
                WuxieAPI.closeGui(player)
                WuxieAPI.openGui(player, MoonCakeCollection().getPage())
            }
        }
    }

    private fun createButton(container: Container, id: String, imagePath: String): WButton {
        return WButton(container, id, "", "${imagePath}_1.png", "${imagePath}_2.png", "${imagePath}_3.png", 0, 0)
    }

    companion object {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun open(e: PlayerOpenScreenEvent) {
            if (e.screen.id != "collection_mobs") return

            val slots = e.player.getDataContainer()["collection_mobs"].run {
                Gson().fromJson<Map<String, String>>(
                    this, object : TypeToken<Map<String, String>>() {}.type
                )
            }
            e.screen.container.componentMap.filter {
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
            WuxieAPI.updateGui(e.player)
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun close(e: PlayerCloseScreenEvent) {
            if (e.screen.id != "collection_mobs") return

            println("${e.player.name}关闭收集图册，保存物品中")
            e.screen.container.componentMap.filter {
                it.key.startsWith("slot")
            }.mapValues {
                (it.value as WSlot).itemStack
            }.filterNot {
                it.value.isAir()
            }.mapValues {
                it.value.serializeToByteArray(true).contentToString().replace(" ", "")
            }.let {
                e.player.getDataContainer()["collection_mobs"] = Gson().toJson(it)
            }
        }
        @SubscribeEvent(EventPriority.HIGHEST)
        fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
            if (e.screen.id != "collection_mobs") return
            if (!e.component.id.startsWith("slot")) return

            val slot = e.component as WSlot
            if (slot.itemStack.isAir()) return

            if(slot.itemStack.type.name != "ARMOURERS_WORKSHOP_ITEMSKIN")
                return giveBackItem(slot, e.player)
        }

        private fun giveBackItem(slot: WSlot, player: Player) {
            player.sendMessage("这不是时装！")
            player.giveItem(slot.itemStack)
            slot.itemStack = ItemStack(Material.AIR)
            WuxieAPI.updateGui(player)
        }
    }
}