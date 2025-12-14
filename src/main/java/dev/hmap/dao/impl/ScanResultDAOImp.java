package dev.hmap.dao.impl;

import dev.hmap.dao.base.ScanResultDAO;
import dev.hmap.model.ScanResult;

import java.util.List;

public class ScanResultDAOImp implements ScanResultDAO<ScanResult> {

    @Override
    public ScanResult save(ScanResult scanResult) {
        return null;
    }

    @Override
    public List<ScanResult> findAll() {
        return List.of();
    }

    @Override
    public void delete(long id) {

    }
}
