package net.toxiic.sellall.hooks;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

public class Vault
{
  private static Object vaultEconomy = null;
  private static Object vaultPermissions = null;
  
  public static Economy getEconomy()
  {
    return vaultEconomy != null ? (Economy)vaultEconomy : null;
  }
  
  public static Permission getPermissions()
  {
    return vaultPermissions != null ? (Permission)vaultPermissions : null;
  }
  
  public static boolean hasVaultEconomy()
  {
    return vaultEconomy != null;
  }
  
  public static boolean hasVaultPermissions()
  {
    return (vaultPermissions != null) && (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault"));
  }
  
  public static void resetInstance()
  {
    vaultEconomy = null;
    vaultPermissions = null;
  }
  
  public static boolean setupEconomy()
  {
    try
    {
      if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault"))
      {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
          vaultEconomy = economyProvider.getProvider();
        }
        return vaultEconomy != null;
      }
    }
    catch (Exception ex) {}
    return false;
  }
  
  public static boolean setupPermissions()
  {
    try
    {
      if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault"))
      {
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
          vaultPermissions = permissionProvider.getProvider();
        }
        return vaultPermissions != null;
      }
    }
    catch (Exception ex) {}
    return false;
  }
}
