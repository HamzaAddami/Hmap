package dev.hmap.utils;

import java.util.ArrayList;
import java.util.List;

public class PortGeneator {

    public static final List<Integer> COMMONS_UDP_PORTS = List.of
            (
                    53, 67, 68, 69, 123, 137, 138, 139,161,
                    162, 445, 500, 514, 520, 1194, 1900, 3478,
                    4500, 5353, 5060, 6881, 10000, 17185, 27015,
                    3702, 4500, 5353
            );

    public static final List<Integer> COMMON_PORTS = List.of
            (20, 21, 22, 23, 25, 80, 110,
                    111, 123, 135, 137, 138, 139, 143,
                    161, 389, 443, 445, 500, 512, 514,
                    515, 520, 587, 631, 636, 873, 902,
                    903, 993, 995, 1900, 5357, 8081, 49152,
                    62078, 65001, 3000, 3306
            );

    public static final List<Integer> WELL_KNOWN_PORTS = generateRangePorts(1, 100);
    private static final int SCAN_TIMEOUT_MS = 2000;



    private static List<Integer> generateRangePorts(int start, int end){
        List<Integer> result = new ArrayList<>();
        for (int i=start; i<end; i++){
            result.add(i);
            start++;
        }
        return result;
    }

}
