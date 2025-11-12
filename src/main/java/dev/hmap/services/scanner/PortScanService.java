package dev.hmap.services.scanner;

import dev.hmap.models.Port;

import java.net.Socket;

public class PortScanService {

    private Port port;
    public PortScanService(Port port){
        this.port = port;
    }


}
