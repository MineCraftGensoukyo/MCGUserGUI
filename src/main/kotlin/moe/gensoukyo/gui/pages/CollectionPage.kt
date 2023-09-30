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
import moe.gensoukyo.gui.util.Pos
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.expansion.getDataContainer
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir


class CollectionPage : Page {
    private val imageRoot = "location:mcgproject:textures/gui"
    private val backgroundImage = "$imageRoot/collection_mooncake3.png"
    private val buttonLastPageImage = "$imageRoot/collection_lastpage"
    private val buttonNextPageImage = "$imageRoot/collection_nextpage"
    private val buttonTageMoonCakeImage = "$imageRoot/collection_tag_mooncake"


    private val guiTestPos = Pos(-1, -91, 512, 512, 0, 0)
    private val x0 = 175
    private val y0 = 240


    private val gui = WInventoryScreen(
        "collection_mooncake",
        backgroundImage,
        guiTestPos.dx,
        guiTestPos.dy,
        guiTestPos.w,
        guiTestPos.h,
        x0,
        y0 + 18 * 6 + 18
    )

    override fun getPage(): WxScreen {
        for (l in 0..5)
            for (i in 0..8) {
                val slotName = "slot$l-$i"
                val x = x0 + 18 * i
                val y = y0 + 1 + 18 * l
                createEmptySlot(gui.container, slotName, x, y)
            }

        for (l in 0..2)
            for (i in 0..5) {
                val slotName = "slot${l}e-$i"
                val x = x0 + 18 * 3 + 18 * i
                val y = y0 - 18 * 3 + 18 * l
                createEmptySlot(gui.container, slotName, x, y)
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

        createTagButton(gui.container, "button_tage_moon_cake", buttonTageMoonCakeImage).apply {
            w = 64
            h = 27
            x = x0 - w - 34
            y = y0 - h + 2
            gui.container.add(this)
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

    private fun createButton(container: Container, id: String, imagePath: String): WButton {
        return WButton(container, id, "", "${imagePath}_1.png", "${imagePath}_2.png", "${imagePath}_3.png", 0, 0)
    }

    private fun createTagButton(container: Container, id: String, imagePath: String): WButton {
        return WButton(container, id, "", "${imagePath}_1.png", "${imagePath}_1.png", "${imagePath}_2.png", 0, 0)
    }


    companion object {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun open(e: PlayerOpenScreenEvent) {
            if (e.screen.id != "collection_mooncake") return

            val slots = e.player.getDataContainer()["collection_mooncake"].run {
                Gson().fromJson<Map<String, Map<String, Any>>>(
                    this, object : TypeToken<Map<String, Map<String, Any>>>() {}.type
                )
            }
            e.screen.container.componentMap.filter {
                it.key.startsWith("slot")
            }.map {
                (it.value as WSlot)
            }.forEach {
                it.itemStack = slots[it.id].run {
                    this?.let { it1 -> ItemStack.deserialize(it1) }
                } ?: ItemStack(Material.AIR)
            }
            WuxieAPI.updateGui(e.player)
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun close(e: PlayerCloseScreenEvent) {
            if (e.screen.id != "collection_mooncake") return

            println("${e.player.name}关闭收集图册，保存物品中")
            e.screen.container.componentMap.filter {
                it.key.startsWith("slot")
            }.mapValues {
                (it.value as WSlot).itemStack
            }.filterNot {
                it.value.isAir()
            }.mapValues {
                it.value.serialize()
            }.let {
                e.player.getDataContainer()["collection_mooncake"] = Gson().toJson(it)
            }
        }

        @SubscribeEvent(EventPriority.HIGHEST)
        fun playerPostClickComponentEventListener(e: PlayerPostClickComponentEvent) {
            if (e.screen.id != "collection_mooncake") return
            if (!e.component.id.startsWith("slot")) return

            val slot = e.component as WSlot
            if (slot.itemStack.isAir()) return

            val lore = slot.itemStack.itemMeta?.lore ?: return giveBackItem(slot, e.player)
            lore.find { it.contains("[食材]") } ?: return giveBackItem(slot, e.player)
            lore.find { it.contains("2023中秋节") } ?: return giveBackItem(slot, e.player)

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

        private fun giveBackItem(slot: WSlot, player: Player) {
            player.sendMessage("这不是月饼！")
            player.giveItem(slot.itemStack)
            slot.itemStack = ItemStack(Material.AIR)
            WuxieAPI.updateGui(player)
        }
    }
}