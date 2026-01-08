package dev.hmap.service.scanner.base;

import dev.hmap.model.Host;

import java.util.List;
import java.util.Optional;


public interface HostService {
    Host registerHost(Host host);
    Host updateHost(Host host);
    Host findById(Long id);
    List<Host> findAllHosts();
    void delete(Host host);
    Optional<Host> findById(String ip);
}
