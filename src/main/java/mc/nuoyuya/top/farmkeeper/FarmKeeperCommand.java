package mc.nuoyuya.top.farmkeeper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmKeeperCommand implements CommandExecutor, TabCompleter {

    private final FarmKeeperPlugin plugin;

    public FarmKeeperCommand(FarmKeeperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家才能执行此命令！");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                plugin.setPluginEnabled(true);
                player.sendMessage("§a耕地保护已开启！");
                break;

            case "off":
                plugin.setPluginEnabled(false);
                player.sendMessage("§c耕地保护已关闭！");
                break;

            case "text":
                if (args.length < 2) {
                    player.sendMessage("§c用法: /farmkeeper text <提示内容>");
                    return true;
                }
                // 合并参数，支持空格
                StringBuilder messageBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    if (i > 1) messageBuilder.append(" ");
                    messageBuilder.append(args[i]);
                }
                String newMessage = messageBuilder.toString();
                plugin.setWarningMessage(newMessage);
                player.sendMessage("§a提示内容已更新为: " + newMessage);
                break;

            case "reload":
                plugin.reloadConfig();
                plugin.loadConfig();  // 现在这个方法可以正常调用了
                player.sendMessage("§a配置已重载！");
                break;

            case "status":
                String status = plugin.isPluginEnabled() ? "§a开启" : "§c关闭";
                player.sendMessage("§6耕地保护状态: " + status);
                player.sendMessage("§6当前提示: " + plugin.getWarningMessage());
                break;

            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 第一个参数补全
            List<String> subCommands = Arrays.asList("on", "off", "text", "reload", "status");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && "text".equalsIgnoreCase(args[0])) {
            // text 命令的提示
            completions.add("<提示内容>");
        }

        return completions;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== 耕地保护插件使用说明 ===");
        player.sendMessage("§b/farmkeeper on §f- 开启耕地保护");
        player.sendMessage("§b/farmkeeper off §f- 关闭耕地保护");
        player.sendMessage("§b/farmkeeper text <内容> §f- 修改提示内容");
        player.sendMessage("§b/farmkeeper reload §f- 重载配置");
        player.sendMessage("§b/farmkeeper status §f- 查看状态");
        player.sendMessage("§b/farmkeeper §f- 显示此帮助信息");
    }
}