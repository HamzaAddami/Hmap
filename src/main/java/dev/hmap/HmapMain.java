package dev.hmap;

import dev.hmap.enums.ScanType;
import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;
import dev.hmap.service.scanner.impl.PortScanService;
import dev.hmap.utils.PortGeneator;
import org.pcap4j.core.*;

import java.io.IOException;
import java.net.*;

import java.util.List;
import java.util.concurrent.*;


public class HmapMain {

//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dev/hmap/fxml/MainView.fxml"));
//        Parent root = loader.load();
//        Scene scene = new Scene(root, 1200, 800);
//        stage.setScene(scene);
//        stage.setTitle("Hmap");
//        stage.show();
//
//    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException, PcapNativeException {
//         launch(args);


        Host host = new Host("192.168.1.55");


        // ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();




        PortScanService portScanService = new PortScanService();


        Future<ScanResult>  result = portScanService.scanAsync(
                host, PortGeneator.COMMON_PORTS, ScanType.TCP_CONNECT
        );
        // System.out.println(threadPoolManager.getSummary());

        try{
            ScanResult finalResult = result.get();
            System.out.println(finalResult.getSummary());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


    }

    public static String getMacAddress(NetworkInterface netInterface) throws SocketException{
        byte[] mac = netInterface.getHardwareAddress();
        if(mac == null) return "UNKNOWN";
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mac.length; i++){
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        return sb.toString();

    }

    public static PcapNetworkInterface getActiveInterface() throws PcapNativeException {
        List<PcapNetworkInterface> allIfs = Pcaps.findAllDevs();
        for (PcapNetworkInterface nif : allIfs) {
            if (!nif.isLoopBack() && nif.isUp() && !nif.getAddresses().isEmpty()) {
                boolean hasIpv4 = nif.getAddresses().stream()
                        .anyMatch(addr -> addr.getAddress() instanceof Inet4Address);
                if (hasIpv4) return nif;
            }
        }
        throw new RuntimeException("Aucune interface réseau active détectée.");
    }

}

