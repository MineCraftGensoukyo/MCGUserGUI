package moe.gensoukyo.gui

import me.wuxie.wakeshow.wakeshow.api.event.PlayerCloseScreenEvent
import moe.gensoukyo.gui.pages.TestPage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.command
import taboolib.module.chat.TellrawJson as TJ
import taboolib.common.platform.function.info
import taboolib.common.platform.function.registerListener
import taboolib.platform.BukkitPlugin
import taboolib.platform.type.BukkitPlayer
import java.util.*

object MCGUserGUI : Plugin() {
    private val mainCommandList = hashMapOf<String,String>(
        "help" to " -- 获取帮助\n",
        "version" to " -- 查看插件版本\n",
        "test" to " -- 打开测试GUI\n"
    )



    override fun onLoad() {
        // override onLoad()
    }

    override fun onEnable() {
        // override onEnable()
        command("mcggui"){
            //一级子指令参数
            dynamic(true){
                suggestion<ProxyPlayer> { _, _ ->
                    mainCommandList.keys.toList()
                }
            }
            literal("help"){
                execute<taboolib.common.platform.ProxyCommandSender> { sender, _, _->
                    val helpText = StringBuilder()
                    helpText.append("§6 ===============================\n")
                    moe.gensoukyo.gui.MCGUserGUI.mainCommandList.forEach { (k, v) ->
                        helpText.append(k+v)
                    }
                    helpText.append("===============================\n")
                    sender.sendMessage(helpText.toString())
                }
            }
            literal("version"){
                execute<ProxyCommandSender> { sender, _, _ ->
                    val description = BukkitPlugin.getInstance().description
                    sender.sendMessage("§6${description.name} --- ${description.version}")
                }
            }
            literal("test"){
                execute<Player> { sender, _, _ ->
                    val testPage = TestPage()
                    sender.sendMessage("测试GUI已开启${testPage}")
                    testPage.showPage(sender)
                }
            }
            execute<ProxyCommandSender> { sender, _, _ ->
                val json = TJ()
                json.append("§6输入")
                    .append(
                        TJ().append(" §nhelp")
                            .runCommand("/mcggui help")
                    )
                    .append("§k 查看帮助")
                    .sendTo(sender)
            }
        }
        info("MCGUserGUI加载完毕！")
        info("""§6
            =====================================================================
              __  __  _____ _____ _    _                _____ _    _ _____ 
             |  \/  |/ ____/ ____| |  | |              / ____| |  | |_   _|
             | \  / | |   | |  __| |  | |___  ___ _ __| |  __| |  | | | |  
             | |\/| | |   | | |_ | |  | / __|/ _ \ '__| | |_ | |  | | | |  
             | |  | | |___| |__| | |__| \__ \  __/ |  | |__| | |__| |_| |_ 
             |_|  |_|\_____\_____|\____/|___/\___|_|   \_____|\____/|_____|
             ====================================================================
        """.trimIndent())

    }



    override fun onDisable() {

    }
}