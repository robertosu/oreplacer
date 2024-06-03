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
        BlockData oreBlockData = OraxenBlocks.getOraxenBlockData(oreBlockId);

        if (oreBlockData == null) {
            getLogger().info("No es un ID VALIDO!");
            return;
        }

        int veinCount = 0;
        while (veinCount < 5) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(maxHeight - minHeight);

            Location oreLocation = new Location(world, x, y, z);
            generateVein(oreLocation, oreBlockData, random);
            veinCount++;
        }
    }

    private void generateVein(Location center, BlockData oreBlockData, Random random) {
        World world = center.getWorld();
        int radius = 3 + random.nextInt(3); // Radio de la veta (máximo 5)

        for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
            for (int y = center.getBlockY() - radius; y <= center.getBlockY() + radius; y++) {
                for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                    Location location = new Location(world, x, y, z);
                    double distance = location.distance(center);

                    if (distance < radius && random.nextDouble() < 0.5) {
                        Block block = location.getBlock();
                        if (replaceableBlocks.contains(block.getType())) {
                            block.setBlockData(oreBlockData);
                        }
                    }
                }
            }
        }
    }
}