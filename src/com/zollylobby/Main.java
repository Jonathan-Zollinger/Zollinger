package com.zollylobby;

import java.io.File;


public class Main {

    public static void main(String[] args) {

        //installgeckodriver() downloads, unzips and places the v0.27.0 geckodriver in your c:/Scripts/Webdriver folder
        // right now that file location is hardcoded into the installGeckoDriver() method, but will be updated.
        String driverFile = "C:/Scripts/webdrivers/geckodriver-v0.27.0.exe";
        if (!new File(driverFile).isFile()){
            Zollinger.installGeckoDriver(driverFile);

        }// if (!new File(driverFile).isFile())

    }
}
