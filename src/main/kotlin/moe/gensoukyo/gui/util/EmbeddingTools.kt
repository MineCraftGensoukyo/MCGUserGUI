package moe.gensoukyo.gui.util

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI
import me.wuxie.wakeshow.wakeshow.ui.Container
import me.wuxie.wakeshow.wakeshow.ui.component.WButton
import me.wuxie.wakeshow.wakeshow.ui.component.WCheckBox
import me.wuxie.wakeshow.wakeshow.ui.component.WScrollingContainer
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList
import moe.gensoukyo.gui.config.MainConfig.armor_types
import moe.gensoukyo.gui.config.MainConfig.conf
import moe.gensoukyo.gui.config.MainConfig.items
import moe.gensoukyo.gui.config.MainConfig.weapon_types
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors

object EmbeddingTools {
    //以下为镶嵌标识符
    //以下为镶嵌石内的lore
    //镶嵌石LORE中表示镶嵌石品阶的记号
    private const val LEVEL_MARKER = "§d品阶 ·"
    private const val STONE_START = "§f§l打造部件: "
    private const val STONE_TYPE_MARKER = "§f» §7部位: "
    private const val STONE_TEXT = "§f» §7类型: §f镶嵌石"
    private const val EMBEDDING_ATTRIBUTE_MARKER = "§f» §7效果:"

    //镶嵌石对应部位标识
    private const val WILD_CARD = "通用"
    private const val WEAPON = "武器"
    private const val ARMOR = "防具"
    private val TYPES = hashMapOf(WEAPON to weapon_types, ARMOR to armor_types)

    //以下为装备内的lore
    private const val EMPTY_SLOT = "§8○ 空部件"
    private const val USED_SLOT = "§a● "
    private const val ISOLATION = "§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m " +
            "§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m"
    private const val ITEM_TYPE_MARK = "§a§l！ §c装备类型: "
    private const val ATTRIBUTE_MARKER = "§7*"

    //武器LORE中表示镶嵌石品阶的记号
    private const val STONE_LEVEL_MARKER_IN_ITEM = " §d品阶 §f: "
    fun romanNumeralToInt(romanNumeral: String?): Int {
        return when (romanNumeral) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            "V" -> 5
            else -> -1
        }
    }

    fun intToRomanNumeral(num: Int): String {
        return when (num) {
            1 -> "I"
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            5 -> "V"
            else -> "ERROR"
        }
    }

    fun getLevel(lore: String?): Int {
        if (lore == null) return -1
        val p = Pattern.compile("\\d+")
        val m = p.matcher(lore)
        while (m.find()) {
            return m.group().toInt()
        }
        return -1
    }

    fun getPureString(name: String): String {
        val tmp = stringWithoutColor(name) ?: return ""
        return tmp.trim { it <= ' ' }
    }

    fun getStringWithouHead(str: String, head: String): String {
        val index = str.indexOf(head) + head.length
        return str.substring(index)
    }

    fun stringWithoutColor(str: String): String? {
        return ChatColor.stripColor(str)
    }

    fun createPrimordialStone(level: Int, amount: Int): ItemStack {
        val stone = items.getItemStack("精炼原石")!!
        stone.amount = amount
        var itemName = stone.itemMeta!!.displayName
        itemName += "${level}"
        val itemMeta = stone.itemMeta!!.clone()
        itemMeta.setDisplayName(itemName)
        stone.itemMeta = itemMeta

        return stone
    }

    fun stoneSlotCheck(player: Player, stone: ItemStack?, button: WButton, stoneTipsText: WTextList) {
        //点击镶嵌石物品槽时调用
        button.h = 0
        stoneTipsText.content = ArrayList()
        if (stone != null) {
            val tips = embeddingStoneCheck(stone)
            if (tips.isEmpty()) {
                button.h = 25
            } else {
                stoneTipsText.content = listOf(tips)
            }
        }
        WuxieAPI.updateGui(player)
    }

    fun equipmentSlotCheck(player: Player, equipment: ItemStack?, button: WButton, equipmentTipsText: WTextList) {
        //点击装备物品槽时调用
        button.w = 0
        equipmentTipsText.content = ArrayList()
        if (equipment != null) {
            val tips = embeddingEquipmentCheck(equipment)
            if (tips.isEmpty()) {
                button.w = 25
            } else {
                equipmentTipsText.content = listOf(tips)
            }
        }
        WuxieAPI.updateGui(player)
    }

    fun embeddingApprovalCheck(equipment: ItemStack?, stone: ItemStack?): String {
        if (equipment == null || equipment.itemMeta?.lore == null) {
            return "§c该物品无法被镶嵌"
        }
        if (stone == null || stone.itemMeta?.lore == null) {
            return "§c该镶嵌石不可镶嵌于该装备上"
        }
        val stoneLore = stone.itemMeta!!.lore!!
        val embedded = HashSet<String>()
        val itemLore = equipment.itemMeta!!.lore!!
        var itemType = ""
        var itemLevel = -1
        var stoneLevel = -1
        for (lore in itemLore) {
            if (lore.contains(USED_SLOT)) {
                val embeddedName = getPureString(getStringWithouHead(lore, USED_SLOT))
                embedded.add(embeddedName)
            }
            if (lore.contains(LEVEL_MARKER)) {
                itemLevel = getLevel(stringWithoutColor(lore))
            }
            if (lore.contains(ITEM_TYPE_MARK)) {
                for (type1 in TYPES.keys) {
                    if (!itemType.isEmpty()) break
                    if (lore.contains(type1)) {
                        itemType = type1
                        break
                    }
                    for (type2 in TYPES[type1]!!) {
                        if (lore.contains(type2)) {
                            itemType = type2
                            break
                        }
                    }
                }
            }
        }
        for (lore in stoneLore) {
            if (lore.contains(LEVEL_MARKER)) {
                stoneLevel = getLevel(stringWithoutColor(lore))
            }
        }
        if (itemType.isEmpty() || itemLevel == -1) {
            return "§c该物品无法被镶嵌"
        }
        if (!typeCheck(itemType, stoneLore) || stoneLevel == -1) {
            return "§c该镶嵌石不可镶嵌于该装备上"
        }
        if (itemLevel > stoneLevel) {
            return "§c镶嵌石品阶与装备不符"
        }
        val stoneName = getPureString(
            getStringWithouHead(
                stone.itemMeta!!.displayName, STONE_START
            )
        )
        return if (embedded.contains(stoneName)) {
            "§c不可在装备上镶嵌同种镶嵌石"
        } else ""
    }

    private fun embeddingStoneCheck(stone: ItemStack): String {
        if (stone.itemMeta?.lore == null) {
            return "§c请放上镶嵌石"
        }
        val stoneLore = stone.itemMeta!!.lore
        var isStone = false
        for (lore in stoneLore!!) {
            if (lore.contains(STONE_TEXT)) {
                isStone = true
                break
            }
        }
        return if (!isStone) {
            "§c请放上镶嵌石"
        } else ""
    }

    private fun embeddingEquipmentCheck(equipment: ItemStack): String {
        if (equipment.itemMeta?.lore == null) {
            return "§c请放上装备"
        }
        var canEmbedding = false
        val itemLore = equipment.itemMeta!!.lore
        for (lore in itemLore!!) {
            if (lore.contains(EMPTY_SLOT)) {
                canEmbedding = true
                break
            }
        }
        return if (!canEmbedding) {
            "§c该装备没有空余的镶嵌槽"
        } else ""
    }

    private fun typeCheck(equipmentType: String, stoneLore: List<String>): Boolean {
        var type = ""
        for (lore in stoneLore) {
            if (lore.contains(STONE_TYPE_MARKER)) {
                val index = lore.indexOf(STONE_TYPE_MARKER) + STONE_TYPE_MARKER.length
                type = getPureString(lore.substring(index))
                break
            }
        }
        if (type == WILD_CARD) return true
        if (type == equipmentType) return true
        for (type1 in TYPES.keys) {
            if (type1 == type) {
                return TYPES[type]!!.contains(equipmentType)
            }
        }
        return false
    }

    fun embedding(equipment: ItemStack, stone: ItemStack): ItemStack {
        val newLoreList: MutableList<String> = ArrayList()
        val newAttribute: MutableList<String> = ArrayList()
        val itemLore = equipment.itemMeta!!.lore
        val stoneLore = stone.itemMeta!!.lore
        val stoneName = getStringWithouHead(stone.itemMeta!!.displayName, STONE_START).trim { it <= ' ' }
        newAttribute.add(0, USED_SLOT + stoneName)
        for (lore in stoneLore!!) {
            if (lore.contains(LEVEL_MARKER)) {
                val stoneLevel = StringBuilder("§d")
                stoneLevel.append(intToRomanNumeral(getLevel(stringWithoutColor(lore))))
                newAttribute.add(1, ATTRIBUTE_MARKER + STONE_LEVEL_MARKER_IN_ITEM + stoneLevel)
                continue
            }
            if (lore.contains(EMBEDDING_ATTRIBUTE_MARKER)) {
                val attribute = getStringWithouHead(lore, EMBEDDING_ATTRIBUTE_MARKER)
                newAttribute.add(ATTRIBUTE_MARKER + attribute)
            }
        }
        var done = false
        for (lore in itemLore!!) {
            if (lore.contains(EMPTY_SLOT) && !done) {
                newLoreList.addAll(newAttribute)
                newLoreList.add(ISOLATION)
                done = true
                continue
            }
            newLoreList.add(lore)
        }
        val newEquipment = equipment.clone()
        val newMeta = equipment.itemMeta!!.clone()
        newMeta.lore = newLoreList
        newEquipment.setItemMeta(newMeta)
        return newEquipment
    }

    //返回值为 摘除镶嵌后的LORE , 应该返回的镶嵌原石物品堆
    fun unEmbedding(itemLore: List<String>, unembeddingList: List<String>)
        : Pair<List<String>, List<ItemStack>> {
        val newLore: MutableList<String> = ArrayList()
        val stoneLevelMap = HashMap<Int, Int>()
        var unEmbedding = false
        for (lore in itemLore) {
            if (lore.contains(USED_SLOT)) {
                val stoneName = getStringWithouHead(lore, USED_SLOT).trim { it <= ' ' }
                if (unembeddingList.contains(stoneName)) {
                    newLore.add(EMPTY_SLOT)
                    unEmbedding = true
                    continue
                }
            }
            if (unEmbedding) {
                if (lore.contains(STONE_LEVEL_MARKER_IN_ITEM)) {
                    val level =
                        romanNumeralToInt(getPureString(getStringWithouHead(lore, STONE_LEVEL_MARKER_IN_ITEM)))
                    if (stoneLevelMap.containsKey(level)) {
                        stoneLevelMap[level] = stoneLevelMap[level]!! + 1
                    } else {
                        stoneLevelMap[level] = 1
                    }
                }
                if (lore.contains(ISOLATION)) {
                    unEmbedding = false
                    continue
                }
            }
            if (unEmbedding) continue
            newLore.add(lore)
        }
        val primordialStone = stoneLevelMap.keys.stream()
            .map { level: Int -> createPrimordialStone(level, stoneLevelMap[level]!!) }
            .collect(Collectors.toList())
        return Pair(newLore, primordialStone)
    }

    private fun createStoneCheckBox(stoneName: String, num: Int, scrollContainer: Container): WCheckBox {
        val VERSION = conf["imageVersion"] as String
        val CHECK_1 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Unembedding_Check_1.png"
        val CHECK_2 = "https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@${VERSION}/img/Unembedding_Check_2.png"
        val checkBox = WCheckBox(
            scrollContainer, stoneName,
            CHECK_1, CHECK_2,
            0, 15 * num, 42, 15
        )
        checkBox.name = stoneName
        checkBox.selectName = stoneName
        checkBox.offsetName = (checkBox.w / 2).toFloat()
        return checkBox
    }

    fun unEmbeddingCheck(equipment: ItemStack?, guiContainer: Container, player: Player) {
        val scroll = guiContainer.getComponent("choose_scroll") as WScrollingContainer
        val scrollContainer = scroll.container
        val tipsText = guiContainer.getComponent("tips_list") as WTextList
        val decide_button = guiContainer.getComponent("decide_button") as WButton
        scrollContainer.componentMap.keys.forEach(Consumer { stone: String? -> scrollContainer.remove(stone) })
        decide_button.isCanPress = false
        tipsText.content = ArrayList()
        try {
            val itemLore = equipment!!.itemMeta!!.lore
            var num = 0
            for (lore in itemLore!!) {
                if (lore.contains(USED_SLOT)) {
                    val stoneName = getStringWithouHead(lore, USED_SLOT).trim { it <= ' ' }
                    scrollContainer.add(createStoneCheckBox(stoneName, num, scrollContainer))
                    num++
                }
            }
            if (num == 0) {
                tipsText.content = listOf("§c无镶嵌石")
            } else {
                decide_button.isCanPress = true
            }
        } catch (e: Exception) {
            tipsText.content = listOf("§c非法输入")
        } finally {
            WuxieAPI.updateGui(player)
        }
    }

    fun inventoryPlentyFor(
        player: Player,
        itemStacks: List<ItemStack>
    ): Boolean {
        val inventory: List<ItemStack> =
            Arrays.asList(*player.inventory.contents).subList(0, 36)
        val emptySlotAmount = AtomicLong(
            inventory.stream()
                .filter { i: ItemStack? -> i == null }
                .count()
        )
        return itemStacks.stream().allMatch { itemStack: ItemStack ->
            var amount = itemStack.amount
            for (slot in inventory) {
                if (itemStack.isSimilar(slot)) {
                    amount -= slot.maxStackSize - slot.amount
                }
                if (amount <= 0) break
            }
            amount <= 0 || emptySlotAmount.getAndDecrement() > 0
        }
    }
}
