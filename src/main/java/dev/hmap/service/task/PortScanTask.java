package dev.hmap.service.task;

import dev.hmap.model.Host;
import dev.hmap.model.Port;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.Callable;
import dev.hmap.enums.PortState;

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
            socket.connect(new InetSocketAddress(host.getIpAddress(), port.getPortNumber()), timeout);
            port.setState(PortState.OPEN);
            host.addPort(port);
        }catch (ConnectException e){
            port.setState(PortState.CLOSED);
        } catch (IOException e) {
            port.setState(PortState.UNKNOWN);
        }

        return port;
    }

    private Port doUdpConnectScan(){

        try(DatagramSocket socket = new DatagramSocket()){
            socket.setSoTimeout(timeout);

            socket.connect(host.getIpAddress(), port.getPortNumber());
            byte[] sendData = "TEST".getBytes();
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length);
            socket.send(packet);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try{
                socket.receive(receivePacket);
                port.setState(PortState.OPEN);
                host.addPort(port);
            }catch (SocketTimeoutException e){
                host.addPort(port);
                port.setState(PortState.OPEN_OR_FILTERED);
            }

        }catch(PortUnreachableException e){
            port.setState(PortState.CLOSED);
        }catch(IOException e){
            port.setState(PortState.UNKNOWN);
        }

        return port;
    }


    private Port doTcpSynScan(){
        return null;
    }



}
