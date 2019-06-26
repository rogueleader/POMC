import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import xGen.XPathGenerator;

public class Main {

	public static void main(String[] args) {
		
		 String DRIVER_PATH = null;
		 String URL = null;
		 String NAV = null;

		try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, Could not find config.properties. Check if it is present in src\\main\\resources ");
                return;
            }

            prop.load(input);
            
            // Driver Path
             DRIVER_PATH = prop.getProperty("pomc.driverpath");

    		// Base URL
    		 URL = prop.getProperty("pomc.url");
    		 
    		 //Navigation
    		 NAV = prop.getProperty("pomc.nav");
    		 

        } catch (IOException ex) {
            ex.printStackTrace();
        }
		
		
		// Give one or more items to click to reach a page ( if any )
		ArrayList<String> nav = new ArrayList<String>(Arrays.asList(NAV.split(",")));
				
		System.setProperty("webdriver.chrome.driver", DRIVER_PATH);

		XPathGenerator.generate(URL, nav); // will be later seeded with URL
		
		

	}

}
