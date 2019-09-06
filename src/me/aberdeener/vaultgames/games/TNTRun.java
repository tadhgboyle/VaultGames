package me.aberdeener.vaultgames.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.aberdeener.vaultgames.VaultGames;
import me.aberdeener.vaultgames.commands.GameCommand;

public class TNTRun implements Listener {

	public static boolean playing = false;

	String string = VaultGames.vcc.getString("string");
	String variable1 = VaultGames.vcc.getString("variable-1");
	String variable2 = VaultGames.vcc.getString("variable-2");

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();

		World world = player.getWorld();

		// see if they are in a game, and get the name
		GameCommand.getInstance();
		if (GameCommand.tntRunRemaining.containsKey(player.getUniqueId())) {

			// begin to get game world to make sure we use event in correct world
			World gameWorld = Bukkit.getWorld("tnt");

			// if the world they are in is equal to the game world from hashmap, continue
			if (world.equals(gameWorld)) {

				if (playing) {

					Location playerMove1 = event.getFrom().clone().subtract(0, 1, 0);
					Location playerMove2 = event.getFrom().clone().subtract(0, 2, 0);

					Block block1 = playerMove1.getBlock();
					Block block2 = playerMove2.getBlock();

					if (block1.getType() == Material.SAND || block1.getType() == Material.GRAVEL) {

						VaultGames.getInstance().getServer().getScheduler()
								.scheduleSyncDelayedTask(VaultGames.getInstance(), new Runnable() {
									public void run() {
										block1.setType(Material.AIR);
										block2.setType(Material.AIR);
									}
								}, 7);
					}

					// if a player drops below y15 make them dead
					if (playerMove1.getY() < 15) {
						player.setGameMode(GameMode.SPECTATOR);
						player.sendMessage(ChatColor.RED + "You died!");

						// after gmsp, teleport to the playLoc to spectate
						player.teleport(Bukkit.getWorld("tnt").getSpawnLocation());
						// remvoe them from the remaining hashmap to count how many players are left
						GameCommand.tntRunRemaining.remove(player.getUniqueId());
						int remaining = GameCommand.tntRunRemaining.size();
						// check size of hashmap, if more than 0, continue
						if (remaining != 0) {

							for (Player players : GameCommand.tntRunTotal.values()) {
								players.sendMessage(ChatColor.translateAlternateColorCodes('&',
										variable1 + player.getName() + string + " has died! Only " + variable2
												+ remaining + string + " players left."));
								return;
							}

							player.sendMessage(ChatColor.translateAlternateColorCodes('&', variable1 + player.getName()
									+ string + " has died! Only " + variable2 + remaining + string + " players left."));
						}
						// if less than 0, end game and teleport players to lobby
						else {

							for (Player players : GameCommand.tntRunTotal.values()) {
								players.setGameMode(GameMode.SPECTATOR);
								players.sendMessage(ChatColor.translateAlternateColorCodes('&',
										variable1 + player.getName() + string + " has won the game!"));
								players.sendMessage(ChatColor.translateAlternateColorCodes('&',
										string + "You will be teleported to the Lobby in 5 seconds..."));

								// make scheduler to teleport players to lobby after 5 seconds + clear hashmaps
								VaultGames.getInstance().getServer().getScheduler()
										.scheduleSyncDelayedTask(VaultGames.getInstance(), new Runnable() {
											public void run() {
												players.teleport(Bukkit.getWorld("Lobby").getSpawnLocation());

												GameCommand.tntRunTotal.clear();
												GameCommand.tntRunRemaining.clear();

												Bukkit.unloadWorld("tnt", false);

												VaultGames.getInstance().getServer().getScheduler()
														.scheduleSyncDelayedTask(VaultGames.getInstance(),
																new Runnable() {
																	public void run() {

																		Bukkit.getServer()
																				.createWorld(new WorldCreator("tnt"));
																		playing = false;
																		return;
																	}
																}, 20 * 5);

											}
										}, 20 * 5); // 20 (one second in ticks) * 5 (seconds to wait)
							
							}
						}
					}
				}
			}

			// if world is not equal to games.yml world, do nothing
			else {
				return;
			}
		}
		// if they arent in the hashmap -> never /game join, do nothing
		else {
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(final EntityDamageEvent event) {

		if (event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			if (player.getWorld().getName().equalsIgnoreCase("tnt")) {
				if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {

		Player player = (Player) event.getPlayer();

		if (GameCommand.tntRunRemaining.containsKey(player.getUniqueId())) {
			if (event.getMessage().equalsIgnoreCase("/tp") || (event.getMessage().equalsIgnoreCase("/hub") || (event
					.getMessage().equalsIgnoreCase("/lobby")
					|| (event.getMessage().equalsIgnoreCase("/warp") || (event.getMessage().equalsIgnoreCase("/sv")
							|| (event.getMessage().equalsIgnoreCase("/cr")
									|| (event.getMessage().equalsIgnoreCase("/tsv")))))))) {
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', string + "You cannot teleport during the game."));
				player.sendMessage(ChatColor.RED + "To leave the game: " + ChatColor.DARK_GREEN + "/game leave");
				event.setCancelled(true);
				return;
			}
		}
	}

	public static void queueRunnable() {

		String string = VaultGames.vcc.getString("string");
		String variable1 = VaultGames.vcc.getString("variable-1");
		String variable2 = VaultGames.vcc.getString("variable-2");

		// make scheduler to teleport players to lobby after 5 seconds + clear hashmaps
		VaultGames.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(VaultGames.getInstance(),
				new Runnable() {
					public void run() {

						int queued = GameCommand.tntRunTotal.size();
						int minPlayers = VaultGames.getInstance().gameData.getInt("tntrun.minPlayers");
						int needed = minPlayers - queued;

						if (playing == false) {

							if (queued == 0) {
								return;
							}

							if (queued < minPlayers) {
								for (Player players : Bukkit.getOnlinePlayers()) {
									players.sendMessage(ChatColor.translateAlternateColorCodes('&',
											string + "Only " + variable2 + needed + string + " players needed for "
													+ variable1 + "TNTRun!"));
									return;
								}
							}

							if (queued >= minPlayers) {
								playing = true;
								for (Player players : GameCommand.tntRunTotal.values()) {
									players.sendMessage(ChatColor.translateAlternateColorCodes('&', variable1 + "TNTRun"
											+ string + " is starting in " + variable1 + "10" + string + " seconds!"));
									VaultGames.getInstance().getServer().getScheduler()
											.scheduleSyncDelayedTask(VaultGames.getInstance(), new Runnable() {
												public void run() {
													players.teleport(Bukkit.getWorld("tnt").getSpawnLocation());
													return;
												}
											}, 20 * 10);
								}
							}
						}
					}
				}, 20 * 10); // 20 (one second in ticks) * 5 (seconds to wait)
	}
}