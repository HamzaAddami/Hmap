package dev.hmap;

import org.apache.commons.net.DatagramSocketClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MyUDPClient extends DatagramSocketClient {

    public String ask(String host, int port, String message) throws IOException {
        try {
            InetAddress ip = InetAddress.getByName(host);
            open();
            _socket_.setSoTimeout(5000);

            byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
            _socket_.send(sendPacket);

            byte[] recData = new byte[4096];
            DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
            _socket_.receive(recPacket);

            return new String(recPacket.getData(), 0, recPacket.getLength(), StandardCharsets.UTF_8);

        } finally {
            close();
        }

    }


}
