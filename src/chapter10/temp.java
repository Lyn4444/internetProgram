package chapter10;


import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @ProjectName internetProgram
 * @ClassName temp
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/15 12:41
 * @Version 1.0
 * @Function
 */

public class temp {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++ ) {
            System.out.println(devices[i].name);
            Pattern pattern = Pattern.compile("\\{(.*?)\\}");
            Matcher matcher = pattern.matcher(devices[i].name);
            while (matcher.find()) {
                String registry = matcher.group(0);
                int DRIVER_CLASS_ROOT = WinRegistry.HKEY_LOCAL_MACHINE;
                String DRIVER_CLASS_PATH = "SYSTEM\\CurrentControlSet\\Control\\Class";
                String NETCFG_INSTANCE_KEY = "NetCfgInstanceId";
                for (String driverClassSubkey : WinRegistry.readStringSubKeys(DRIVER_CLASS_ROOT, DRIVER_CLASS_PATH, 0)) {
                    for (String driverSubkey : WinRegistry.readStringSubKeys(DRIVER_CLASS_ROOT, DRIVER_CLASS_PATH + "\\" + driverClassSubkey, 0)) {
                        String path = DRIVER_CLASS_PATH + "\\" + driverClassSubkey + "\\" + driverSubkey;
                        String netCfgInstanceId = WinRegistry.readString(DRIVER_CLASS_ROOT, path, NETCFG_INSTANCE_KEY, 0);
                        String theDriverName = "";
                        if (netCfgInstanceId != null && netCfgInstanceId.equalsIgnoreCase(registry)) {
                            theDriverName = trimOrDefault(WinRegistry.readString(DRIVER_CLASS_ROOT, path, "DriverDesc", 0), "");
                            System.out.println(theDriverName);
                            break;
                        }
                    }
                }
            }
            System.out.println("______________");
        }
    }

    private final static String trimOrDefault (String str, String def) {
        str = (str == null) ? "" : str.trim();
        return str.isEmpty() ? def : str;
    }
}
