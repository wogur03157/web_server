package com.aroasoft.tcpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ConfigProperties {
	private final Properties configProp = new Properties();
	private static ConfigProperties instance = null;
	 
// private constructor
    private ConfigProperties() {
    }
 
    public static ConfigProperties getInstance() {
    	if (instance == null) {

    		String PATH = "/data/config/tomcat/metatourism.config.properties";
    		System.out.println(PATH);
            instance = new ConfigProperties(PATH);
        }
	    return instance;
	}
	private ConfigProperties(String PATH)
	{
	      //Private constructor to restrict new instances
	  // InputStream in = this.getClass().getClassLoader().getResourceAsStream(PATH);
		
		InputStream in = null;
	   try {
		   File file = new File(PATH);
		   in =  new FileInputStream(file);
		   configProp.load(in);
	   } catch (IOException e) {
		   e.printStackTrace();
	   }finally {
		   try {
			   if (in!=null) in.close();
		   }catch(Exception e) {
			   
		   }
	   }
	}
	    
	public String getProperty(String key)
	{
		return configProp.getProperty(key);
	}
	  
	public Set<String> getAllPropertyNames(){
		return configProp.stringPropertyNames();
	}
	    
	public boolean containsKey(String key){
		return configProp.containsKey(key);
	}
	
	public void setProperty(String key, String value)
	{
		configProp.setProperty(key, value);
	}
}
