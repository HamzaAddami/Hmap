package dev.hmap.services.task;

import dev.hmap.models.Host;
import dev.hmap.models.Port;
import dev.hmap.services.scanner.IPortScan;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class PortScanTask implements Callable<Port> {

    private static final int timeout = 2000;

    public enum ScanType{
        TCP_CONNECT,
        UDP,
        TCP_SYN
    }

    private final Host host;
    private final Port port;
    private final ScanType scanType;

    public PortScanTask(Host host, Port port, ScanType scanType){
        this.host = host;
        this.port = port;
        this.scanType = scanType;
    }

    @Override
    public Port call(){
        return switch(scanType){
            case TCP_CONNECT -> doTcpConnectScan();
            case UDP -> doUdpConnectScan();
            case TCP_SYN -> doTcpSynScan();
            default -> port;
        };
    }

    private Port doTcpConnectScan(){

        try(Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(host.getIpAddress(), port.getPortNumber()));
            port.setState(Port.PortState.OPEN);
            host.addPort(port);
        }catch (ConnectException e){
            port.setState(Port.PortState.CLOSED);
        } catch (IOException e) {
            port.setState(Port.PortState.UNKNOWN);
        }

        return port;
    }

    private Port doUdpConnectScan(){
        return null;
    }

    private Port doTcpSynScan(){
        return null;
    }



}
