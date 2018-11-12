package net.toxiic.sellall;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.toxiic.sellall.hooks.MultiplierAPI;
import net.toxiic.sellall.hooks.RankupAPI;
import net.toxiic.sellall.hooks.Vault;

import org.black_ixx.bossshop.BossShop;
import org.black_ixx.bossshop.api.BossShopAPI;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.black_ixx.bossshop.core.enums.BSBuyType;
import org.black_ixx.bossshop.events.BSPlayerPurchaseEvent;
import org.black_ixx.bossshop.events.BSPlayerPurchasedEvent;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SellAll
  extends JavaPlugin
{
  private static SellAll instance = null;
  private BossShop bossShop = null;
  private BossShopAPI bossShopAPI = null;
  private Permission sellAllPermission = new Permission("toxiicsellall.sellall");
  
  public void onEnable()
  {
    if ((!getServer().getPluginManager().isPluginEnabled("BossShop")) || (!(getServer().getPluginManager().getPlugin("BossShop") instanceof BossShop)))
    {
      getServer().getLogger().severe("*** BossShop could not be found! ***");
      getPluginLoader().disablePlugin(this);
      return;
    }
    instance = this;
    Vault.setupEconomy();
    Vault.setupPermissions();
    this.bossShop = ((BossShop)getServer().getPluginManager().getPlugin("BossShop"));
    this.bossShopAPI = this.bossShop.getAPI();
    Lang.init(this);
    
    getCommand("sellall").setExecutor(this);
    getCommand("sall").setExecutor(this);
    
    getServer().getPluginManager().addPermission(this.sellAllPermission);
  }
  
  public void onDisable()
  {
    getServer().getPluginManager().removePermission(this.sellAllPermission);
    
    Vault.resetInstance();
    this.bossShopAPI = null;
    this.bossShop = null;
    instance = null;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((cmd.getName().equals("sellall")) || (cmd.getName().equals("sall"))) {
      return runSellCommand(sender, args);
    }
    return false;
  }
  
  public boolean runSellCommand(CommandSender sender, String[] args)
  {
    if (args.length == 0)
    {
      if ((sender instanceof Player))
      {
        Player player = (Player)sender;
        if (player.hasPermission(this.sellAllPermission))
        {
          String playerRank = RankupAPI.getRank(player);
          BSShop rankShop = this.bossShopAPI.getShop(playerRank);
          if (rankShop == null)
          {
            rankShop = this.bossShopAPI.getShop("Z");
            if (rankShop == null)
            {
              Lang.sendMessage(sender, Lang.NO_SHOP, new Object[] { "Z" });
              return true;
            }
          }
          int amountSold = 0;
          double amountReceived = 0.0D;
          
          ItemStack[] itemContents = player.getInventory().getContents();
          double playerMultiplier = MultiplierAPI.getMultiplier(player);
          for (int itemIndex = 0; itemIndex < itemContents.length; itemIndex++)
          {
            ItemStack playerItem = itemContents[itemIndex];
            if ((playerItem != null) && (playerItem.getType() != Material.AIR)) {
              try
              {
                BSBuy item = null;
                for (Map.Entry<ItemStack, BSBuy> itemEntry : rankShop.getItems().entrySet()) {
                  if ((itemEntry.getKey() != null) && (((ItemStack)itemEntry.getKey()).getType() == playerItem.getType()) && (((ItemStack)itemEntry.getKey()).getDurability() == playerItem.getDurability()))
                  {
                    item = (BSBuy)itemEntry.getValue();
                    break;
                  }
                }
                if (item != null)
                {
                  BSPlayerPurchaseEvent purchaseEvent = new BSPlayerPurchaseEvent(player, rankShop, item);
                  getServer().getPluginManager().callEvent(purchaseEvent);
                  if ((!purchaseEvent.isCancelled()) && (item.getBuyType() == BSBuyType.Money))
                  {
                    Object objReward = item.getReward();
                    double itemReward = (objReward instanceof Integer) ? ((Integer)objReward).doubleValue() : (objReward instanceof Double) ? ((Double)objReward).doubleValue() : 0.0D;
                    double reward = itemReward / 64.0D * playerItem.getAmount() * playerMultiplier;
                    itemContents[itemIndex] = new ItemStack(Material.AIR);
                    if (reward < 0.0D) {
                      Vault.getEconomy().withdrawPlayer(player, reward);
                    } else {
                      Vault.getEconomy().depositPlayer(player, reward);
                    }
                    if (this.bossShop.getClassManager().getSettings().getTransactionLogEnabled()) {
                      this.bossShop.getClassManager().getTransactionLog().addTransaction(player, item);
                    }
                    try
                    {
                      BSPlayerPurchasedEvent purchasedEvent = new BSPlayerPurchasedEvent(player, rankShop, item);
                      getServer().getPluginManager().callEvent(purchasedEvent);
                    }
                    catch (Exception ex)
                    {
                      ex.printStackTrace();
                    }
                    amountSold++;
                    amountReceived += reward;
                  }
                }
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
              }
            }
          }
          if (amountSold > 0)
          {
            player.getInventory().setContents(itemContents);
            player.updateInventory();
          }
          Lang.sendReplacedMessage(sender, Lang.SOLD_ITEMS, new Object[] { "<amount>", Integer.valueOf(amountSold), "<money>", String.format("%.1f", new Object[] { Double.valueOf(amountReceived) }) });
          if (playerMultiplier != 1.0D) {
            Lang.sendReplacedMessage(sender, Lang.MULTIPLIER, new Object[] { "<multiplier>", String.format("%.1f", new Object[] { Double.valueOf(playerMultiplier) }) });
          }
        }
        else
        {
          Lang.sendMessage(sender, Lang.COMMAND_NO_PERMISSIONS);
        }
      }
      else
      {
        sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
      }
    }
    else if (args.length == 1)
    {
      if (args[0].equalsIgnoreCase("reload"))
      {
        if (sender.isOp())
        {
          reloadConfig();
          Lang.init(this);
          sender.sendMessage(ChatColor.GREEN + getDescription().getFullName() + " reloaded.");
        }
        else
        {
          sender.sendMessage(ChatColor.GREEN + "ToXiiC SellAll");
        }
      }
      else {
        sender.sendMessage(ChatColor.GREEN + "ToXiiC SellAll");
      }
    }
    else {
      sender.sendMessage(ChatColor.GREEN + "ToXiiC SellAll");
    }
    return true;
  }
  
  public static SellAll getInstance()
  {
    return instance;
  }
}
