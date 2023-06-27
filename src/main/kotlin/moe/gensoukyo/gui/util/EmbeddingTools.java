package moe.gensoukyo.gui.util;

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI;
import me.wuxie.wakeshow.wakeshow.ui.Container;
import me.wuxie.wakeshow.wakeshow.ui.component.*;
import moe.gensoukyo.gui.config.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmbeddingTools {
    private static final String VERSION = (String) (MainConfig.INSTANCE.getConf().get("imageVersion"));
    private static final String CHECK_1 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_Check_1.png",VERSION);
    private static final String CHECK_2 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_Check_2.png",VERSION);

    //以下为镶嵌标识符

    //以下为镶嵌石内的lore
    //镶嵌石LORE中表示镶嵌石品阶的记号
    private final static String LEVEL_MARKER = "§d品阶 ·";
    private final static String STONE_START = "§f§l打造部件: ";
    private final static String STONE_TYPE_MARKER = "§f» §7部位: ";
    private final static String STONE_TEXT = "§f» §7类型: §f镶嵌石";
    private final static String EMBEDDING_ATTRIBUTE_MARKER = "§f» §7效果:";
    //镶嵌石对应部位标识
    private final static String WILD_CARD = "通用";
    private final static String WEAPON = "武器";
    private final static String ARMOR = "防具";
    private final static HashMap<String,Set<String>> TYPES = new HashMap<>();{
        Set<String> SWORD_SET = new HashSet<>();
        SWORD_SET.add("剑");
        SWORD_SET.add("太刀");
        TYPES.put(WEAPON,SWORD_SET);

        Set<String> ARMOR_SET = new HashSet<>();
        TYPES.put(ARMOR,ARMOR_SET);
    }
    //以下为装备内的lore
    private final static String EMPTY_SLOT = "§8○ 空部件";
    private final static String USED_SLOT = "§a● ";
    private final static String ISOLATION = "§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m " +
            "§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m";
    private final static String ITEM_TYPE_MARK = "§a§l！ §c装备类型: ";
    private final static String ATTRIBUTE_MARKER = "§7*";
    //武器LORE中表示镶嵌石品阶的记号
    private final static String STONE_LEVEL_MARKER_IN_ITEM = " §d品阶 §f: ";

    public static int romanNumeralToInt(String romanNumeral){
        switch (romanNumeral){
            case "I" : return  1;
            case "II" : return  2;
            case "III" : return  3;
            case "IV" : return  4;
            case "V" : return  5;
            default : return  -1;
        }
    }

    public static String intToRomanNumeral(int num){
        switch (num){
            case 1 : return "I";
            case 2 : return "II";
            case 3 : return "III";
            case 4 : return "IV";
            case 5 : return "V";
            default : return "ERROR";
        }
    }

    public static int getLevel(String lore) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(lore);
        while (m.find()) {
            return Integer.parseInt(m.group());
        }
        return -1;
    }

    public static String getPureString(String name){
        return stringWithoutColor(name).trim();
    }

    public static String getStringWithouHead(String str,String head) {
        int index = str.indexOf(head) + head.length();
        return str.substring(index);
    }

    public static String stringWithoutColor(String str) {
        return ChatColor.stripColor(str);
    }

    public static ItemStack creatPrimordialStone(int level, int amount){
        ItemStack itemStack = new ItemStack(Material.getMaterial("MCGPROJECT_MCG_PROP"),amount);
        itemStack.setDurability((short) 69);
        String name = "§a§l精炼原石 · 品阶%d";
        ItemMeta meta = itemStack.getItemMeta().clone();
        meta.setDisplayName(String.format(name, level));
        meta.setLore(Arrays.asList(new String[]{"§f[素材]", "§7沉淀久远气息的结晶", "§7经过加工后，可以刻入魔法"}));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static void stoneSlotCheck(Player player,ItemStack stone,WButton button,WTextList stoneTipsText){
        //点击镶嵌石物品槽时调用
        button.setH(0);
        stoneTipsText.setContent(new ArrayList<>());
        if(stone!=null){
            String tips = embeddingStoneCheck(stone);
            if (tips.isEmpty()) {
                button.setH(25);
            }
            else {
                stoneTipsText.setContent(Collections.singletonList(tips));
            }
        }
        WuxieAPI.updateGui(player);
    }

    public static void equipmentSlotCheck(Player player,ItemStack equipment,WButton button,WTextList equipmentTipsText){
        //点击装备物品槽时调用
        button.setW(0);

        equipmentTipsText.setContent(new ArrayList<>());
        if(equipment!=null){
            String tips = embeddingEquipmentCheck(equipment);
            if (tips.isEmpty()) {
                button.setW(25);
            }
            else {
                equipmentTipsText.setContent(Collections.singletonList(tips));
            }
        }
        WuxieAPI.updateGui(player);
    }

    public static String embeddingApprovalCheck(ItemStack equipment,ItemStack stone){
        List<String> stoneLore = stone.getItemMeta().getLore();
        HashSet<String> embedded = new HashSet<>();
        List<String> itemLore = equipment.getItemMeta().getLore();
        String itemType = "";
        int itemLevel = -1;
        int stoneLevel = -1;

        for(String lore : itemLore) {
            if(lore.contains(USED_SLOT)) {
                String embeddedName = getPureString(getStringWithouHead(lore, USED_SLOT));
                embedded.add(embeddedName);
            }
            if(lore.contains(LEVEL_MARKER)) {
                itemLevel = getLevel(stringWithoutColor(lore));
            }
            if(lore.contains(ITEM_TYPE_MARK)) {
                for(String type1 : TYPES.keySet()){
                    if(!itemType.isEmpty()) break;

                    if(lore.contains(type1)){
                        itemType = type1;
                        break;
                    }
                    for(String type2 : TYPES.get(type1)){
                        if(lore.contains(type2)){
                            itemType = type2;
                            break;
                        }
                    }
                }
            }
        }

        for(String lore : stoneLore) {
            if(lore.contains(LEVEL_MARKER)) {
                stoneLevel = getLevel(stringWithoutColor(lore));
            }
        }

        if(itemType.isEmpty() || itemLevel == -1) {
            return "§c该物品无法被镶嵌";
        }
        if(!typeCheck(itemType, stoneLore) || stoneLevel == -1) {
            return "§c该镶嵌石不可镶嵌于该装备上";
        }
        if(itemLevel > stoneLevel) {
            return "§c镶嵌石品阶与装备不符";
        }

        String stoneName = getPureString(getStringWithouHead(stone.getItemMeta().getDisplayName(), STONE_START));
        if(embedded.contains(stoneName)) {
            return "§c不可在装备上镶嵌同种镶嵌石";
        }

        return "";
    }

    private static String embeddingStoneCheck(ItemStack stone){
        List<String> stoneLore = stone.getItemMeta().getLore();

        boolean isStone = false;
        for(String lore : stoneLore) {
            if(lore.contains(STONE_TEXT)) {
                isStone = true;
                break;
            }
        }
        if(!isStone){
            return "§c请放上镶嵌石";
        }

        return "";
    }

    private static String embeddingEquipmentCheck(ItemStack equipment) {
        boolean canEmbedding = false;
        List<String> itemLore = equipment.getItemMeta().getLore();

        for(String lore : itemLore) {
            if(lore.contains(EMPTY_SLOT)) {
                canEmbedding = true;
                break;
            }
        }

        if(!canEmbedding) {
            return "§c该装备没有空余的镶嵌槽";
        }

        return "";
    }

    private static boolean typeCheck(String equipmentType,List<String> stoneLore) {
        String type = "";
        for(String lore : stoneLore) {
            if(lore.contains(STONE_TYPE_MARKER)) {
                int index = lore.indexOf(STONE_TYPE_MARKER) + STONE_TYPE_MARKER.length();
                type = getPureString(lore.substring(index));
                break;
            }
        }

        if(type.equals(WILD_CARD)) return true;
        if(type.equals(equipmentType)) return true;

        for(String type1 : TYPES.keySet()) {
            if(type1.equals(type)){
                return type.equals(equipmentType) || TYPES.get(type).contains(equipmentType);
            }
        }

        return false;
    }

    public static ItemStack embedding(ItemStack equipment,ItemStack stone){
        List<String> newLoreList = new ArrayList<>();
        List<String> newAttribute = new ArrayList<>();
        List<String> itemLore = equipment.getItemMeta().getLore();
        List<String> stoneLore = stone.getItemMeta().getLore();

        String stoneName = getStringWithouHead(stone.getItemMeta().getDisplayName(), STONE_START).trim();
        newAttribute.add(0,USED_SLOT + stoneName);
        for(String lore : stoneLore) {
            if(lore.contains(LEVEL_MARKER)) {
                StringBuilder stoneLevel = new StringBuilder("§d");
                stoneLevel.append(intToRomanNumeral(getLevel(stringWithoutColor(lore))));
                newAttribute.add(1,ATTRIBUTE_MARKER+STONE_LEVEL_MARKER_IN_ITEM+stoneLevel);
                continue;
            }
            if(lore.contains(EMBEDDING_ATTRIBUTE_MARKER)) {
                String attribute = getStringWithouHead(lore, EMBEDDING_ATTRIBUTE_MARKER);
                newAttribute.add(ATTRIBUTE_MARKER+attribute);
            }
        }

        boolean done = false;
        for(String lore : itemLore) {
            if(lore.contains(EMPTY_SLOT) && !done) {
                newLoreList.addAll(newAttribute);
                newLoreList.add(ISOLATION);
                done = true;
                continue;
            }
            newLoreList.add(lore);
        }

        ItemStack newEquipment = equipment.clone();
        ItemMeta newMeta = equipment.getItemMeta().clone();
        newMeta.setLore(newLoreList);
        newEquipment.setItemMeta(newMeta);

        return newEquipment;
    }

    //返回值为 摘除镶嵌后的LORE , 应该返回的镶嵌原石物品堆
    public static List<List> unEmbedding(List<String> itemLore,List<String> unembeddingList){
        List<String> newLore = new ArrayList<>();
        HashMap<Integer,Integer> stoneLevelMap = new HashMap<>();
        boolean unEmbedding = false;

        for (String lore : itemLore) {
            if (lore.contains(USED_SLOT)) {
                String stoneName = getStringWithouHead(lore, USED_SLOT).trim();
                if (unembeddingList.contains(stoneName)) {
                    newLore.add(EMPTY_SLOT);
                    unEmbedding = true;
                    continue;
                }
            }
            if(unEmbedding) {
                if(lore.contains(STONE_LEVEL_MARKER_IN_ITEM)){
                    Integer level = romanNumeralToInt(getPureString(getStringWithouHead(lore,STONE_LEVEL_MARKER_IN_ITEM)));
                    if(stoneLevelMap.containsKey(level)){
                        stoneLevelMap.put(level,stoneLevelMap.get(level)+1);
                    }
                    else {
                        stoneLevelMap.put(level,1);
                    }
                }
                if (lore.contains(ISOLATION)) {
                    unEmbedding = false;
                    continue;
                }
            }
            if (unEmbedding) continue;
            newLore.add(lore);
        }

        List<ItemStack> primordialStone = stoneLevelMap.keySet().stream()
                .map(level -> creatPrimordialStone(level,stoneLevelMap.get(level)))
                .collect(Collectors.toList());

        List<List> returnList = new ArrayList<>();
        returnList.add(newLore);
        returnList.add(primordialStone);
        return returnList;
    }

    private static WCheckBox createStoneCheckBox(String stoneName, int num, Container scrollContainer){
        WCheckBox checkBox = new WCheckBox(scrollContainer,stoneName,
                CHECK_1, CHECK_2,
                0,15*num,42,15);
        checkBox.setName(stoneName);
        checkBox.setSelectName(stoneName);
        checkBox.setOffsetName(checkBox.getW()/2);
        return checkBox;
    }

    public static void unEmbeddingCheck(ItemStack equipment, Container guiContainer, Player player){
        WScrollingContainer scroll = (WScrollingContainer) guiContainer.getComponent("choose_scroll");
        Container scrollContainer = scroll.getContainer();
        WTextList tipsText = (WTextList) guiContainer.getComponent("tips_list");
        WButton decide_button = (WButton) guiContainer.getComponent("decide_button");

        scrollContainer.getComponentMap().keySet().forEach(stone -> scrollContainer.remove(stone));
        decide_button.setCanPress(false);
        tipsText.setContent(new ArrayList<>());

        try{
            List<String> itemLore = equipment.getItemMeta().getLore();
            int num = 0;

            for (String lore : itemLore) {
                if (lore.contains(USED_SLOT)) {
                    String stoneName = getStringWithouHead(lore, USED_SLOT).trim();
                    scrollContainer.add(createStoneCheckBox(stoneName, num, scrollContainer));
                    num++;
                }
            }

            if (num == 0) {
                tipsText.setContent(Collections.singletonList("§c无镶嵌石"));
            } else {
                decide_button.setCanPress(true);
            }
        }catch (Exception e){
            tipsText.setContent(Collections.singletonList("§c非法输入"));
        }finally {
            WuxieAPI.updateGui(player);
        }
    }

    public static boolean inventoryPlentyFor(Player player,List<ItemStack> itemStacks){
        List<ItemStack> inventory = Arrays.asList(player.getInventory().getContents()).subList(0,36);

        AtomicLong emptySlotAmount = new AtomicLong(inventory.stream()
                .filter(i -> i==null)
                .count());

        boolean flag = itemStacks.stream().allMatch(itemStack -> {
            int amount = itemStack.getAmount();
            for(ItemStack slot : inventory){
                if(itemStack.isSimilar(slot)){
                   amount -= (slot.getMaxStackSize()-slot.getAmount());
                }
                if(amount<=0) break;
            }

            return amount<=0 || emptySlotAmount.getAndDecrement() > 0;
        });

        return flag;
    }
}
