package mc.nuoyuya.top.farmkeeper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FarmKeeperPlugin extends JavaPlugin implements Listener {

    private static FarmKeeperPlugin instance;
    private boolean pluginEnabled = true;
    private String warningMessage = "§c你踩坏了一处耕地，你是坏蛋！";
    private final Map<UUID, Long> cooldownMap = new HashMap<>();
    private final long COOLDOWN_TIME = 1000; // 1秒冷却时间

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置
        saveDefaultConfig();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        // 注册命令
        FarmKeeperCommand commandExecutor = new FarmKeeperCommand(this);
        this.getCommand("farmkeeper").setExecutor(commandExecutor);
        this.getCommand("farmkeeper").setTabCompleter(commandExecutor);

        // 加载配置
        loadConfig();

        getLogger().info("§a耕地保护插件已启用！");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("§c耕地保护插件已禁用！");
    }


    public void loadConfig() {
        reloadConfig();
        pluginEnabled = getConfig().getBoolean("enabled", true);
        warningMessage = getConfig().getString("warning-message", "§c你踩坏了一处耕地，你是坏蛋！");
    }

    public void saveConfigSettings() {
        getConfig().set("enabled", pluginEnabled);
        getConfig().set("warning-message", warningMessage);
        saveConfig();
    }

    @EventHandler
    public void onPlayerTrampleFarmland(PlayerInteractEvent event) {
        if (!pluginEnabled) return;

        // 检查是否是踩踏耕地事件
        if (event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType() == Material.FARMLAND) {

            Player player = event.getPlayer();
            UUID playerId = player.getUniqueId();

            // 冷却检查，防止频繁提示
            long currentTime = System.currentTimeMillis();
            if (cooldownMap.containsKey(playerId)) {
                long lastTime = cooldownMap.get(playerId);
                if (currentTime - lastTime < COOLDOWN_TIME) {
                    return;
                }
            }

            cooldownMap.put(playerId, currentTime);

            // 在经验条上方显示提示
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendTitle("", warningMessage, 10, 40, 10);
                }
            }.runTask(this);

            // 可选：在聊天栏也发送提示
            player.sendMessage(warningMessage);
        }
    }

    // Getter 和 Setter 方法
    public static FarmKeeperPlugin getInstance() {
        return instance;
    }

    public boolean isPluginEnabled() {
        return pluginEnabled;
    }

    public void setPluginEnabled(boolean enabled) {
        this.pluginEnabled = enabled;
        saveConfigSettings();
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String message) {
        this.warningMessage = message;
        saveConfigSettings();
    }
}