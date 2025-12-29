package dev.hmap.service.scanner.base;

import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;
import dev.hmap.enums.ScanType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Future;

public interface IPortScan {
    InetAddress resolveHost(String host) throws UnknownHostException;
    Future<ScanResult> scanAsync(Host host, List<Integer> ports, ScanType scanType);
    void shutdown();

}
