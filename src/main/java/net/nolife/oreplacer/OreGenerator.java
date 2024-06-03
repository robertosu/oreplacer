package net.nolife.oreplacer;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenBlocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Random;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public class OreGenerator {
    private final OraxenPlugin oraxen;
    private final String oreBlockId;
    private final int minHeight;
    private final int veinSize;
    private final Set<Material> replaceableBlocks;

    public OreGenerator(OraxenPlugin oraxen, String oreBlockId, int minHeight, int veinSize, Set<Material> replaceableBlocks) {
        this.oraxen = oraxen;
        this.oreBlockId = oreBlockId;
        this.minHeight = minHeight;
        this.veinSize = veinSize;
        this.replaceableBlocks = replaceableBlocks;
    }

    public void generateOre(World world, Random random, int chunkX, int chunkZ) {
        int maxHeight = world.getMaxHeight();

        if (oreBlockId == null) {
            // El ID del bloque de mineral no es válido
            return;
        }

        int veinCount = 0;
        while (veinCount < 5) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(maxHeight - minHeight);

            Location oreLocation = new Location(world, x, y, z);
            generateVein(oreLocation, oreBlockId, random);
            veinCount++;
        }
    }

    private void generateVein(Location center, String oreBlockId, Random random) {
        World world = center.getWorld();
        int veinSize = 8 + random.nextInt(8); // Tamaño de la veta (8-15 bloques)
        int radius = 3 + random.nextInt(3); // Radio de la veta (máximo 5)

        for (int i = 0; i < veinSize; i++) {
            int x = center.getBlockX() + random.nextInt(radius * 2 + 1) - radius;
            int y = center.getBlockY() + random.nextInt(radius * 2 + 1) - radius;
            int z = center.getBlockZ() + random.nextInt(radius * 2 + 1) - radius;
            Location location = new Location(world, x, y, z);

            Block block = location.getBlock();
            if (replaceableBlocks.contains(block.getType())) {
                try {
                    OraxenBlocks.place(oreBlockId, location);
                    System.out.println("Bloque de mineral colocado en: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());

                } catch (Exception e) {
                    System.out.println("Error al colocar bloque de mineral en: " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                    e.printStackTrace();
                }
            }
        }
    }
}