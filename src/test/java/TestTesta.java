import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import sun.security.util.Debug;

import java.io.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TestTesta {

    private static final String SCR_PATH = "D:\\Screenshots";
    private static final String TAG = "GoogleSearchTest";

    private Calendar calendar = Calendar.getInstance();
    private WebDriver webDriver;

    @Test
    public void TestGoogle(){
        System.setProperty("webdriver.chrome.driver","D:\\Programs\\chromedriver.exe");
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        JsonParse();
    }

    private void JsonParse(){
        JSONObject jsonObject = new JSONObject(readFile("test.json"));
        JSONArray jsonArray = jsonObject.getJSONArray("steps");

        String action;
        for (int i=0; i< jsonArray.length(); i++){
            JSONObject currentObject = jsonArray.getJSONObject(i);
            action = currentObject.getString("action");
            Debug.println(TAG, currentObject.getString("description"));
            if (action.equals("Screenshot")){
                TakeScreenShot(webDriver);
            }else {
                String params = currentObject.getString("params");

                if (action.equals("openUrl")) {
                    webDriver.get(params);
                } else if (action.equals("Click")) {
                    WebElement webElement = webDriver.findElement(By.xpath(params));
                    webElement.click();
                } else if (action.equals("setValue")) {
                    String[] input = currentObject.getString("description").split(" ");
                    WebElement webElement = webDriver.findElement(By.xpath(params));
                    webElement.sendKeys(input[input.length - 1]);
                } else if (action.equals("checkElementVisible")){
                    boolean isVisible = webDriver.findElement(By.xpath(params)).isDisplayed();
                    System.out.print("Element visibility: "+isVisible);
                } else {
                    System.out.println("Action type unknown");
                }
            }
        }
    }

    private String readFile(String filename){
        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            result = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void TakeScreenShot(WebDriver driver){
        File tempFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileHandler.copy(tempFile, new File(TestTesta.SCR_PATH +"\\screenshot_"+ calendar.getTimeInMillis()+".png" ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
