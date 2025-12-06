package dev.hmap.services.scanner;

import dev.hmap.models.Host;
import dev.hmap.models.ScanResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Future;

public interface IPortScan {

    InetAddress resolveHost(String host) throws UnknownHostException;
    boolean isOpenPort(Host host, int port);
    ScanResult scan(Host host, int port);
    Future<ScanResult> scanAsync(Host host, int port);

    void shutdown();

}
