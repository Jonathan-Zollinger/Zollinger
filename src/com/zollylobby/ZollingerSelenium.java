package com.zollylobby;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class ZollingerSelenium {

    public static void main(String[] args) {

        String downloadToMe = "C:\\Scripts";
        WebDriver driver = getHeadlessDriverWithSpecDownload(downloadToMe,"application/octet-stream");

        //navigate to website and click the win64 driver
        String url = "https://github.com/mozilla/geckodriver/releases";
        driver.get("https://github.com/mozilla/geckodriver/releases");
        System.out.printf("navigated to %s%n",url);
        String linkText = "geckodriver-v0.27.0-win64.zip";
        driver.findElement(By.linkText(linkText)).click();
        System.out.printf("downloaded %s file to %s%n",linkText,downloadToMe);

    }//end public static void main(String[] args)

    public static WebDriver getHeadlessDriverWithSpecDownload(String downloadFilepath,String mimeType) {

        String driverFile = "geckodriver-v0.27.0-win64.exe";
        System.setProperty("webdriver.gecko.driver", driverFile);
        FirefoxProfile profile = new FirefoxProfile();

        //set Firefox profile preferences
            /* it seems like you could get away without changing the browser.download.folderList preference to "2", but
            without it we can't change the browser.download.dir preference. */
        profile.setPreference("browser.download.folderList", 2);//uses last download directory as destination directory
        profile.setPreference("browser.download.dir",downloadFilepath); //if path = invalid, goes to downloads folder

        /* the second argument in these preferences is what's called a "mimetype" of a filetype.
        I find the easiest way to figure the proper mimetype for your use is to follow Florent B's suggestion
        and look at the network panel under the developer tools under a manual run through. https://bit.ly/2FEYB0p */
        profile.setPreference("browser.helperApps.neverAsk.openFile","application/octet-stream");
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream");

        //set Firefox options
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true); //run headless version of firefox

        //include profile in options
        options.setProfile(profile); //include all the profile settings in this option set

        //initiate new instance of firefox
        return new FirefoxDriver(options); //call the driver w/ our specified options (and profile).

    }//end public static WebDriver getHeadlessDriverWithSpecDownload(String downloadFilepath, String mimeType)

}//end public class ZollingerSelenium {
