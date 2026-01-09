package dev.hmap.dao.base;

import dev.hmap.model.Host;

import java.util.List;
import java.util.Optional;

public interface HostDAO extends BaseDAO<Host, Long>{
    Optional<Host> findByIp(String ip);
}
