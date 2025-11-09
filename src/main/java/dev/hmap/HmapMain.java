package dev.hmap;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import org.apache.commons.net.SocketClient;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Locale;


public class HmapMain  {

//    @Override
//    public void start(Stage stage) {
//        Circle circle = new Circle(20, 20, 20);
//        Group root = new Group(circle);
//        Scene scene = new Scene(root, 400, 300);
//        stage.setScene(scene);
//        stage.setTitle("Hmap");
//        stage.show();
//    }

    public static void main(String[] args)  throws IOException{

        System.out.println(Application.getUserAgentStylesheet());

    }



}

