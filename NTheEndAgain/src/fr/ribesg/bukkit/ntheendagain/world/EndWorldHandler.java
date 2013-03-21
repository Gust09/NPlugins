package fr.ribesg.bukkit.ntheendagain.world;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

import fr.ribesg.bukkit.ncore.Utils;
import fr.ribesg.bukkit.ntheendagain.Config;
import fr.ribesg.bukkit.ntheendagain.NTheEndAgain;

public class EndWorldHandler {

    private final static Random                          rand = new Random();

    private final String                                 camelCaseWorldName;

    private final NTheEndAgain                           plugin;
    private final World                                  endWorld;
    private final EndChunks                              chunks;
    private final Config                                 config;
    private final HashMap<UUID, HashMap<String, Double>> dragons;

    private int                                          numberOfAliveEDs;

    public EndWorldHandler(final NTheEndAgain instance, final World world) {
        plugin = instance;
        endWorld = world;
        camelCaseWorldName = Utils.toLowerCamelCase(endWorld.getName());
        chunks = new EndChunks(plugin);
        config = new Config(plugin, endWorld.getName());
        dragons = new HashMap<UUID, HashMap<String, Double>>();
        updateNumberOfAliveEDs();
    }

    public void loadConfigs() throws IOException {
        try {
            chunks.load(plugin.getConfigFilePath(camelCaseWorldName + "Chunks"));
        } catch (final IOException e) {
            throw new IOException(camelCaseWorldName + "Chunks.yml");
        }
        try {
            config.loadConfig(plugin, camelCaseWorldName + "Config.yml");
        } catch (final IOException e) {
            throw new IOException(camelCaseWorldName + "Config.yml");
        }
    }

    public void saveChunks() throws IOException {
        try {
            chunks.write(plugin.getConfigFilePath(camelCaseWorldName + "Chunks"));
        } catch (final IOException e) {
            throw new IOException(camelCaseWorldName + "Chunks.yml");
        }
    }

    public void init() {

    }

    public void playerHitED(final UUID enderDragonID, final String playerName, final double dmg) {
        HashMap<String, Double> dragonMap;
        if (!dragons.containsKey(enderDragonID)) {
            dragonMap = new HashMap<String, Double>();
            dragons.put(enderDragonID, dragonMap);
        } else {
            dragonMap = dragons.get(enderDragonID);
        }
        if (dragonMap.containsKey(playerName)) {
            dragonMap.put(playerName, dragonMap.get(playerName) + dmg);
        } else {
            dragonMap.put(playerName, dmg);
        }
    }

    public void respawnDragons() {
        for (int i = numberOfAliveEDs; i <= config.getNbEnderDragons(); i++) {
            respawnDragon();
        }
    }

    private void respawnDragon() {
        final int x = rand.nextInt(41) - 20; // [-20;20]
        final int y = 70 + rand.nextInt(21); // [70;90]
        final int z = rand.nextInt(41) - 20; // [-20;20]
        final Location loc = new Location(endWorld, x, y, z);
        endWorld.spawnEntity(loc, EntityType.ENDER_DRAGON);
    }

    private void updateNumberOfAliveEDs() {
        numberOfAliveEDs = endWorld.getEntitiesByClass(EnderDragon.class).size();
    }
}
