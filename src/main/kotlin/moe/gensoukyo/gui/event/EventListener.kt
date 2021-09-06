package moe.gensoukyo.gui.event

import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import me.wuxie.wakeshow.wakeshow.ui.component.WSlot
import moe.gensoukyo.gui.pages.WxAlchemyScreen
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.util.giveItem

object EventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun playerCloseScreenEventListener(e: PlayerCloseScreenEvent) {
        info("${e.player.name}关闭${e.screen.id} - ${e.screen}")
        if (e.screen.id == "炼金UI") {
            if (!(e.screen as WxAlchemyScreen).isSuccess) {
                e.player.sendTitle("§4你在中途离开", "§c无人看管的炼金炉烧毁了你的材料！", 5, 70, 10)
                e.player.spawnParticle(Particle.FLAME, 1.0, 1.0, 1.0, 15, 0.0, 0.0, 0.0)
            } else if ((e.screen as WxAlchemyScreen).isSuccess && (e.screen.container.getComponent("item_output_1") as WSlot).itemStack != ItemStack(
                    Material.AIR
                )
            ) {
                val item = (e.screen.container.getComponent("item_output_1") as WSlot).itemStack
                e.player.giveItem(item)
                e.player.sendTitle("§a合成成功！", "§c下次不要忘记取出物品了哦", 5, 70, 10)
            } else {
                e.player.sendTitle("§a合成成功！", "", 5, 70, 10)
            }
        }
        /*if (e.player.isOp)
            e.player.sendMessage("§6${e.player.name}关闭§4${e.screen.id} - ${e.screen}")*/
    }
}