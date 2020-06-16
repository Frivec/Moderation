package fr.frivec.json;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonManager {
	
	private Gson gson;
	
	public GsonManager() {
		
		this.gson = createGsonInstance();
		
	}
	
	private Gson createGsonInstance() {
		
		return new GsonBuilder()
				.setDateFormat(DateFormat.SHORT)
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}
	
	public String serializeObject(final Object object) {
		return this.gson.toJson(object);
	}
	
	public Object deSeralizeJson(final String json, Class<?> object) {
		return this.gson.fromJson(json, object);
	}
	
	public String getUUIDFromMojang(String name) {
		
		final String stringURL = "https://api.mojang.com/users/profiles/minecraft/" + name;
		
		try {
			
			@SuppressWarnings("deprecation")
			final String UUIDJson = IOUtils.toString(new URL(stringURL).openStream());           
			
			if(UUIDJson.isEmpty())
				
				return "invalid name";                       
            
			final JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            
            return UUIDObject.get("id").toString();
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			
		}
		
		return "error";
		
	}
	
	public String getName(String uuid) {
    
		final String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
        
		try {
            
			@SuppressWarnings("deprecation")
			final String nameJson = IOUtils.toString(new URL(url).openStream());
			final JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            
            final String playerSlot = nameValue.get(nameValue.size() - 1).toString();
            final JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            
            return nameObject.get("name").toString();
      
		} catch (IOException | ParseException e) {
			return "error";
		}
		
	}
	
	public Gson getGson() {
		return gson;
	}

}
