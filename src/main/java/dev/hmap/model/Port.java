package dev.hmap.model;

import java.util.Objects;
import dev.hmap.enums.PortState;
import dev.hmap.enums.Protocol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ports")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int portNumber;

    @Enumerated(EnumType.STRING)
    private Protocol protocol;

    @Enumerated(EnumType.STRING)
    private PortState state;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    // Constructors -------------------

    public Port(int portNumber){
        this(portNumber, Protocol.TCP);
    }

    public Port(int portNumber, Protocol protocol){
        this.portNumber = portNumber;
        this.protocol = protocol;
        this.state = PortState.UNKNOWN;
    }

    public Port(int portNumber, boolean isOpen){
        this(portNumber);
        this.state = isOpen ? PortState.OPEN : PortState.CLOSED;
    }

    // Methods

    public String getDefaultServiceName() {
        return switch (portNumber){
            case 20 -> "FTP_DATA";
            case 21 -> "FTP";
            case 22 -> "SSH";
            case 23 -> "TELNET";
            case 25 -> "SMTP";
            case 53 -> "DNS";
            case 80 -> "HTTP";
            case 110 -> "POP3";
            case 143 -> "IMAP";
            case 443 -> "HTTPS";
            case 445 -> "SMB";
            case 3306 -> "MySQL";
            case 3389 -> "RDP";
            case 5432 -> "PostgreSQL";
            case 5900 -> "VNC";
            case 8080 -> "HTTP_PROXY";
            case 27017 -> "MongoDB";
            case 135 -> "msrpc";
            case 902 -> "iss-realsecure";
            case 912 -> "apex-mesh";
            case 139 -> "microsoft-ds";
            default -> "UNKNOWN";
        };
    }

    public int getRiskLevel(){
        if(state != PortState.OPEN) return 0;

        return switch (portNumber) {
            case 21, 23 -> 90;
            case 445, 3389 -> 70;
            case 22, 3306 -> 50;
            case 80, 8080 -> 30;
            case 443 -> 10;
            default -> 40;
        };
    }

}
