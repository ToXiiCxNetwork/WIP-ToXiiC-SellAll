package net.toxiic.sellall.hooks;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;


public class MultiplierAPI
{
  public static double getMultiplier(Player player)
  {
    if ((player != null) && 
      (hasMultiplier())) {
      try
      {
        return net.toxiic.multiplier.MultiplierAPI.getMultiplier(player);
      }
      catch (Exception ex) {}
    }
    return 1.0D;
  }
  
  public static boolean hasMultiplier()
  {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("ToXiiCxMultiplier");
  }
}
