package dev.hmap.model;

import dev.hmap.enums.HostStatus;
import dev.hmap.enums.OsFamily;
import jakarta.persistence.*;
import lombok.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Entity
@Table(name = "hosts")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hostName;
    private InetAddress ipAddress;
    private String ipString;
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private OsFamily osFamily;

    @Enumerated(EnumType.STRING)
    private HostStatus status;
    private boolean isActive;
    private String description;
    private long latency;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Port> openPorts;

    private int openPortsCount;
    private int totalPortsScanned;

    private LocalDateTime scanDate;

    private boolean reachable;

    // Constructors -------------------------

    public Host(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
        this.ipString = ipAddress.getHostAddress();
        this.status = HostStatus.UNKNOWN;
        this.isActive = false;
        this.osFamily = OsFamily.UNKNOWN;
        this.openPorts = new ArrayList<>();
    }

    public Host(InetAddress ipAddress, String hostName){
        this(ipAddress);
        this.hostName = hostName;
    }

    public Host(String ipString) throws UnknownHostException {
        this(InetAddress.getByName(ipString));
    }




    // Methods -----------------------------

    // Ports method

    public void addPort(Port port){
         if(port != null && !openPorts.contains(port)){
             openPorts.add(port);
             openPortsCount++;
         }
    }

    public void addPorts(List<Port> ports){
        if(ports != null){
            openPorts.addAll(ports);
        }
    }

    public boolean hasOpenPorts() {
        return openPorts.isEmpty();
    }

    public Port getPort(int portNumber){
        for(Port port: openPorts){
            if(port.getPortNumber() == portNumber){
                return port;
            }
        }
        return null;
    }

    // Control methods

    public void setActive(){
        this.isActive = true;
        this.status = HostStatus.UP;
    }

    public void setInactive(){
        this.isActive = false;
        this.status = HostStatus.DOWN;
    }


}
