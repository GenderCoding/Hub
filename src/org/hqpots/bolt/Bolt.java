package org.hqpots.bolt;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hqpots.bolt.commands.SetSpawnCommand;
import org.hqpots.bolt.scoreboard.ColorUtils;
import org.hqpots.bolt.scoreboard.ScoreboardHelper;
import org.hqpots.bolt.selector.PlayerEvents;
import org.hqpots.bolt.selector.ServerSelector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Bolt extends JavaPlugin implements Listener, PluginMessageListener
{   
  public static Team team;
	
  private static Bolt instance;

  public static FileConfiguration config;
  public static File conf;

public static Object ghost;
  PluginManager manager = Bukkit.getServer().getPluginManager();
  
  private final Map<Player, ScoreboardHelper> scoreboardHelperMap = new HashMap<>();
  
  public static Bolt getInstance()
  {
    if (instance == null)
    {
      instance = new Bolt();
    }
    return instance;
  }
  
  @Override
  public void onEnable()
  {
    instance = this;

    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

    Bukkit.getServer().getPluginManager().registerEvents(this, this);

    setupScoreboard();

    for (Player player : Bukkit.getServer().getOnlinePlayers())
    {
      onPlayerJoin(player);
    }

    long timeMillis = System.currentTimeMillis();
    getConsoleCommandSender().sendMessage(new ColorUtils().translateFromString("&a[Bolt] Plugin loaded in " + (System.currentTimeMillis() - timeMillis) + "ms."));

    config = getConfig();
    config.options().copyDefaults(true);
    conf = new File(getDataFolder(), "config.yml");
    saveConfig();
    saveDefaultConfig();

    getCommand("setspawn").setExecutor(new SetSpawnCommand(this));

    manager.registerEvents(new PlayerEvents() , this);
    manager.registerEvents(new ServerSelector() , this);

  }

  @Override
  public void onDisable()
  {
    instance = null;
  }
  
  public ConsoleCommandSender getConsoleCommandSender()
  {
    return Bukkit.getServer().getConsoleSender();
  }
  
  public List<String> getDeveloper()
  {
    return Arrays.asList(new String[] { "Mason" });
  }
  
  public FileConfigurationOptions getFileConfigurationOptions()
  {
    return getConfig().options();
  }
  
  public byte[] getOnlinePlayers()
  {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("PlayerCount");
    out.writeUTF("ALL");
    return out.toByteArray();
  }
  
  private void onPlayerJoin(Player player)
  { 
    new BukkitRunnable() 
    {
      @Override
      public void run()
      {
        if (player.isOnline())
        {
          Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
          ScoreboardHelper scoreboardHelper = new ScoreboardHelper(scoreboard, new ColorUtils().translateFromString("&d&lHCFHQ &c[HUB]"));
          
	  scoreboardHelperMap.put(player, scoreboardHelper);
	}
      }
    }.runTaskLater(this, 20L);
  }
  
  @EventHandler	
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    Player player = event.getPlayer();
    onPlayerJoin(player);
  }
  
  @EventHandler	
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    scoreboardHelperMap.remove(player);
  }
  
  @EventHandler
  public void onPlayerKick(PlayerKickEvent event)
  {
    Player player = event.getPlayer();
    scoreboardHelperMap.remove(player);
  }
  
  private String getRanks(Player player)
  {
    String message = "";
    PermissionUser permissionUser = PermissionsEx.getUser(player);
    for (String ranks : permissionUser.getGroupNames())
    {
      message += ranks + ", ";
    }
    
    if (message.length() > 2)
    {
      message = message.substring(0, message.length() - 2);
    }
    
    if (message.length() == 0)
    {
      message = "User";
    }
    return message;
  }
  
  public void setupScoreboard()
  {
    new BukkitRunnable()
    {
      @Override
      public void run()
      {
        for (Map.Entry<Player, ScoreboardHelper> entry : scoreboardHelperMap.entrySet())
        {
	  Player player = entry.getKey();
	  ScoreboardHelper scoreboardHelper = entry.getValue();
	  scoreboardHelper.clear();
          scoreboardHelper.add(new ColorUtils().translateFromString("&7&m------------------------"));
          scoreboardHelper.add(new ColorUtils().translateFromString("&d&lName:"));
          scoreboardHelper.add(new ColorUtils().translateFromString("&e" + player.getName()));
          scoreboardHelper.add(new ColorUtils().translateFromString(" "));
          scoreboardHelper.add(new ColorUtils().translateFromString("&d&lRank:"));
          scoreboardHelper.add(new ColorUtils().translateFromString("&e" + getRanks(player)));
          //scoreboardHelper.add(new ColorUtils().translateFromString(" "));
          //for (Queue queue : Queue.getQueues())
          //{
           // if (queue.getPlayers().containsKey(player.getUniqueId()))
            //{
             // scoreboardHelper.add(new ColorUtils().translateFromString(" "));
              //scoreboardHelper.add(new ColorUtils().translateFromString("&cQueued for:"));
              //scoreboardHelper.add(new ColorUtils().translateFromString(" &b" + queue.getServer()));
              //scoreboardHelper.add(new ColorUtils().translateFromString(" &bPosition: &f#" + queue.getPlayers().get(player.getUniqueId()) + " of " + queue.getQueueSize()));
            //}
          //}
          scoreboardHelper.add(new ColorUtils().translateFromString(" "));
          scoreboardHelper.add(new ColorUtils().translateFromString("&dstore.hcfhq.org"));
          scoreboardHelper.add(new ColorUtils().translateFromString("&7&m------------------------"));
              
          scoreboardHelper.update(player);
        }
      }
    }.runTaskTimer(this, 0L, 3L);
  }

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] message)
  {
  }
}