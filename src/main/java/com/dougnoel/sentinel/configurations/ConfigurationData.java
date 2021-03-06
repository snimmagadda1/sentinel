package com.dougnoel.sentinel.configurations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import com.dougnoel.sentinel.exceptions.ConfigurationNotFoundException;
import com.dougnoel.sentinel.strings.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigurationData extends File {
	/**
	 * Config file for sentinel Configuration Settings. Users can create a sentinel.yml file and we will load
	 * configs from this file instead of setting them manually on the command line each time. A user can override
	 * a specific setting by passing in a command line arg or using System.getProperty() in the setup method in the Test java file.
	 */
	private static final long serialVersionUID = 3930207641065895241L;
	
	@JsonProperty("configurations")
	public Map<String, Map<String, String>> configurations;
	
	@JsonCreator
    public ConfigurationData() throws FileNotFoundException {
        super("conf/sentinel.yml");
    }
	
	/**
	 * Returns the default configuration value for the given key.
	 * 
	 * @param configurationKey String the name of the setting to retrieve
	 * @return String the configuration value
	 * @throws ConfigurationNotFoundException if the value is not found in the configuration file
	 */
	public String getDefaultConfigurationValue(String configurationKey) throws ConfigurationNotFoundException {
		return getConfigurationValue(null, configurationKey);
	}
	
	/**
	 * Returns the configuration value for the given environment and key .
	 * 
	 * @param environment String the environment in which the test is running (e.g. dev, qa, sit)
	 * @param configurationKey String the name of the setting to retrieve
	 * @return String the configuration value
	 * @throws ConfigurationNotFoundException if the value is not found in the configuration file
	 */
	public String getConfigurationValue(String environment, String configurationKey) throws ConfigurationNotFoundException {
		String configurationValue = null;
		boolean defaultChecked = false;
		// This is here in case something falls through, but there are checks when we get the environment to
		// stop execution and throw an exception if no env is set.
		if (environment == null) {
			environment = "default";
		}
		try {
			if (configurations.containsKey(environment)) {
				configurationValue = configurations.get(environment).get(configurationKey);
			}
			// If we did not find a value for the set environment, then we check the default settings
			if (configurationValue == null) {
				defaultChecked = true;
				configurationValue = configurations.get("default").get(configurationKey);
			}
		} catch (NullPointerException e){
			String errorMessage = StringUtils.format(
					"A configuration value for {} was not found for the {} environment{}.", configurationKey,
					environment, defaultChecked == true ? " or the default environment" : "");
			throw new ConfigurationNotFoundException(errorMessage);	
		}

		return configurationValue;
	}
}