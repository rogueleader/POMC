package modelCreator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import org.antlr.stringtemplate.StringTemplate;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class POMCreator {

	public static void pomGenerateByFileName(String fileName) {

		String path = System.getProperty("user.dir");
	
		try {
			POMCreator stHelper = new POMCreator();

			InputStream is = new FileInputStream(path + "\\src\\main\\resources\\strtemplate.stg");
			@SuppressWarnings("resource")
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			String fileAsString = sb.toString();

			StringTemplate strTemplate = new StringTemplate(fileAsString);

			String resContent = stHelper.processTemplate(strTemplate, fileName, path);

			FileOutputStream out = new FileOutputStream(path + "\\" + fileName + ".ts");
			out.write(resContent.getBytes());
			out.close();
			System.out.println("\nPOM File Created Successfully - "+fileName + ".ts");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	

	public String processTemplate(StringTemplate strTemplate, String fileName, String path) {

		strTemplate.setAttribute("fileName", fileName);
		strTemplate.setAttribute("ClassName", fileName);
		
		// JSON parser object to parse read file
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(path + "\\" + fileName + ".json")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonElement jelement = new JsonParser().parse(content);
		JsonObject jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("button");
		StringBuilder sb = new StringBuilder();

		Set<Map.Entry<String, JsonElement>> entries = jobject.entrySet();// will return members of your object
		for (Map.Entry<String, JsonElement> entry : entries) {
			
			String strFun ="\n	public async clickAt" + entry.getKey() + "Button() \n    {";
			strFun += "\n       await element(by.xpath(" + entry.getValue() + ")).click(); \n";
			strFun += "    }\n";

			sb.append(strFun);
			
		}
		
		strTemplate.setAttribute("buttonFunction", sb.toString());

		return strTemplate.toString();

	}
	
	
	
}
