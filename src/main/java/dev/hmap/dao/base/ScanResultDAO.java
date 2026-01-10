package dev.hmap.dao.base;

import dev.hmap.model.Host;
import dev.hmap.model.ScanResult;

import java.util.List;

public interface ScanResultDAO extends BaseDAO<ScanResult, Long> {
    List<ScanResult> findLastScansByHost(Host host, int limit);
}
