package net.toxiic.sellall;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public enum Lang
{
  COMMAND_NO_PERMISSIONS("Command.No permissions", "&4You do not have access to that command."),  NO_SHOP("No shop", "&c%s does not have a shop!"),  SOLD_ITEMS("Sold items", "&aYou sold &b<amount> &aitem(s) for &b$<money>&a!"),  MULTIPLIER("Multiplier", "&aYou received more money than normal because you have a <multiplier>x multiplier.");
  
  private static YamlConfiguration config = null;
  private static File configFile = null;
  private String key = "";
  private String defaultValue = "";
  
  private Lang(String key, String defValue)
  {
    this.key = key;
    this.defaultValue = defValue;
  }
  
  public String getMessage()
  {
    return replaceChatColours(getRawMessage());
  }
  
  public String getMessage(Object... format)
  {
    return replaceChatColours(String.format(getRawMessage(), format));
  }
  
  public String getRawMessage()
  {
    return config != null ? config.getString(this.key, this.defaultValue) : this.defaultValue;
  }
  
  public String getReplacedMessage(Object... objects)
  {
    String langMessage = getRawMessage();
    if (objects != null)
    {
      Object firstObject = null;
      for (int i = 0; i < objects.length; i++) {
        if (i % 2 == 0) {
          firstObject = objects[i];
        } else if ((firstObject != null) && (objects[i] != null)) {
          langMessage = langMessage.replace(firstObject.toString(), objects[i].toString());
        }
      }
    }
    return replaceChatColours(langMessage);
  }
  
  public static void sendMessage(CommandSender sender, Lang lang)
  {
    String strMessage = lang.getMessage();
    if (!strMessage.isEmpty())
    {
      List<String> messages = new ArrayList();
      if (strMessage.contains("\n"))
      {
        String[] messageSplit = strMessage.split("\n");
        for (String message : messageSplit) {
          messages.add(message);
        }
      }
      else
      {
        messages.add(strMessage);
      }
      String message;
      for (Iterator i$ = messages.iterator(); i$.hasNext(); sender.sendMessage(message)) {
        message = (String)i$.next();
      }
    }
  }
  
  public static void sendMessage(CommandSender sender, Lang lang, Object... objects)
  {
    String strMessage = lang.getMessage(objects);
    if (!strMessage.isEmpty())
    {
      List<String> messages = new ArrayList();
      if (strMessage.contains("\n"))
      {
        String[] messageSplit = strMessage.split("\n");
        for (String message : messageSplit) {
          messages.add(message);
        }
      }
      else
      {
        messages.add(strMessage);
      }
      String message;
      for (Iterator i$ = messages.iterator(); i$.hasNext(); sender.sendMessage(message)) {
        message = (String)i$.next();
      }
    }
  }
  
  public static void sendRawMessage(CommandSender sender, Lang lang)
  {
    String strMessage = lang.getRawMessage();
    if (!strMessage.isEmpty())
    {
      List<String> messages = new ArrayList();
      if (strMessage.contains("\n"))
      {
        String[] messageSplit = strMessage.split("\n");
        for (String message : messageSplit) {
          messages.add(message);
        }
      }
      else
      {
        messages.add(strMessage);
      }
      String message;
      for (Iterator i$ = messages.iterator(); i$.hasNext(); sender.sendMessage(message)) {
        message = (String)i$.next();
      }
    }
  }
  
  public static void sendReplacedMessage(CommandSender sender, Lang lang, Object... objects)
  {
    String strMessage = lang.getReplacedMessage(objects);
    if (!strMessage.isEmpty())
    {
      List<String> messages = new ArrayList();
      if (strMessage.contains("\n"))
      {
        String[] messageSplit = strMessage.split("\n");
        for (String message : messageSplit) {
          messages.add(message);
        }
      }
      else
      {
        messages.add(strMessage);
      }
      String message;
      for (Iterator i$ = messages.iterator(); i$.hasNext(); sender.sendMessage(message)) {
        message = (String)i$.next();
      }
    }
  }
  
  public static void init(JavaPlugin plugin)
  {
    if (configFile == null) {
      configFile = new File(plugin.getDataFolder(), "messages.yml");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
    for (Lang value : values()) {
      if (!config.isSet(value.key)) {
        config.set(value.key, value.defaultValue);
      }
    }
    try
    {
      config.save(configFile);
    }
    catch (Exception ex) {}
  }
  
  public static String getString(String path)
  {
    return config.getString(path);
  }
  
  public static String getString(String path, String defaultValue)
  {
    return config.getString(path, defaultValue);
  }
  
  public static String saveString(String path, String value)
  {
    if (!config.isSet(path))
    {
      config.set(path, value);
      try
      {
        config.save(configFile);
      }
      catch (Exception ex) {}
    }
    return config.isSet(path) ? config.getString(value) : value;
  }
  
  private static String replaceChatColours(String aString)
  {
    return aString != null ? ChatColor.translateAlternateColorCodes('&', aString) : "";
  }
}
