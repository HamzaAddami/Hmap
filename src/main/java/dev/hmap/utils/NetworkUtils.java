package dev.hmap.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {
    public static List<String> getAddressesFromCidr(String cidr) {
        List<String> ips = new ArrayList<>();
        String[] parts = cidr.split("/");
        String ip = parts[0];
        int prefix = Integer.parseInt(parts[1]);

        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            int addressInt = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

            int mask = 0xFFFFFFFF << (32 - prefix);
            int startIp = addressInt & mask;
            int endIp = startIp | ~mask;

            for (int i = startIp + 1; i < endIp; i++) {
                ips.add(String.format("%d.%d.%d.%d",
                        (i >> 24) & 0xFF, (i >> 16) & 0xFF,
                        (i >> 8) & 0xFF, i & 0xFF));
            }
        } catch (UnknownHostException e) {}
        return ips;
    }

    public static String fetchMacAddr(InetAddress address){
        return null;
    }
}
