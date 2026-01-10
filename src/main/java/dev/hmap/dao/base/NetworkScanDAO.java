package dev.hmap.dao.base;

import dev.hmap.model.NetworkScan;

import java.util.List;

public interface NetworkScanDAO extends BaseDAO<NetworkScan, Long> {
    List<NetworkScan> findBySubnet(String subnet);
}
