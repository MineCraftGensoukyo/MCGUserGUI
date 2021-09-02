package moe.gensoukyo.gui

import moe.gensoukyo.gui.pages.TestPage
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.command
import taboolib.module.chat.TellrawJson as TJ
import taboolib.common.platform.function.info
import taboolib.platform.type.BukkitPlayer
import java.util.*

object MCGUserGUI : Plugin() {
    private val mainCommandList = listOf(
        "help",
        "test"
    )
    override fun onLoad() {
        // override onLoad()
        command("mcggui"){
            ///一级子指令参数
            dynamic(false){
                suggestion<ProxyPlayer> { _, _ ->
                    mainCommandList
                }
            }
            literal("help"){
                execute<ProxyCommandSender> { sender,_,_->
                    sender.sendMessage("""§6
                    ===============================
                    help  -- 获取帮助
                    test  -- 打开测试GUI
                    ===============================
                """.trimIndent())
                }
            }
            literal("test"){
                execute<ProxyPlayer> { sender,_,_ ->
                    val testPage = TestPage()
                    sender.sendMessage("测试GUI已开启${testPage}")
                    testPage.showPage(sender)
                }
            }
            execute<ProxyCommandSender> { sender, context, argument ->
                val json = TJ()
                json.append("§6输入")
                    .append(
                        TJ().append(" §nhelp")
                            .runCommand("mcggui help")
                    )
                    .append("§k 查看帮助")
            }
        }
    }

    override fun onEnable() {
        // override onEnable()
    }

    override fun onDisable() {
        // override onDisable()
    }
}