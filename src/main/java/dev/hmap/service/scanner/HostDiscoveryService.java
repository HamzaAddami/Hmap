package dev.hmap.service.scanner;

import dev.hmap.config.ThreadPoolManager;
import dev.hmap.enums.HostStatus;
import dev.hmap.model.Host;
import dev.hmap.utils.NetworkUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class HostDiscoveryService {

    private final ThreadPoolManager threadPoolManager;
    private final HostService hostService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public HostDiscoveryService() {
        this.threadPoolManager = ThreadPoolManager.getInstance();
        this.hostService = new HostService();
    }

    public boolean isReachable(InetAddress address, int timeout) {
        try {
            return address.isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    public void discoverHost(String subnet, Consumer<Host> onHostFound) {
        isRunning.set(true);
        List<String> targets = NetworkUtils.getAddressesFromCidr(subnet);

        System.out.println("[*] Scanning : " + targets.size() + " addresses...");

        for (String target : targets) {
            // Check if scan was stopped
            if (!isRunning.get()) {
                System.out.println("[*] Scan stopped by user");
                break;
            }

            threadPoolManager.executeNetworkTasks(() -> {
                // Double-check inside task
                if (!isRunning.get()) {
                    return;
                }

                try {
                    InetAddress addr = InetAddress.getByName(target);

                    long startTime = System.currentTimeMillis();
                    boolean reachable = isReachable(addr, 1000);
                    long latency = System.currentTimeMillis() - startTime;

                    if (reachable && isRunning.get()) {
                        Host host = new Host(addr);
                        host.setIpString(target);
                        host.setHostName(NetworkUtils.fetchHostname(addr));
                        host.setMacAddress(NetworkUtils.fetchMacAddress(addr));
                        host.setReachable(true);
                        host.setLatency(latency);
                        host.setStatus(HostStatus.UP);

                        // Only notify if still running
                        if (isRunning.get()) {
                            onHostFound.accept(host);
                            hostService.registerHost(host);
                        }
                    }
                } catch (IOException e) {
                    // Silently ignore unreachable hosts
                }
            });
        }
    }

    public void shutdown() {
        System.out.println("[*] Initiating graceful shutdown...");
        isRunning.set(false);

        // Create new instance for future scans
        // The ThreadPoolManager is singleton, so we just reset the flag
    }

    public void forceShutdown() {
        System.out.println("[!] Force shutdown initiated");
        isRunning.set(false);
        threadPoolManager.shutdownNow();
    }

    public boolean isRunning() {
        return isRunning.get();
    }
}