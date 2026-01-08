package dev.hmap.service.scanner.base;

import dev.hmap.enums.OsFamily;
import dev.hmap.model.Host;
import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;

public interface HostDiscovery {

    boolean isReachable(InetAddress address, int timeout);
    boolean isReachableByTCP(InetAddress address, int timeout);
    void discoverHost(String subnet, Consumer<Host> onHostFound);
    void shutdown();
}
