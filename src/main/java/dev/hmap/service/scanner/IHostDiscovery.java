package dev.hmap.service.scanner;

import dev.hmap.enums.OsFamily;
import dev.hmap.model.Host;
import java.net.InetAddress;
import java.util.List;

public interface IHostDiscovery {

    boolean isReachable(InetAddress address, int timeout);
    Host discoverHost(InetAddress address, int timeout);
    List<Host> scanNetwork(int cidr, int timeout);
    List<Host> scanNetwork(InetAddress gateway,String subnetMask, int timeout);
    OsFamily detectOs(InetAddress address);
    Host fullDiscovery(InetAddress address, int timeout);

}
