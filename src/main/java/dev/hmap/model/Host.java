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
import java.util.logging.Logger;


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

    @Transient
    private InetAddress ipAddress;

    private String ipString;
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private OsFamily osFamily;

    @Enumerated(EnumType.STRING)
    private HostStatus status;
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

    public void addPort(Port port){
         if(port != null && !openPorts.contains(port)){
             openPorts.add(port);
             port.setHost(this);
         }
    }

    @PostLoad
    private void initInetAddress(){
        try{
            this.ipAddress = InetAddress.getByName(ipString);
        }catch (UnknownHostException e){
            System.err.println(e.getMessage());
        }
    }


}
