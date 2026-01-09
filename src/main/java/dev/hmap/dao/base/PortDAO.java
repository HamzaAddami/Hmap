package dev.hmap.dao.base;

import dev.hmap.model.Port;

import java.util.List;

public interface
PortDAO extends BaseDAO<Port, Long> {
    List<Port> findOpenPortsByHost(Long hostId);
}
