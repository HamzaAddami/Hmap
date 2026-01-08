package dev.hmap.service.scanner.impl;

import dev.hmap.config.ThreadPoolManager;
import dev.hmap.enums.HostStatus;
import dev.hmap.model.Host;
import dev.hmap.service.scanner.base.HostDiscovery;
import dev.hmap.service.scanner.base.HostService;
import dev.hmap.utils.NetworkUtils;
import dev.hmap.utils.PortGenerator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class HostDiscoveryServiceImpl implements HostDiscovery {

    private final ThreadPoolManager threadPoolManager;
    private final HostService hostService;


    public HostDiscoveryServiceImpl() {
        this.threadPoolManager = ThreadPoolManager.getInstance();
        this.hostService = new HostServiceImpl();
    }


    @Override
    public boolean isReachable(InetAddress address, int timeout) {
        try {
            return address.isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isReachableByTCP(InetAddress address, int timeout) {
        for (int port : PortGenerator.COMMON_PORTS) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(address, port), timeout);
                return true;
            } catch (IOException e) {

            }
        }
        return false;
    }

    @Override
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
                        host.setHostName(fetchHostname(addr));
                        host.setMacAddress(fetchMacAddress(addr));
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

    private String fetchHostname(InetAddress addr) {
        String canonical = addr.getCanonicalHostName();
        return (canonical.equals(addr.getHostAddress())) ? "Unknown" : canonical;
    }
    private String fetchMacAddress(InetAddress addr) {

        try {
            Process process = Runtime.getRuntime().exec("arp -a " + addr.getHostAddress());
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    if (token.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})")) {
                        return token.toUpperCase();
                    }
                }
            }
        } catch (Exception e) { return "00:00:00:00:00:00"; }
        return "Not Found";
    }

    @Override
    public void shutdown(){
        threadPoolManager.shutdownNow();
    }


}
