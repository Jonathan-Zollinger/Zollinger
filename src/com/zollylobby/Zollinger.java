package com.zollylobby;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zollinger {

    public static WebDriver getHeadlessDriverWithSpecDownload(
            String DriverPath, String downloadFilepath,String mimeType) {

        String driverFile = DriverPath;
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
        profile.setPreference("browser.helperApps.neverAsk.openFile",mimeType);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",mimeType);

        //set Firefox options
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true); //run headless version of firefox

        //include profile in options
        options.setProfile(profile); //include all the profile settings in this option set

        //initiate new instance of firefox
        return new FirefoxDriver(options); //call the driver w/ our specified options (and profile).

    }//end public static WebDriver getHeadlessDriverWithSpecDownload(String downloadFilepath, String mimeType)

    public static void installGeckoDriver(String driverFile){
        /*
        this method is kinda useless.
        this method verifies geckodriver install, and installs if it isn't installed.
        this method installs the geckodriver w/o any browser, so that the browser can be used by others.
        it's assumed that the gecko driver needed is version 27 --> https://mzl.la/33RH9jq
        */

        String ffoxDriverLinux =
                "https://github.com/mozilla/geckodriver/releases/download/v0.27.0/geckodriver-v0.27.0-linux64.tar.gz";
        String ffoxDriverWin64 =
                "https://github.com/mozilla/geckodriver/releases/download/v0.27.0/geckodriver-v0.27.0-win64.zip";
        String zippedDriver = null;
        String thisOS = System.getProperty("os.name");


        //download the driver if the OS is linux
        if (thisOS.equalsIgnoreCase("Linux")){
            validateDirectory(getDirectory(driverFile));
            sendCmdlet("/bin/bash cd /Downloads/");
            sendCmdlet("wget " + ffoxDriverLinux);
            zippedDriver = getDirectory(driverFile) + "geckodriver-v0.27.0-linux64.tar.gz";


            //download the driver if the OS is windows
        }else if(thisOS.startsWith("Windows")){
            sendCmdlet("cmd /C start " + ffoxDriverWin64);

            //wait for the file to download to continue
            Path source = Paths.get(System.getenv(
                    "USERPROFILE") + "/Downloads/geckodriver-v0.27.0-win64.zip");
            try {
                awaitfile(source, 100000000);
            }catch (Exception e){
                System.out.print("wait failed");
            }//end try-catch awaitfile()

            //verify the directory path is kosher. If it's not, make it
            validateDirectory(driverFile);

            //move the zipped driver to the correct directory. https://bit.ly/33UGHAD and https://bit.ly/2Fg7soZ
            Path target = Paths.get("C:/Scripts/webdrivers/geckodriver-v0.27.0-win64.zip");
            try {
                Files.move(source, target);
            } catch (Exception e) {
                System.out.printf("attempted file move from %s to %s failed\n", source, target);
                e.printStackTrace();
            }//end try-catch Files.move(source,target)

            //unzip file
            zippedDriver = getDirectory(driverFile) + "geckodriver-v0.27.0-win64.zip";
            try {
                unzip(zippedDriver,driverFile);
            } catch (Exception e) {
                System.out.printf("attempted unzip of %s failed%n",zippedDriver);
                e.printStackTrace();
            }//end try-catch InputStream is = new GZIPInputStream(new FileInputStream(driverFile))

            //move now unzipped file to the /scripts/webdrivers folder
            source = Paths.get("C:/Scripts/webdrivers/geckodriver-v0.27.0.exe/geckodriver.exe");
            target = Paths.get("C:/Scripts/webdrivers/geckodriver.exe");
            try {
                Files.move(source, target,StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.printf("attempted file move from %s to %s failed\n", source, target);
                e.printStackTrace();
            }//end try-catch Files.move(source, target)

            //rename that same file
            source = target;
            target = Paths.get(driverFile);
            try {
                Files.move(source, target,StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.printf("attempted file move from %s to %s failed\n", source, target);
                e.printStackTrace();
            }

            //delete extra folders from unzip and moves https://bit.ly/3iIbLIh



        }else{
            System.out.println("Fatal Error: This program is not yet designed to work with your OS.");
        }//end if (thisOS.equalsIgnoreCase("Linux")


    }//end public static void installGeckoDriver()

    public static void sendCmdlet(String command){

        //define variables
        String[] commandArray = command.split(" ");
        String cmdlet = commandArray[2];
        String line = "";

        /*
        send the command to the terminal - this page helped a lot when writing this: https://bit.ly/33PZjlB
        be sure to dictate whether the terminal should stay open on windows with the cmd interpreter
                https://bit.ly/3gUhHgH
        */
        try {
            Process proc = Runtime.getRuntime().exec(command);

            // Read the output
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.print(line + "\n");
                }// end while ((line = reader.readLine()) != null)
            }catch (Exception e){
                System.out.printf("failed to read input stream from %s%n",cmdlet);
            }// end try-catch while ((line = reader.readLine()) != null)

            try {
                proc.waitFor();
            }catch (Exception e){
                System.out.printf("proc.waitfor() of %s failed%n",cmdlet);
            }//end try-catch proc.waitFor();

        }catch (Exception e){
            System.out.printf("attempted cmdlet \"%s\" failed%n",cmdlet);
        }//end Process proc = Runtime.getRuntime().exec(command)
    }// end  public static void sendCmdlet(String command)

    public static void downloadWithSelenium(String url, String filelocation){


        String dir = getDirectory(filelocation);
        validateDirectory(filelocation);

        File f = new File(filelocation);
        //if the file doesn't exist, download it to this file.
        if (!f.isFile()){ //https://bit.ly/33KZAGd
            org.openqa.selenium.firefox.FirefoxProfile fxProfile;
            fxProfile = new FirefoxProfile();

            //designate download location
            fxProfile.setPreference("browser.download.folderList",2);
            fxProfile.setPreference("browser.download.manager.showWhenStarting",false);
            fxProfile.setPreference("browser.download.dir",dir);
            fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream");
            //https://bit.ly/31DdJmn
            //return most of the downloads settings to original settings
            if (System.getProperty("os.name").equalsIgnoreCase("windows 10")){
                fxProfile.setPreference("browser.download.dir","c:/Downloads");
            }else if (System.getProperty("os.name").equalsIgnoreCase("linux")){
                fxProfile.setPreference("browser.download.dir","/Downloads");
            }//end if os = windows 10 or linux

            fxProfile.setPreference("browser.download.manager.showWhenStarting",true);
        }//end if (!f.isFile())
    }//end public static void download(String url, String filelocation

    public static String getDirectory(String fileName){
        /*
        this method takes a filename and returns its working directory
        */

        String dir = ""; //this will be the string of the directory
        String[] directories = fileName.split("/");
        for (int i = 0; i < directories.length-1; i++){
            dir = dir + directories[i] +"/";
        }// end for (int i = 0; i < directories.length-1; i++)
        return dir;
    }//end public static String getDirectory(String fileName)

    public static void validateDirectory(String fileName){
        /*
        this method looks to see if a directory of a destination is created, if it's not, it creates it.
        */
        String dir = getDirectory(fileName);

        //designate file and folder from string
        File directory = new File(dir);
        if (!directory.exists()){
            System.out.printf("the directory %s doesn't exist, creating %s directory%n",directory,directory);
            boolean result = false;

            try{
                directory.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.printf("Error, attempted creation of %s directory failed%n",directory);
            }// end try-catch directory.mkdir()
            if(result) {
                System.out.printf("the directory %s was successfully created%n",directory);
            }// end if(result)
        }else{
            System.out.printf("the directory %s already exists, no further action performed\n",directory);
        }// end if (!directory.exists())
    }//end public static void validateFile(String fileName)

    public static BasicFileAttributes awaitfile(Path target, long timeout) // https://bit.ly/2XWmeYr
            throws IOException, InterruptedException{
        final Path name = target.getFileName();
        final Path targetDir = target.getParent();

        // If path already exists, return early
        try {
            return Files.readAttributes(target, BasicFileAttributes.class);
        } catch (Exception e) {}

        final WatchService watchService = FileSystems.getDefault().newWatchService();
        try {
            final WatchKey watchKey = targetDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            // The file could have been created in the window between Files.readAttributes and Path.register
            try {
                return Files.readAttributes(target, BasicFileAttributes.class);
            } catch (NoSuchFileException ex) {}
            // The file is absent: watch events in parent directory
            WatchKey watchKey1 = null;
            boolean valid = true;
            do {
                long t0 = System.currentTimeMillis();
                watchKey1 = watchService.poll(timeout, TimeUnit.MILLISECONDS);
                if (watchKey1 == null) {
                    return null; // timed out
                }
                // Examine events associated with key
                for (WatchEvent<?> event: watchKey1.pollEvents()) {
                    Path path1 = (Path) event.context();
                    if (path1.getFileName().equals(name)) {
                        return Files.readAttributes(target, BasicFileAttributes.class);
                    }
                }
                // Did not receive an interesting event; re-register key to queue
                long elapsed = System.currentTimeMillis() - t0;
                timeout = elapsed < timeout? (timeout - elapsed) : 0L;
                valid = watchKey1.reset();
            } while (valid);
        } finally {
            watchService.close();
        }

        return null;
    }// end public static BasicFileAttributes awaitfile(Path target, long timeout)

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }//end try catch unzip

    }//end private static void unzip(String zipFilePath, String destDir)

}//end public class Zollinger
