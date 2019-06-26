package jWriter;

import java.io.FileWriter;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import modelCreator.POMCreator;

public class JSONWriter {

	@SuppressWarnings("null")
	public static void writer(LinkedHashMap<String, LinkedHashMap<String, String>> x, String fileName) {

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

		String uglyJSONString = gson.toJson(x);

		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);

		try {
			if (fileName != null || !fileName.equals(""))
				fileName = fileName.replaceAll("[^\\w\\s]", "");

			FileWriter fw = new FileWriter("./" + fileName + ".json");
			fw.write(prettyJsonString);
			fw.close();
		}

		catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("JSON file created - " + fileName + ".json");
		// create POM File in TS format
		POMCreator.pomGenerateByFileName(fileName);
	}

}
