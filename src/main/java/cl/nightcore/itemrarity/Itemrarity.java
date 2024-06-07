package cl.nightcore.itemrarity;

import dev.aurelium.auraskills.api.AuraSkillsBukkitProvider;
import dev.aurelium.auraskills.api.item.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.stat.Stats;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Itemrarity extends JavaPlugin implements CommandExecutor {

    private static final String IDENTIFIER_KEY = "identify_used";
    private static final List<Stats> STATS = Arrays.asList(
            Stats.CRIT_CHANCE, Stats.CRIT_DAMAGE, Stats.HEALTH, Stats.LUCK,
            Stats.REGENERATION, Stats.SPEED, Stats.STRENGTH, Stats.TOUGHNESS,
            Stats.WISDOM
    );

    @Override
    public void onEnable() {
        // Intentamos registrar el comando inmediatamente
        if (registerIdentifyCommand()) {
            getLogger().info("Comando 'identify' registrado con éxito.");
        } else {
            // Si falla, intentamos cada segundo hasta 10 veces
            new BukkitRunnable() {
                int attempts = 0;

                @Override
                public void run() {
                    if (registerIdentifyCommand() || ++attempts >= 10) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(this, 20L, 20L); // 20 ticks = 1 segundo
        }
    }

    private boolean registerIdentifyCommand() {
        if (!Bukkit.getPluginManager().isPluginEnabled("AuraSkills")) {
            getLogger().warning("AuraSkills no está habilitado. El comando 'identify' no estará disponible.");
            return false;
        }

        try {
            AuraSkillsBukkit.get(); // Verificamos que podemos obtener la instancia
            getCommand("identify").setExecutor(this);
            return true;
        } catch (IllegalStateException e) {
            getLogger().warning("AuraSkills no está inicializado correctamente. Reintentando...");
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            player.sendMessage(ChatColor.RED + "Debes tener un item en tu mano principal.");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(getNamespacedKey(IDENTIFIER_KEY), PersistentDataType.INTEGER)) {
            player.sendMessage(ChatColor.RED + "Este item ya ha sido identificado.");
            return true;
        }

        try {
            Random random = new Random();
            int statsCount = 3 + random.nextInt(3); // 3, 4 o 5 stats
            int totalValue = 0;

            List<String> lore = new ArrayList<>();
            ItemStack modifiedItem = item.clone(); // Clonar el ItemStack original

            for (int i = 0; i < statsCount; i++) {
                Stats stat = STATS.get(random.nextInt(STATS.size()));
                int value = 5 + random.nextInt(12); // 5 a 16
                totalValue += value;

                // Agregar la estadística al objeto ItemStack
                modifiedItem = AuraSkillsBukkit.get().getItemManager().addModifier(modifiedItem, ModifierType.ITEM, stat, value, true);
                //lore.add(ChatColor.GRAY + stat.getDisplayName(Locale.forLanguageTag(player.getLocale())) + ": +" + value);
            }

            double average = (double) totalValue / statsCount;
            String rarity;
            if (average >= 14) {
                rarity = ChatColor.GOLD + "Legendario";
            } else if (average >= 12) {
                rarity = ChatColor.LIGHT_PURPLE + "Mítico";
            } else if (average >= 10) {
                rarity = ChatColor.BLUE + "Raro";
            } else {
                rarity = ChatColor.DARK_AQUA + "Común";
            }

            lore.add(0, rarity);

            player.getInventory().setItemInMainHand(modifiedItem);
            player.sendMessage(ChatColor.GREEN + "¡Item identificado con éxito!");
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED + "Error al acceder a AuraSkills. Por favor, inténtalo más tarde.");
            getLogger().warning("AuraSkills no está inicializado: " + e.getMessage());
        }

        return true;
    }

    private org.bukkit.NamespacedKey getNamespacedKey(String key) {
        return new org.bukkit.NamespacedKey(this, key);
    }
}