package xGen;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import jWriter.JSONWriter;

public class XPathGenerator {

	static WebDriver wd = new ChromeDriver();

	public static void waitForPageLoaded(WebDriver webDriver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeAsyncScript(
						"var callback = arguments[arguments.length - 1];" + "if (document.readyState !== 'complete') {"
								+ "  callback('document not ready');" + "} else {" + "  try {"
								+ "    var testabilities = window.getAllAngularTestabilities();"
								+ "    var count = testabilities.length;" + "    var decrement = function() {"
								+ "      count--;" + "      if (count === 0) {" + "        callback('complete');"
								+ "      }" + "    };" + "    testabilities.forEach(function(testability) {"
								+ "      testability.whenStable(decrement);" + "    });" + "  } catch (err) {"
								+ "    callback(err.message);" + "  }" + "}")
						.toString().equals("complete");
			}
		};
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 10);
			wait.until(expectation);
		} catch (Throwable error) {
			new Exception("Timeout waiting for Page Load Request to complete.");
		}
	}

	public static void generate(String URL, ArrayList<String> nav) {

		System.out.println("______ ________  ___  _____ ______ _____  ___ _____ ___________ \r\n"
				+ "| ___ \\  _  |  \\/  | /  __ \\| ___ \\  ___|/ _ \\_   _|  _  | ___ \\\r\n"
				+ "| |_/ / | | | .  . | | /  \\/| |_/ / |__ / /_\\ \\| | | | | | |_/ /\r\n"
				+ "|  __/| | | | |\\/| | | |    |    /|  __||  _  || | | | | |    / \r\n"
				+ "| |   \\ \\_/ / |  | | | \\__/\\| |\\ \\| |___| | | || | \\ \\_/ / |\\ \\ \r\n"
				+ "\\_|    \\___/\\_|  |_/  \\____/\\_| \\_\\____/\\_| |_/\\_/  \\___/\\_| \\_|\r\n"
				+ "                                                                ");

		Instant start = Instant.now();

		wd.manage().window().maximize();

		wd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // IMplicit Wait by using timeouts() on webdriver

		wd.get(URL);

		waitForPageLoaded(wd);

		Iterator<String> it = nav.iterator();

		if (it.hasNext()) {
			while (it.hasNext()) {
				String ix = (String) it.next();

				if (ix != null && !ix.equals(""))
					wd.findElement(By.xpath(ix)).click();

			}

		}

		waitForPageLoaded(wd);

		System.out.println("\nPage Loaded... Generating XPath for "+wd.getCurrentUrl());

		List<WebElement> eList = wd.findElements(By.cssSelector("*"));

		LinkedHashMap<String, LinkedHashMap<String, String>> xMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();

		// Limiting the generation for selected tags - 'INPUT', 'BUTTON', 'SELECT',
		// 'TEXTAREA' ---- REMOVED 'A'

		for (WebElement e : eList) {
			if (Stream.of("INPUT", "BUTTON", "SELECT", "TEXTAREA").anyMatch(e.getTagName()::equalsIgnoreCase)) {
				if (e.getTagName().equalsIgnoreCase("input") && e.getAttribute("type").equalsIgnoreCase("checkbox")
						&& e.getAttribute("id").equals(""))
					continue;
				else {
					String n = e.getAttribute("name");

					String id = e.getAttribute("id");

					String l = e.getAttribute("label");

					// Adding filter for ID > NAME > LABEL

					if ((n != null && !n.equals("")) || (id != null && !id.equals(""))
							|| (l != null && !l.equals(""))) {
						String x = generateXpath(e);

						
						if (e.getAttribute("type").equalsIgnoreCase("checkbox")) {
							x = x.split("tbody\\[1\\]")[1].split("\\/div\\[1\\]")[0].replaceAll("/", "//");
						}
						if(e.getTagName().equalsIgnoreCase("input")&&e.getAttribute("type")!=null&&e.getAttribute("type").equalsIgnoreCase("submit"))
						{
							if (xMap.containsKey("button"))
								xMap.get("button").put(
										((id != null && !id.equals("")) ? id : (n != null && !n.equals("")) ? n : l), x);
							else {
								xMap.put("button", new LinkedHashMap<String, String>());
								xMap.get("button").put(
										((id != null && !id.equals("")) ? id : (n != null && !n.equals("")) ? n : l), x);
							}
						}
						else if (xMap.containsKey(e.getTagName()))
							xMap.get(e.getTagName()).put(
									((id != null && !id.equals("")) ? id : (n != null && !n.equals("")) ? n : l), x);
						else {
							xMap.put(e.getTagName(), new LinkedHashMap<String, String>());
							xMap.get(e.getTagName()).put(
									((id != null && !id.equals("")) ? id : (n != null && !n.equals("")) ? n : l), x);
						}

					}
				}
			}
		}

//		xMap.forEach((k, v) -> System.out.println(k + "     " + v));
		// Map not allowing duplicate values as keys so skipping tags

		Instant finish = Instant.now();

		long timeElapsed = Duration.between(start, finish).getSeconds(); // in millis

		String cURL = "locators";
		
		String wdcURL = wd.getCurrentUrl();
		
		if(wdcURL.length() - wdcURL.replaceAll("/","").length()>=3)
			 cURL = wdcURL.split("/")[3];

		System.out.println("\nTotal XPath generated : " + xMap.values().stream().mapToInt(LinkedHashMap::size).sum()
				+ " in " + timeElapsed + " seconds.\n ");

		wd.close();

		JSONWriter.writer(xMap, cURL); // Writing to JSON file
		
		
		

	}

	@SuppressWarnings("unchecked")
	public static String generateXpath(WebElement e) {

		if (e.getAttribute("id") != null && !e.getAttribute("id").equals("")
				&& !e.getAttribute("id").contains("Checkbox"))
			return "//" + e.getTagName() + "[@id='" + e.getAttribute("id") + "']";
		if (e.getTagName().equals("html"))
			return "/html[1]";

		int ctr = 0;

		List<WebElement> siblings = (List<WebElement>) ((JavascriptExecutor) wd)
				.executeScript("return arguments[0].parentNode.childNodes;", e);

		WebElement parent = (WebElement) ((JavascriptExecutor) wd).executeScript("return arguments[0].parentNode;", e);

		for (int i = 0; i < siblings.size(); i++) {

			WebElement sibling = null;

			if (siblings.get(i) instanceof WebElement) {
				sibling = siblings.get(i);

				Long nodeType = (Long) ((JavascriptExecutor) wd).executeScript("return arguments[0].nodeType;",
						sibling);

				if (sibling.equals(e))
					return generateXpath(parent) + '/' + e.getTagName().toLowerCase() + '[' + (ctr + 1) + ']';

				if (nodeType == 1 && sibling.getTagName().equalsIgnoreCase(e.getTagName().toLowerCase()))
					ctr++;
			}

		}

		return null;

	}
}

/*
 * 
 * This piece of code will print the entire DOM to console
 * 
 * String javascript = "return arguments[0].innerHTML"; String
 * pageSource=(String)((JavascriptExecutor)wd) .executeScript(javascript,
 * wd.findElement(By.tagName("html"))); pageSource = "<html>"+pageSource
 * +"</html>"; System.out.println(pageSource);
 * 
 * 
 * 
 */
