package dev.hmap;

import java.io.IOException;


public class Hmap {


    public static void main(String[] args) throws IOException {

        System.out.println("=== Testing UDP Services Only ===\n");

        MyUDPClient udpClient = new MyUDPClient();

        System.out.println(udpClient.ask("ftp.scene.org", 13, "Hello"));








    }



}

