/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package licensefilegenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 *
 * @author Dell790
 */
public class SerialNumberHelper {
    
    public static String getSystemMotherBoard_SerialNumber() throws Exception{
        
        if(OSHelper.isWindows()){
            return (getWindowsMotherboard_SerialNumber());
        }
        else if(OSHelper.isUnix()){
            return (GetLinuxMotherBoard_serialNumber());
        }
        else{
            throw new Exception("Your OS is not supported");
        }
    }
    
    public static String getSystemHardkisk_SerialNumber() throws Exception {
        if(OSHelper.isWindows()){
            return (getWindowsHarddisk_SerialNumber());
        }
        else if(OSHelper.isUnix()){
            return (GetLinuxHarddisk_serialNumber());
        }
        else{
            throw new Exception("Your OS is not supported");
        }
    }
    
    private static String getWindowsMotherboard_SerialNumber() throws Exception {
        String result = "";
        File file = File.createTempFile("realhowto",".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs =
        "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
          + "Set colItems = objWMIService.ExecQuery _ \n"
          + "   (\"Select * from Win32_BaseBoard\") \n"
          + "For Each objItem in colItems \n"
          + "    Wscript.Echo objItem.SerialNumber \n"
          + "    exit for  ' do the first cpu only! \n"
          + "Next \n";

        fw.write(vbs);
        fw.close();

        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
           result += line;
        }
        input.close();
        return result.trim();
    } 
    
    private static String GetLinuxMotherBoard_serialNumber() throws Exception {
        String command = "dmidecode -s baseboard-serial-number";
        String sNum = null; 
        Process SerNumProcess = Runtime.getRuntime().exec(command);
        BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
        sNum = sNumReader.readLine().trim();
        SerNumProcess.waitFor();
        sNumReader.close();
        
        return sNum; 
    }
    
    private static String getWindowsHarddisk_SerialNumber() throws Exception {
        String sc = "cmd /c" + "wmic diskdrive get serialnumber";

        Process p = Runtime.getRuntime().exec(sc);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        StringBuilder sb = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        } 

        return sb.substring(sb.toString().lastIndexOf("r") + 1).trim();
    } 
    
    private static String GetLinuxHarddisk_serialNumber() throws Exception {
        String sc = "/sbin/udevadm info --query=property --name=sda"; // get HDD parameters as non root user
        String[] scargs = {"/bin/sh", "-c", sc};

        Process p = Runtime.getRuntime().exec(scargs);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream())); 
        String line;
        StringBuilder sb  = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.indexOf("ID_SERIAL_SHORT") != -1) { // look for ID_SERIAL_SHORT or ID_SERIAL
                sb.append(line);
            }    
        }

        return sb.toString().substring(sb.toString().indexOf("=") + 1); 
    }
    
}

class OSHelper {
    
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

}
