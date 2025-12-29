package dev.hmap.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    private String serviceName;
    private Port port;
    private String protocol;
    private String description;
    private boolean isVulnerable;

    public Service(Port port){
        this.port = port;
        this.protocol = port.getDefaultServiceName();
        this.isVulnerable = false;
    }
}
