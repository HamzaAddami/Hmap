package dev.hmap.services.scanner;

import dev.hmap.models.Host;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Future;

public class HostDiscoveryService implements IHostDiscovery{

    private final List<Host> hostList;
    private Future<?> scanPool;
    public HostDiscoveryService(List<Host> hostList){
        this.hostList = hostList;
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
    public Host discoverHost(InetAddress address, int timeout) {
        return null;
    }

    @Override
    public List<Host> scanNetwork(int cidr, int timeout) {
        return List.of();
    }

    @Override
    public List<Host> scanNetwork(InetAddress gateway, String subnetMask, int timeout) {
        return List.of();
    }

    @Override
    public Host.OsFamily detectOs(InetAddress address) {
        return null;
    }

    @Override
    public Host fullDiscovery(InetAddress address, int timeout) {
        return null;
    }
}
