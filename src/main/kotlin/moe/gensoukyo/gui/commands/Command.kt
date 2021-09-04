package moe.gensoukyo.gui.commands

import moe.gensoukyo.gui.pages.TestPage
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.module.chat.TellrawJson
import taboolib.platform.BukkitPlugin

/*@CommandHeader("mcggui",aliases = ["mgui"])*/
object Command {
    private val mainCommandList = listOf(
        "help",
        "test",
        "version"
    )

    /*@CommandBody*/
    val main = mainCommand{
        ///一级子指令参数
        dynamic(false){
            suggestion<ProxyPlayer> { _, _ ->
                mainCommandList
            }
        }
        execute<ProxyCommandSender> { _, _, _ ->
            val json = TellrawJson()
            json.append("§6输入")
                .append(
                    TellrawJson().append(" §nhelp")
                        .runCommand("mcggui help")
                )
                .append("§k 查看帮助")
        }
    }

    /*@CommandBody(optional = true)*/
    val help = subCommand {
        execute<ProxyCommandSender> { sender, _, _->
            sender.sendMessage("""§6
                    ===============================
                    help  -- 获取帮助
                    test  -- 打开测试GUI
                    ===============================
                """.trimIndent())
        }
    }

    /*@CommandBody(optional = true)*/
    val test = subCommand {
        execute<Player> { sender, _, _ ->
            val testPage = TestPage()
            sender.sendMessage("测试GUI已开启${testPage}")
            testPage.showPage(sender)
        }
    }

    /*@CommandBody(optional = true)*/
    val version = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val description = BukkitPlugin.getInstance().description
            sender.sendMessage("§6${description.name} --- ${description.version}")
        }
    }
}