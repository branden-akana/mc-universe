package com.hyperfresh.mcuniverse;

import com.octopod.util.common.FileUtil;
import com.octopod.util.common.IOUtils;
import com.octopod.util.configuration.yaml.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Octopod - octopodsquad@gmail.com
 */
public class UniverseConfig
{
	/**
	 * The currently active configuration.
	 */
	private YamlConfiguration config = null;

	private UniversePlugin plugin = null;

	private Logger logger = null;

	private File file;

	public UniverseConfig(UniversePlugin plugin, Logger logger)
	{
		this.logger = logger;
		this.plugin = plugin;
		this.file = new File(plugin.getPluginFolder(), "config.yml");
	}

	private InputStream readInternalConfig()
	{
		return plugin.getResource("config.yml");
	}

	private InputStream readLocalConfig()
	{
		try {
			return new FileInputStream(file);
		} catch (IOException e) {
			return null;
		}
	}

	private boolean isConfigOld()
	{
		InputStream input = readInternalConfig();
		if(input != null)
		{
			YamlConfiguration config = new YamlConfiguration(input);
			logger.i(UniverseAPI.getInstance().PREFIX + "&7Internal Config Version: &6" + config.getInt("version", 0));
			logger.i(UniverseAPI.getInstance().PREFIX + "&7Local Config Version: &6" + getConfig().getInt("version", -1));
			IOUtils.closeSilent(input);
			return config.getInt("version", 0) > getConfig().getInt("version", -1);
		} else {
			return false;
		}
	}

	/**
	 * Writes the default configuration (resource) into configFile.
	 * Throws various errors.
	 *
	 * @throws NullPointerException, IOException
	 */
	private YamlConfiguration writeInternalConfig() throws IOException
	{
		//Backup the old config.yml if it exists
		if (file.exists())
		{
			String fileName = "config.yml.old";
			//Copy the old config to this new backup config.
			InputStream input = readLocalConfig();
			if(input != null)
			{
				FileUtil.write(new File(plugin.getPluginFolder(), fileName), input);
				IOUtils.closeSilent(input);
			}
			logger.i(UniverseAPI.getInstance().PREFIX + "&7Old configuration renamed to &6" + fileName);
		}

		InputStream input = readInternalConfig();
		if(input != null)
		{
			YamlConfiguration config = new YamlConfiguration(readInternalConfig());

			try {
				FileUtil.write(file, input);
				logger.i(UniverseAPI.getInstance().PREFIX + "&7New config created, version: &6" + config.getInt("version"));
			} catch (IOException e)
			{
				logger.i(UniverseAPI.getInstance().PREFIX + "&cError writing configuration!");
				e.printStackTrace();
			}

			return config;
		} else
		{
			throw new IOException("Couldn't find the internal configuration file.");
		}
	}

	/**
	 * Loads the configuration.
	 * Each created instance of UniverseAPIConfig will load a new config.
	 */
	public void load() throws IOException
	{
		logger.i("&8====== &7LOADING Universe CONFIGURATION &8======");

		if (!file.exists())
		{
			//Sets the config from the internal file
			config = writeInternalConfig();
		}
		else
		{
			//Sets the current configuration from config.yml.
			config = new YamlConfiguration(readLocalConfig());
			if (isConfigOld())
			{
				config = writeInternalConfig();
			}
		}

		logger.i("&8====== &7FINISHED LOADING CONFIGURATION &8======");
	}

	public YamlConfiguration getConfig()
	{
		return config;
	}
}
