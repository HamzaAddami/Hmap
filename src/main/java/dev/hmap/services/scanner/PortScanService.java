package dev.hmap.services.scanner;

import dev.hmap.models.Port;

import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.Future;

public class PortScanService {

    private static final int[] COMMON_PORTS =
            {20, 21, 22, 23, 25, 53, 80, 110, 143, 443,
             445,3306, 3389, 5432, 5900, 8080, 27017};

    private static final int[] WELL_KNOWN_PORTS = generateRangePorts(1, 1024);


    private static int[] generateRangePorts(int start, int end){
        int[] result = new int[end - start + 1];
        for (int i=0; i<=result.length; i++){
            result[i] = start +1;
        }
        return result;
    }









    private Future<?> portScanner;
    private Port port;

    public PortScanService(Port port){
        this.port = port;
    }





}
