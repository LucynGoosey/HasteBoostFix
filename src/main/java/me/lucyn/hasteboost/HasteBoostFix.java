package me.lucyn.hasteboost;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


//This plugin was inspired by Sebastian Richel's Haste Boost: https://github.com/sebastianrich18/Haste-Boost


public final class HasteBoostFix extends JavaPlugin implements Listener {

    private final Material inHand = Material.NETHERITE_PICKAXE;
    private final Logger log = Bukkit.getLogger();
    private FileConfiguration config = this.getConfig();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log.info("Haste-Boost is now enabled!");
        saveDefaultConfig();

    }

    //entire onCommand method copied from original project. Fixed typos. removed isMuted completely.
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("hb")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("showconfig")) {
                    String str = "HASTE BOOST CURRENT CONFIG SETTINGS:\n";
                    str += "yLvl: " + config.getInt("minYLvl") + "\n";
                    str += "boostAmplifier: " + config.getInt("boostAmplifier") + "\n";
                    str += "netheritePicOnly: " + config.getBoolean("netheritePicOnly") + "\n";

                    sender.sendMessage(str);
                    log.info(str);
                    return true;

                } else if (args[0].equalsIgnoreCase("reload")) {
                    this.reloadConfig();
                    this.config = this.getConfig();
                    sender.sendMessage("[Haste Boost] Reloaded config.yml");
                    log.info("[Haste Boost] Reloaded config.yml");
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }


    @EventHandler
    public void onEntityEffect(EntityPotionEffectEvent event) {
        if(event.getCause().equals(EntityPotionEffectEvent.Cause.BEACON)) {
            Player player = (Player) event.getEntity();
            boolean isBelowYLvl = player.getLocation().getY() < config.getInt("yLvl");
            boolean hasNetheriteInHand = player.getInventory().getItemInMainHand().getType() == this.inHand;
            boolean isInHaste = player.getPotionEffect(PotionEffectType.HASTE) != null;

            // kept typo for compatibility
            if (config.getBoolean("netheritePicOnly")) {
                if (isInHaste && isBelowYLvl && hasNetheriteInHand) {
                    applyEffect(player);
                }
            } else {
                if (isInHaste && isBelowYLvl) {
                    applyEffect(player);
                }
            }

        }

    }

    public void applyEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, config.getInt("boostAmplifier")));

    }


    @Override
    public void onDisable() {
        log.info("Haste Boost Stopped");
    }
}
