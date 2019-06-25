package jWriter;

import java.io.FileWriter;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JSONWriter {

	public static void writer(LinkedHashMap<String, LinkedHashMap<String, String>> x) {

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		String uglyJSONString = gson.toJson(x);
		
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);
		
		
		 try{    
	           FileWriter fw=new FileWriter("./locators.json");    
	           fw.write(prettyJsonString);    
	           fw.close();    
	          }
		 
		 catch(Exception e){System.out.println(e);}    
	          System.out.println("JSON file created !");    
	     }    

	}
