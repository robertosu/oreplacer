package net.nolife.oreplacer;

import io.th0rgal.oraxen.OraxenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Oreplacer extends JavaPlugin implements Listener {

    private OreGenerator oreGenerator;

    @Override
    public void onEnable() {
        // Configurar el generador de minerales
        OraxenPlugin oraxen = (OraxenPlugin) Bukkit.getPluginManager().getPlugin("Oraxen");
        String oreBlockId = "orax_ore";
        int minHeight = 10;
        int veinSize = 8;
        Set<Material> replaceableBlocks = new HashSet<>(Arrays.asList(Material.STONE, Material.DEEPSLATE));
        oreGenerator = new OreGenerator(oraxen, oreBlockId, minHeight, veinSize, replaceableBlocks);

        // Registrar el listener para el evento ChunkPopulateEvent
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("CustomOrePlugin ha sido habilitado");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomOrePlugin ha sido deshabilitado");

    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        World world = event.getWorld();
        if (world.getName().equals("mundo")) {
            int chunkX = event.getChunk().getX();
            int chunkZ = event.getChunk().getZ();
            Random random = new Random();

            // Programar una tarea asíncrona para generar las vetas
            getServer().getScheduler().runTaskAsynchronously(this, () -> {

                oreGenerator.generateOre(world, random, chunkX, chunkZ);
            });
        }
    }
}