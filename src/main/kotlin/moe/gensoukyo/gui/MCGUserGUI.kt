package moe.gensoukyo.gui

import moe.gensoukyo.gui.config.MainConfig.reload
import moe.gensoukyo.gui.pages.*
import moe.gensoukyo.gui.util.ClearCache
import org.bukkit.entity.Player
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.*
import taboolib.platform.BukkitPlugin
import moe.gensoukyo.gui.config.MainConfig as configs
import taboolib.module.chat.TellrawJson as TJ

object MCGUserGUI : Plugin() {
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

    override fun onLoad() {
        // override onLoad()
    }

    override fun onEnable() {

        command("mcggui", permissionDefault = PermissionDefault.OP, permissionMessage = "§c你没有使用这个指令的权限！") {
            //一级子指令参数
            literal("help", optional = true) {
                execute<taboolib.common.platform.ProxyCommandSender> { sender, _, _ ->
                    val helpText = StringBuilder()
                    helpText.append("§6 ===============================\n")
                    moe.gensoukyo.gui.MCGUserGUI.mainCommandList.forEach { (k, v) ->
                        helpText.append(k + v)
                    }
                    helpText.append("===============================\n")
                    sender.sendMessage(helpText.toString())
                }
            }
            literal("version", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    val description = BukkitPlugin.getInstance().description
                    sender.sendMessage("§6${pluginId} --- $pluginVersion")
                }
            }
            literal("reload", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    try {
                        configs.conf.reload()
                        configs.items.reload()
                        configs.loadEquipmentTypes()
                        if (configs.conf["clearCache"] as Boolean) {
                            onlinePlayers().forEach { player ->
                                ClearCache.run(player.cast())
                            }
                        }
                        sender.sendMessage("§6重载成功！ - Version:${pluginVersion}")
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
        info(
            """§6
            =====================================================================
                 __  __  _____ _____ _    _                _____ _    _ _____ 
                |  \/  |/ ____/ ____| |  | |              / ____| |  | |_   _|
                | \  / | |   | |  __| |  | |___  ___ _ __| |  __| |  | | | |  
                | |\/| | |   | | |_ | |  | / __|/ _ \ '__| | |_ | |  | | | |  
                | |  | | |___| |__| | |__| \__ \  __/ |  | |__| | |__| |_| |_ 
                |_|  |_|\_____\_____|\____/|___/\___|_|   \_____|\____/|_____|
                                $pluginId
                                Author  - DavidWang19, ZakeArias, satorishi
                                Version - $pluginVersion
             ====================================================================
        """.trimIndent()
        )

    }


    override fun onDisable() {

    }
}