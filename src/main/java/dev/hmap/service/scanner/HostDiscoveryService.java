package dev.hmap.service.scanner;

import dev.hmap.config.ThreadPoolManager;
import dev.hmap.enums.OsFamily;
import dev.hmap.model.Host;
import dev.hmap.service.scanner.base.IHostDiscovery;
import dev.hmap.utils.PortGeneator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HostDiscoveryService implements IHostDiscovery {

    private final ThreadPoolManager threadPoolManager;

    public HostDiscoveryService(){
        this.threadPoolManager = ThreadPoolManager.getInstance();
    }


    @Override
    public boolean isReachable(InetAddress address, int timeout) {
        try{
            return address.isReachable(timeout);
        }catch (IOException e){
            return false;
        }
    }

    @Override
    public boolean isReachableByTCP(InetAddress address, int timeout){
        for(int port : PortGeneator.COMMON_PORTS){
            try(Socket socket = new Socket()){
                socket.connect(new InetSocketAddress(address, port), timeout);
            }catch (IOException i){}
        }
        return false;
    }

    @Override
    public Host discoverHost(InetAddress address, int timeout) {

        Host host = new Host(address);
        long startTime = System.currentTimeMillis();

        boolean reachable = isReachable(address, timeout);

        if (!reachable) {
            reachable = isReachableByTCP(address, timeout);
        }

        if (reachable) {
            host.setLatency(System.currentTimeMillis() - startTime);
            host.setActive();
            host.setReachable(true);
        } else {
            host.setInactive();
            host.setReachable(false);
        }

        host.setScanDate(LocalDateTime.now());
        return host;

    }


    @Override
    public List<Host> scanNetwork(int cidr, int timeout) {
        List<Host> discoveredHosts = new ArrayList<>();
        List<Future<Host>> futures = new ArrayList<>();
        try{
            String localIp = InetAddress.getLocalHost().getHostAddress();
            String subnet = localIp.substring(0, localIp.lastIndexOf("."));

            for(int i = 0; i < 255; i++){
                String targetIp = subnet + "." + i;
                InetAddress address = InetAddress.getByName(targetIp);
                Future<Host> future = (Future<Host>) threadPoolManager.executeNetworkTasks(() -> discoverHost(address, timeout));
                futures.add(future);
            }

            for(Future<Host> f : futures){
                try{
                    Host h = f.get(timeout + 500, TimeUnit.SECONDS);
                    if(h.isActive()){
                        discoveredHosts.add(h);
                    }
                }catch (Exception i){}
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return discoveredHosts;
    }

    @Override
    public List<Host> scanNetwork(InetAddress gateway, String subnetMask, int timeout) {
        return List.of();
    }

    @Override
    public OsFamily detectOs(InetAddress address) {
        return null;
    }

    @Override
    public Host fullDiscovery(InetAddress address, int timeout) {
        Host host = discoverHost(address, timeout);
        return host;
    }
}
