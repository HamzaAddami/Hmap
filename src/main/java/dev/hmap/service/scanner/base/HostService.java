package dev.hmap.service.scanner.base;

import dev.hmap.model.Host;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;


public interface HostService {
    Host addService(Host host) throws ServiceException;
}
