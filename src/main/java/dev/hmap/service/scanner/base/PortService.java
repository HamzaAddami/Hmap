package dev.hmap.service.scanner.base;

import dev.hmap.model.Port;

public interface PortService {
    Port registerPort(Port p);
    Port updatePort(Port p);

}
