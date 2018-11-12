package net.toxiic.sellall.hooks;

import java.io.File;
import net.toxiic.sellall.SellAll;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class RankupAPI
{
  private static final String rankupPluginName = "ToXiiCRanks";
  
  public static String getRank(Player player)
  {
    if ((hasRankup()) && (Vault.hasVaultPermissions())) {
      try
      {
        File pluginDir = SellAll.getInstance().getDataFolder().getAbsoluteFile().getParentFile();
        if ((pluginDir != null) && (pluginDir.exists()))
        {
          File rankupDir = new File(pluginDir, "ToXiiCxRanks");
          if (rankupDir.exists())
          {
            File rankupFile = new File(rankupDir, "config.yml");
            if (rankupFile.exists())
            {
              FileConfiguration rankupConfig = YamlConfiguration.loadConfiguration(rankupFile);
              if (rankupConfig.contains("Ranks."))
              {
                String[] playerRanks = Vault.getPermissions().getPlayerGroups(player);
                String strCurrentRank = Vault.getPermissions().getPrimaryGroup(player);
                if ((strCurrentRank == null) || (!rankupConfig.contains("Ranks." + strCurrentRank))) {
                  for (String playerRank : playerRanks) {
                    if (rankupConfig.contains("Ranks." + playerRank)) {
                      strCurrentRank = playerRank;
                    }
                  }
                }
                return strCurrentRank == null ? "Default" : strCurrentRank;
              }
            }
          }
        }
      }
      catch (Exception ex) {}
    }
    String playerRank = Vault.hasVaultPermissions() ? Vault.getPermissions().getPrimaryGroup(player) : "Default";
    return playerRank == null ? "Default" : playerRank;
  }
  
  public static boolean hasRankup()
  {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("ToXiiCxRanks");
  }
}
