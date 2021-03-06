package com.timvisee.dungeonmaze.populator.maze.spawner;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import com.timvisee.dungeonmaze.config.DMConfigHandler;
import com.timvisee.dungeonmaze.event.generation.DMGenerationSpawnerCause;
import com.timvisee.dungeonmaze.event.generation.DMGenerationSpawnerEvent;
import com.timvisee.dungeonmaze.populator.maze.DMMazeRoomBlockPopulator;
import com.timvisee.dungeonmaze.populator.maze.DMMazeRoomBlockPopulatorArgs;

public class SpawnerPopulator extends DMMazeRoomBlockPopulator {
	public static final int MIN_LAYER = 1;
	public static final int MAX_LAYER = 7;
	public static final int CHANCE_OF_SPAWNER = 30;
	public static final double CHANCE_OF_TORCH_ADDITION_PER_LEVEL = -0.5; /* to 3 */
	public static final double MIN_SPAWN_DISTANCE = 2; // Chunks

	@Override
	public void populateRoom(DMMazeRoomBlockPopulatorArgs args) {
		Chunk c = args.getSourceChunk();
		Random rand = args.getRandom();
		int x = args.getChunkX();
		int z = args.getChunkZ();
		
		// Make sure the distance between the spawn and the current chunk is allowed
		if(distance(0, 0, c.getX(), c.getZ()) < MIN_SPAWN_DISTANCE)
			return;
					
		// Apply chances
		if(rand.nextInt(100) < CHANCE_OF_SPAWNER) {
			
			int spawnerX = x + rand.nextInt(6) + 1;
			int spawnerY = args.getFloorY() + 1;
			int spawnerZ = z + rand.nextInt(6) + 1;
			
			if(c.getBlock(spawnerX, spawnerY - 1, spawnerZ).getType() != Material.AIR) {
				Block spawnerBlock = c.getBlock(spawnerX, spawnerY, spawnerZ);
				spawnerBlock = c.getBlock(spawnerX, spawnerY, spawnerZ);
				
				if(spawnerBlock.getType() == Material.AIR) {
					// Generate a random spawnedType for the spawner
					EntityType spawnedType = null;
					int i = rand.nextInt(25) + 1;
					if(i >= 1 && i <= 9 && DMConfigHandler.mobs.contains("Zombie"))
						spawnedType = EntityType.ZOMBIE;
					else if(i == 10)
						spawnedType = EntityType.CAVE_SPIDER;
					else if(i >= 11 && i <= 15 && DMConfigHandler.mobs.contains("Skeleton"))
						spawnedType = EntityType.SKELETON;
						
					else if(i >= 16 && i <= 19 && DMConfigHandler.mobs.contains("Spider"))
						spawnedType = EntityType.SPIDER;
					else if(i == 20 && DMConfigHandler.mobs.contains("Enderman"))
						spawnedType = EntityType.ENDERMAN;
					else if(i >= 21 && i <= 22 && DMConfigHandler.mobs.contains("PigZombie"))
						spawnedType = EntityType.PIG_ZOMBIE;
					else if(i == 23 && DMConfigHandler.mobs.contains("Creeper"))
						spawnedType = EntityType.CREEPER;
					
					else if(i == 24 && DMConfigHandler.mobs.contains("MagmaCube"))
						spawnedType = EntityType.MAGMA_CUBE;
					
					else if(i == 25 && DMConfigHandler.mobs.contains("Silverfish"))
						spawnedType = EntityType.SILVERFISH;
					
					else // if no entity type is allowed and the random return none value, continue the for loop
						return;
					
					// Call the spawner generation event
					DMGenerationSpawnerEvent event = new DMGenerationSpawnerEvent(spawnerBlock, spawnedType, DMGenerationSpawnerCause.NORMAL, rand);
					Bukkit.getServer().getPluginManager().callEvent(event);
					
					// Make sure the event isn't cancelled yet
					if(event.isCancelled())
						return;
					
					// Change the block into a creature spawner
					spawnerBlock.setType(Material.MOB_SPAWNER);
					
					// Cast the created s pawner into a CreatureSpawner object
					CreatureSpawner s = (CreatureSpawner) spawnerBlock.getState();
					
					// Set the spawned type of the spawner
					s.setSpawnedType(event.getSpawnedType());
				}
			}
		}
	}
	
	public double distance(int x1, int y1, int x2, int y2) {
		double dx   = x1 - x2;         //horizontal difference 
		double dy   = y1 - y2;         //vertical difference 
		double dist = Math.sqrt( dx*dx + dy*dy ); //distance using Pythagoras theorem
		return dist;
	}
	
	/**
	 * Get the minimum layer
	 * @return Minimum layer
	 */
	@Override
	public int getMinimumLayer() {
		return MIN_LAYER;
	}
	
	/**
	 * Get the maximum layer
	 * @return Maximum layer
	 */
	@Override
	public int getMaximumLayer() {
		return MAX_LAYER;
	}
}