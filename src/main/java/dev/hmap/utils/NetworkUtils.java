package dev.hmap.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    private List<InetAddress> getAddressesFromCidr(String cidr) throws UnknownHostException {
        List<InetAddress> adresses = new ArrayList<>();

        String[] parts = cidr.split("/");
        String ipString = parts[0];
        int prefix = Integer.parseInt(parts[1]);

        InetAddress ia = InetAddress.getByName(ipString);
        byte[] addressBytes = ia.getAddress();

        // Calcul du masque de sous-réseau
        int mask = 0xffffffff << (32 - prefix);
        int ipInt = 0;
        for (byte b : addressBytes) {
            ipInt = (ipInt << 8) | (b & 0xff);
        }

        int networkAddress = ipInt & mask;
        int broadcastAddress = networkAddress | (~mask);

        // On boucle de l'adresse réseau + 1 jusqu'à broadcast - 1
        for (int i = networkAddress + 1; i < broadcastAddress; i++) {
            byte[] currentIpBytes = new byte[] {
                    (byte) ((i >> 24) & 0xff),
                    (byte) ((i >> 16) & 0xff),
                    (byte) ((i >> 8) & 0xff),
                    (byte) (i & 0xff)
            };
            adresses.add(InetAddress.getByAddress(currentIpBytes));
        }
        return adresses;
    }
}
