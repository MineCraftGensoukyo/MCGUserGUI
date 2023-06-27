package moe.gensoukyo.gui.pages;

import me.wuxie.wakeshow.wakeshow.api.WuxieAPI;
import me.wuxie.wakeshow.wakeshow.ui.Container;
import me.wuxie.wakeshow.wakeshow.ui.WInventoryScreen;
import me.wuxie.wakeshow.wakeshow.ui.WxScreen;
import me.wuxie.wakeshow.wakeshow.ui.component.WButton;
import me.wuxie.wakeshow.wakeshow.ui.component.WImage;
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot;
import me.wuxie.wakeshow.wakeshow.ui.component.WTextList;
import moe.gensoukyo.gui.config.MainConfig;
import moe.gensoukyo.gui.util.EmbeddingTools;
import moe.gensoukyo.gui.util.Pos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmbeddingPage implements Page{
    private final String VERSION = (String) (MainConfig.INSTANCE.getConf().get("imageVersion"));
    private final String GUI_BACKGROUND = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BG.png",VERSION);
    private final String BTN_1 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_1.png",VERSION);
    private final String BTN_2 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_2.png",VERSION);
    private final String BTN_3 = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_BTN_3.png",VERSION);
    private final String SUCCESS_URL = String.format("https://cdn.jsdelivr.net/gh/MineCraftGensoukyo/MCGImages@%s/img/Enhance_Success.png",VERSION);
    private final Pos guiTestPos = new Pos(-1, -1, 190, 190, 0,0);
    private final WInventoryScreen gui = new WInventoryScreen("镶嵌UI",
            GUI_BACKGROUND,
            guiTestPos.getDx(),guiTestPos.getDy(),guiTestPos.getW(),guiTestPos.getH(),15,guiTestPos.getH()-80);
    private final Container guiContainer = gui.getContainer();
    private final WButton button = new WButton(guiContainer,"embedding_button","",
            BTN_1,BTN_2,BTN_3,
            120,35);
    private final WImage imageSuccess = new WImage(guiContainer, "image_success",
                    SUCCESS_URL, 152, 38, 0, 0);
    private final List<String> titleList = new ArrayList<>();{
        titleList.add("§e§l镶嵌");
    }
    private final WTextList titleText = new WTextList(guiContainer,"title_text",titleList,90,10,600,20);
    private final WTextList equipmentTipsText = new WTextList(guiContainer,"equipment_tips",new ArrayList<>(),
            150, 30, 60, 20);
    private final WTextList stoneTipsText = new WTextList(guiContainer,"stone_tips",new ArrayList<>(),
            9, 20, 60, 20);
    private final WSlot stoneSlot = new WSlot(guiContainer,"stone_slot", new ItemStack(Material.AIR),51,41);
    private final WSlot equipmentSlot = new WSlot(guiContainer,"equipment_slot", new ItemStack(Material.AIR),101,41);
    {
        equipmentSlot.setCanDrag(true);
        stoneSlot.setCanDrag(true);
        button.setTooltips(Collections.singletonList("确认镶嵌"));
        button.setW(25);
        button.setH(25);
        button.setFunction((t,pl) -> {
            String tips = EmbeddingTools.embeddingApprovalCheck(equipmentSlot.getItemStack(),stoneSlot.getItemStack());

            if(tips.isEmpty()) {
                ItemStack newEquipment = EmbeddingTools.embedding(equipmentSlot.getItemStack(), stoneSlot.getItemStack());
                equipmentSlot.setItemStack(newEquipment);

                ItemStack newStone = stoneSlot.getItemStack().clone();
                newStone.setAmount(newStone.getAmount() - 1);
                stoneSlot.setItemStack(newStone);

                button.setW(0);
                button.setH(0);
                equipmentTipsText.setContent(Collections.singletonList("§a镶嵌成功"));
            }
            else {
                equipmentTipsText.setContent(Collections.singletonList(tips));
            }

            WuxieAPI.updateGui(pl);
        });

        guiContainer.add(button);
        guiContainer.add(titleText);
        guiContainer.add(stoneTipsText);
        guiContainer.add(equipmentTipsText);
        guiContainer.add(equipmentSlot);
        guiContainer.add(stoneSlot);
    }

    @NotNull
    @Override
    public WxScreen getPage() {
        return gui;
    }

    @Override
    public void showPage(@NotNull Player player) {
        DefaultImpls.showPage(this,player);
    }

    @Override
    public void showCachePage(@NotNull Player player) {
        DefaultImpls.showCachePage(this,player);
    }
}
