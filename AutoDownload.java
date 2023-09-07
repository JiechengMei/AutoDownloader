import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoDownload {
    public static void main(String[] args) throws IOException {

        //handle the download location (pass)
        String storagePath = "/Users/jcmbp/Desktop/ipro/test/";
        System.setProperty("webdriver.chrome.driver", "/Users/jcmbp/Desktop/Java Library/chromedriver");

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", storagePath);
        ChromeOptions options = new ChromeOptions().addArguments("--headless");
        options.setExperimentalOption("prefs", chromePrefs);
        WebDriver driver = new ChromeDriver(options);

        //global variable (pass)
        String SUrl = "https://xeno-canto.org/explore?query="; //basic species url structure
        String DUrl_h = "https://xeno-canto.org/"; //basic download page structure head
        String DUrl_t = "/download"; //basic download page structure tail

        String sUrl; //use for species page
        String dUrl; //use for download page
        String species;


        // import the species list into an arraylist (pass)
        BufferedReader br = new BufferedReader(new FileReader("species_list.txt"));
        List<String> speciesList = new ArrayList<>();

        //take out the species name
        String info_i;
        while ((info_i = br.readLine()) != null) {
            String[] info = info_i.split("_");
            speciesList.add(info[0].replaceAll("\\s+", "%20"));
        }
        ;
        System.out.println("Size of the speciesList: " + speciesList.size());


        // connect to the page (pass)
        //should under a loop
        String sid = "";
        String[] stype;
        for (int x = 0; x < 100; x++) {
            System.out.println("Current x:"+x);
            try {
                String scPath = storagePath + speciesList.get(x).replaceAll("%20", " ") + "_call";
                String stPath = storagePath + speciesList.get(x).replaceAll("%20", " ") + "_test";
                new File(scPath).mkdirs();
                new File(stPath).mkdirs();


                species = speciesList.get(x);
                sUrl = SUrl + species;
                driver.get(sUrl);

//getting information
                WebElement content = driver.findElement(By.id("content-area"));
                WebElement table = content.findElement(By.className("results"));
                WebElement tbody = table.findElement(By.tagName("tbody"));
                List<WebElement> tr = tbody.findElements(By.tagName("tr"));
                //all information goes into an array


                for (int i = 0; i < tr.size(); i++) {
//none sample file
                    stype = tr.get(i).findElements(By.tagName("td")).get(9).getText().split("\\s+");
                    if (stype.length > 1) {
                        sid = tr.get(i).findElements(By.tagName("td")).get(12).getText().replaceAll("[^0-9]", "");
                        dUrl = DUrl_h + sid + DUrl_t;
                        DownloadAudio(dUrl, sid, stPath);
                        System.out.println("Current species: " + (x) + " Downloaded === " + species.replaceAll("%20", " ") + " S_ID: " + sid + " S_TYPE: test ===> Local ");
                    }
                }

//sample file

                for (int i = 0; i < tr.size(); i++) {
                    stype = tr.get(i).findElements(By.tagName("td")).get(9).getText().split("\\s+");
                    if (stype.length == 1 && stype[0].equalsIgnoreCase("call")) {
                        sid = tr.get(i).findElements(By.tagName("td")).get(12).getText().replaceAll("[^0-9]", "");
                        dUrl = DUrl_h + sid + DUrl_t;
                        DownloadAudio(dUrl, sid, scPath);
                        System.out.println("Current species: " + (x) + " Downloaded === " + species.replaceAll("%20", " ") + " S_ID: " + sid + " S_TYPE: call ===> Local ");
                    }
                }
            } catch (Exception e) {
                System.out.println(Color.YELLOW + "ERROR! Species #" + x + " NOT FOUND");
            }
        }

        System.out.println("Program End");
        driver.quit();
    }

    private static void DownloadAudio(String dUrl, String sid, String Path) {
        System.setProperty("webdriver.chrome.driver", "/Users/jcmbp/Desktop/Java Library/chromedriver");
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", Path);
        ChromeOptions options = new ChromeOptions().addArguments("--headless");
        options.setExperimentalOption("prefs", chromePrefs);
        WebDriver driver = new ChromeDriver(options);
        driver.get(dUrl);

        ArrayList<String> aid = UpdateListLocal(Path);
        File folder = new File(Path);
        File[] files = folder.listFiles();
        while (!aid.contains(sid)) {
            aid = UpdateListLocal(Path);
        }
        driver.close();
    }

    private static ArrayList<String> UpdateListLocal(String Path) {
        ArrayList<String> aid = new ArrayList<>();
        File folder = new File(Path);
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
//#128 will trigger bug, see if the download file endswith before fine turn this line
            if (files[i].getName().endsWith(".mp3") || files[i].getName().endsWith(".wav")) {
                String[] temp = files[i].getName().split("\\s+-\\s+");
                aid.add(temp[0].replaceAll("[^0-9]", ""));
            }
        }
        return aid;
    }


}