package dev.hmap.service.scanner;

import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;

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
