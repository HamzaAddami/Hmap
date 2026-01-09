package dev.hmap.service.scanner;

import dev.hmap.config.ThreadPoolManager;
import dev.hmap.enums.HostStatus;
import dev.hmap.model.Host;
import dev.hmap.utils.NetworkUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;

public class HostDiscoveryService  {

    private final ThreadPoolManager threadPoolManager;
    private final dev.hmap.service.scanner.HostService hostService;


    public HostDiscoveryService() {
        this.threadPoolManager = ThreadPoolManager.getInstance();
        this.hostService = new dev.hmap.service.scanner.HostService();
    }



    public boolean isReachable(InetAddress address, int timeout) {
        try {
            return address.isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    public void discoverHost(String subnet, Consumer<Host> onHostFound) {

        List<String> targets = NetworkUtils.getAddressesFromCidr(subnet);

        System.out.println("[*] Scanning : " + targets.size() + " addresses...");

        for (String target : targets) {
            threadPoolManager.executeNetworkTasks(() -> {
                try {
                    InetAddress addr = InetAddress.getByName(target);

                    long startTime = System.currentTimeMillis();
                    boolean reachable = isReachable(addr, 1000);
                    long latency = System.currentTimeMillis() - startTime;
                    if (reachable) {
                        Host host = new Host(addr);
                        host.setIpString(target);
                        host.setHostName(NetworkUtils.fetchHostname(addr));
                        host.setMacAddress(NetworkUtils.fetchMacAddress(addr));
                        host.setReachable(true);
                        host.setLatency(latency);
                        host.setStatus(HostStatus.UP);
                        onHostFound.accept(host);
                        hostService.registerHost(host);
                    }
                } catch (IOException e) {
                }
            });
        }
    }


    public void shutdown(){
        threadPoolManager.shutdownNow();
    }


}
