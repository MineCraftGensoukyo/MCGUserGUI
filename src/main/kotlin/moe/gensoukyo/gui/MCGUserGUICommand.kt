package moe.gensoukyo.gui

import moe.gensoukyo.gui.config.MainConfig
import moe.gensoukyo.gui.config.MainConfig.reload
import moe.gensoukyo.gui.pages.*
import moe.gensoukyo.gui.util.ClearCache
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.TellrawJson

class MCGUserGUICommand {
    private val mainCommandList = hashMapOf(
        "help" to " -- 获取帮助\n",
        "version" to " -- 查看插件版本\n",
        "reload" to " -- 重载配置\n",
        "test" to " -- 打开测试GUI\n",
        "enhance [player]" to " -- 打开强化GUI\n",
        "proficiency [player]" to " -- 打开熟练度GUI\n",
        "decompose [player]" to " -- 打开分解GUI\n",
        "embedding [player]" to " -- 打开镶嵌GUI\n",
        "unembedding [player]" to " -- 打开摘除镶嵌GUI\n"
    )
    fun register(){
        command("mcggui", permissionDefault = PermissionDefault.OP, permissionMessage = "§c你没有使用这个指令的权限！") {
            //一级子指令参数
            literal("help", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    val helpText = StringBuilder()
                    helpText.append("§6 ===============================\n")
                    mainCommandList.forEach { (k, v) ->
                        helpText.append(k + v)
                    }
                    helpText.append("===============================\n")
                    sender.sendMessage(helpText.toString())
                }
            }
            literal("version", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    sender.sendMessage("§6$pluginId --- $pluginVersion")
                }
            }
            literal("reload", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    try {
                        MainConfig.conf.reload()
                        MainConfig.items.reload()
                        MainConfig.loadConfigs()
                        if (MainConfig.conf["clearCache"] as Boolean) {
                            onlinePlayers().forEach { player ->
                                ClearCache.run(player.cast())
                            }
                        }
                        sender.sendMessage("§6重载成功！ - Version:$pluginVersion")
                    } catch (e: Exception) {
                        sender.sendMessage("§6重载失败！ - Err:${e}")
                    }
                }
            }
            literal("test", optional = true) {
                execute<Player> { sender, _, _ ->
                    val testPage = TestPage()
                    sender.sendMessage("测试GUI已开启${testPage}")
                    testPage.showCachePage(sender)
                }
            }
            literal("enhance", optional = true) {
                execute<Player> { sender, _, _ ->
                    val enhancePage = EnhancePage()
                    enhancePage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val enhancePage = EnhancePage()
                        enhancePage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            literal("proficiency", optional = true) {
                execute<Player> { sender, _, _ ->
                    val enhancePage = ProficiencyPage()
                    enhancePage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val enhancePage = ProficiencyPage()
                        enhancePage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            literal("decompose", optional = true) {
                execute<Player> { sender, _, _ ->
                    val decomposePage = DecomposePage()
                    decomposePage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val decomposePage = DecomposePage()
                        decomposePage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            literal("embedding", optional = true) {
                execute<Player> { sender, _, _ ->
                    val embeddingPage = EmbeddingPage()
                    embeddingPage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val embeddingPage = EmbeddingPage()
                        embeddingPage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            literal("unembedding", optional = true) {
                execute<Player> { sender, _, _ ->
                    val unembeddingPage = UnEmbeddingPage()
                    unembeddingPage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val unembeddingPage = UnEmbeddingPage()
                        unembeddingPage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            literal("collection", optional = true) {
                execute<Player> { sender, _, _ ->
                    val collectionPage = CollectionPage()
                    collectionPage.showCachePage(sender)
                }
                dynamic {
                    suggestion<ProxyCommandSender> { _, _ ->
                        onlinePlayers().map { it.name }
                    }
                    execute<ProxyCommandSender> { _, _, argument ->
                        val collectionPage = CollectionPage()
                        collectionPage.showCachePage(getProxyPlayer(argument)!!.cast())
                    }
                }
            }
            execute<ProxyCommandSender> { sender, _, _ ->
                val json = TellrawJson()
                json.append("§6输入")
                    .append(
                        TellrawJson().append(" §nhelp")
                            .runCommand("/mcggui help")
                    )
                    .append("§k 查看帮助")
                    .sendTo(sender)
            }
        }
    }
}