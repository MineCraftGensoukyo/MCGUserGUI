package moe.gensoukyo.gui

import moe.gensoukyo.gui.config.CsvConfigLoader
import moe.gensoukyo.gui.config.MainConfig.reload
import moe.gensoukyo.gui.pages.TestPage
import org.bukkit.entity.Player
import taboolib.common.platform.Plugin
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.pluginVersion
import taboolib.platform.BukkitPlugin
import moe.gensoukyo.gui.config.MainConfig as configs
import taboolib.module.chat.TellrawJson as TJ

object MCGUserGUI : Plugin() {
    private val mainCommandList = hashMapOf<String, String>(
        "help" to " -- 获取帮助\n",
        "version" to " -- 查看插件版本\n",
        "reload" to " -- 重载配置\n",
        "test" to " -- 打开测试GUI\n"
    )

    override fun onLoad() {
        // override onLoad()
    }

    override fun onEnable() {

        command("mcggui", permissionDefault = PermissionDefault.FALSE, permissionMessage = "§c你没有使用这个指令的权限！") {
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
                        configs.alchemyItems.reload()
                        sender.sendMessage("§6重载成功！ - Version:${pluginVersion}")
                        configs.alchemyRecipes.reload()
                        sender.sendMessage("§6CSV配置重载成功！ - Version:${CsvConfigLoader.version}")
                    } catch (e: Exception) {
                        sender.sendMessage("§6重载失败！ - Err:${e}")
                    }
                }
            }
            literal("test", optional = true) {
                execute<Player> { sender, _, _ ->
                    //configs.alchemyItems.setItemStack("test", ItemStack(Material.APPLE))
                }
                literal("cache", optional = true) {
                    execute<Player> { sender, _, _ ->
                        val testPage = TestPage()
                        sender.sendMessage("测试GUI已开启${testPage}")
                        testPage.showCachePage(sender)
                    }
                }
            }
            literal("coftest", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    configs.conf.getKeys(false).forEach {
                        sender.sendMessage(it)
                    }
                    sender.sendMessage("alchemyItems")
                    configs.alchemyItems.getKeys(false).forEach {
                        sender.sendMessage(it)
                    }

                }
            }
            literal("csvtest", optional = true) {
                execute<ProxyCommandSender> { sender, _, _ ->
                    configs.alchemyRecipes.recipeList.forEach {
                        sender.sendMessage(it.toString())
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
                                Author  - ZakeArias,DavidWang19
                                Version - $pluginVersion
             ====================================================================
        """.trimIndent()
        )

    }


    override fun onDisable() {

    }
}