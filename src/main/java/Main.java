import java.util.LinkedHashMap;

import jWriter.JSONWriter;
import xGen.XPathGenerator;;

public class Main {
	
	public static void main(String[] args) {
		
		LinkedHashMap<String, LinkedHashMap<String, String>> x = XPathGenerator.generate();  // will be later seeded with URL
		
		JSONWriter.writer(x);
		
		
	}

}
