package moe.gensoukyo.gui.pages;

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI;
import me.wuxie.wakeshow.wakeshow.ui.Container;
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen;
import me.wuxie.wakeshow.wakeshow.ui.WxScreen;
import me.wuxie.wakeshow.wakeshow.ui.component.*;
import moe.gensoukyo.gui.config.MainConfig;
import moe.gensoukyo.gui.util.EmbeddingTools;
import moe.gensoukyo.gui.util.Pos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnEmbeddingPage implements Page {
    private final String VERSION = (String) (MainConfig.INSTANCE.getConf().get("imageVersion"));
    private final String GUI_BACKGROUND = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BG.png",VERSION);
    private final String BTN_1 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_1.png",VERSION);
    private final String BTN_2 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_2.png",VERSION);
    private final String BTN_3 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Unembedding_BTN_3.png",VERSION);

    private final Pos GUI_POS = new Pos(-1, -1, 190, 190, 0,0);
    private final WInventoryScreen gui = new WInventoryScreen("摘除镶嵌UI",
            GUI_BACKGROUND,
            GUI_POS.getDx(),GUI_POS.getDy(),GUI_POS.getW(),GUI_POS.getH(),15,GUI_POS.getH()-80);
    private final Container guiContainer = gui.getContainer();
    private final List<String> titleList = new ArrayList<>();{
        titleList.add("§9§l摘除镶嵌");
    }
    private final WScrollingContainer scroll = new WScrollingContainer(guiContainer,"choose_scroll",
            147,4,39,68,200);
    private final WTextList titleText = new WTextList(guiContainer,"title_list",titleList,78,5,60,20);
    private final WTextList tipsText = new WTextList(guiContainer,"tips_list",new ArrayList<>(),35,62,60,20);
    private final WSlot equipmentSlot = new WSlot(guiContainer,"equipment_slot", new ItemStack(Material.AIR),88,42);
    private final WButton decide_button = new WButton(guiContainer,"decide_button","",
            BTN_1,BTN_2,BTN_3,
            25,35);
    private final Container scrollContainer = scroll.getContainer();
    {
        tipsText.setScale(0.9);
        decide_button.setW(45);
        decide_button.setH(13);
        decide_button.setCanPress(false);
        decide_button.setTooltips(Collections.singletonList("摘除镶嵌"));

        decide_button.setFunction((t,pl) -> {
            ItemStack item = equipmentSlot.getItemStack();
            List<String> itemLore = item.getItemMeta().getLore();
            List<String> unembeddingList = new ArrayList<>();

            scrollContainer.getComponentMap().forEach((key,value) -> {
                if(!(value instanceof WCheckBox)) return;
                WCheckBox choose = (WCheckBox) value;
                if(choose.isSelect()) unembeddingList.add(key);
            });

            if(unembeddingList.isEmpty()){
                tipsText.setContent(Collections.singletonList("§c未选择"));
            }
            else {
                List<List> returnList = EmbeddingTools.unEmbedding(itemLore,unembeddingList);
                List<String> newLore = returnList.get(0);
                List<ItemStack> primordialStoneList = returnList.get(1);

                if(EmbeddingTools.inventoryPlentyFor(pl, primordialStoneList)){
                    ItemStack newEquipment = item.clone();
                    ItemMeta newMeta = item.getItemMeta().clone();
                    newMeta.setLore(newLore);
                    newEquipment.setItemMeta(newMeta);
                    equipmentSlot.setItemStack(newEquipment);

                    primordialStoneList.forEach(pl.getInventory()::addItem);

                    tipsText.setContent(Collections.singletonList("§a摘除成功"));

                    decide_button.setCanPress(false);
                    scrollContainer.getComponentMap().keySet().forEach(scrollContainer::remove);
                }
                else {
                    tipsText.setContent(Collections.singletonList("§c背包不足"));
                }
            }

            WuxieAPI.updateGui(pl);
        });

        equipmentSlot.setCanDrag(true);
        scroll.setBarWidth(5);
        scroll.setShowScrollBar(false);

        guiContainer.add(decide_button);
        guiContainer.add(titleText);
        guiContainer.add(tipsText);
        guiContainer.add(scroll);
        guiContainer.add(equipmentSlot);

    }

    @Override
    public WxScreen getPage() {
        return gui;
    }

    @Override
    public void showPage(Player player) {
        DefaultImpls.showPage(this,player);
    }

    @Override
    public void showCachePage(Player player) {
        DefaultImpls.showCachePage(this,player);
    }
}