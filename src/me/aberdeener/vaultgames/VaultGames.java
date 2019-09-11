package me.aberdeener.vaultgames;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.aberdeener.vaultgames.commands.GameCommand;
import me.aberdeener.vaultgames.games.SpeedPvP;
import me.aberdeener.vaultgames.games.TNTRun;

public class VaultGames extends JavaPlugin implements Listener {

	public static VaultGames instance;

	// data file setup
	public File gameDataFile;
	public FileConfiguration gameData;

	public static FileConfiguration vcc = Bukkit.getServer().getPluginManager().getPlugin("VaultCore").getConfig();

	@Override
	public void onEnable() {

		instance = this;

		// so that worlds dont error out on start
		new BukkitRunnable() {
			@Override
			public void run() {
				// create player data file
				createGameData();

				// register listeners (below)
				registerListeners();
			}
		}.runTaskLater(this, 200);

		// register commands and their classes
		this.getCommand("game").setExecutor(new GameCommand());
		this.getCommand("game").setTabCompleter(new TabCompletion());

		TNTRunQueue();
		SpeedPvPQueue();
		
		Bukkit.getWorld("tnt").setAutoSave(false);
	}

	private void TNTRunQueue() {
		this.getServer().getScheduler().runTaskTimer(VaultGames.getInstance(), new Runnable() {
			public void run() {
				TNTRun.TNTRunQueue();
			}
		}, 0, 20 * 10);
	}
	
	private void SpeedPvPQueue() {
		this.getServer().getScheduler().runTaskTimer(VaultGames.getInstance(), new Runnable() {
			public void run() {
				SpeedPvP.SpeedPvPQueue();
			}
		}, 0, 20 * 10);
	}

	private void registerListeners() {

		// creates the plugin manager
		PluginManager pm = Bukkit.getServer().getPluginManager();

		// listens for the main class
		pm.registerEvents(this, this);

		// Listens for tntrun class
		pm.registerEvents(new TNTRun(), this);
		
		// Listens for speed pvp class
		pm.registerEvents(new SpeedPvP(), this);
		
		//Listens for generic events class
		pm.registerEvents(new Events(), this);

	}

	// call data file from other class
	public FileConfiguration getGameData() {
		return this.gameData;
	}

	// method to make data file
	private void createGameData() {
		gameDataFile = new File(getDataFolder(), "games.yml");
		if (!gameDataFile.exists()) {
			gameDataFile.getParentFile().mkdirs();
			saveResource("games.yml", false);
		}

		gameData = new YamlConfiguration();
		try {
			gameData.load(gameDataFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	// save data file
	public void saveGameData() {
		try {
			gameData.save(gameDataFile);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	// lets us use this instance to load coords, reload config etc
	public static VaultGames getInstance() {
		return instance;
	}
}