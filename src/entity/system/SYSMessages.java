package entity.system;

import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "SYSMessages")
@NamedQueries({
        @NamedQuery(name = "SYSMessages.findAll", query = "SELECT s FROM SYSMessages s"),
        @NamedQuery(name = "SYSMessages.findByMid", query = "SELECT s FROM SYSMessages s WHERE s.mid = :mid"),
        @NamedQuery(name = "SYSMessages.findBySender", query = "SELECT s FROM SYSMessages s WHERE s.sender = :sender"),
        @NamedQuery(name = "SYSMessages.findBySenderHost", query = "SELECT s FROM SYSMessages s WHERE s.senderHost = :senderHost"),
        @NamedQuery(name = "SYSMessages.findByRecipient", query = "SELECT s FROM SYSMessages s WHERE s.recipient = :recipient"),
        @NamedQuery(name = "SYSMessages.findByReceiverHostAndUnprocessed", query = "SELECT s FROM SYSMessages s WHERE s.processed = '9999-12-31 23:59:59' AND  s.receiverHost = :receiverHost"),
        @NamedQuery(name = "SYSMessages.findByCommand", query = "SELECT s FROM SYSMessages s WHERE s.command = :command"),
        @NamedQuery(name = "SYSMessages.findBySent", query = "SELECT s FROM SYSMessages s WHERE s.sent = :sent"),
        @NamedQuery(name = "SYSMessages.findByProcessed", query = "SELECT s FROM SYSMessages s WHERE s.processed = :processed")})
public class SYSMessages implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MID")
    private Long mid;
    @Column(name = "Sender")
    private String sender;
    @Column(name = "Recipient")
    private String recipient;
    @Column(name = "Command")
    private Integer command;
    @Lob
    @Column(name = "Message")
    private String message;
    @Basic(optional = false)
    @Column(name = "Sent")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sent;
    @Basic(optional = false)
    @Column(name = "Processed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processed;

    @JoinColumn(name = "SHOST", referencedColumnName = "HostID")
    @ManyToOne
    private SYSHosts senderHost;

    @JoinColumn(name = "RHOST", referencedColumnName = "HostID")
    @ManyToOne
    private SYSHosts receiverHost;

    public SYSMessages() {
    }

    public SYSMessages(SYSHosts senderHost, SYSHosts receiverHost, Integer command, String message) {
        this.senderHost = senderHost;
        this.receiverHost = receiverHost;
        this.recipient = null;
        this.sender = null;
        this.command = command;
        this.message = message;
        this.sent = new Date();
        this.processed = SYSConst.DATE_BIS_AUF_WEITERES;
    }


    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public Date getProcessed() {
        return processed;
    }

    public void setProcessed(Date processed) {
        this.processed = processed;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mid != null ? mid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SYSMessages)) {
            return false;
        }
        SYSMessages other = (SYSMessages) object;
        if ((this.mid == null && other.mid != null) || (this.mid != null && !this.mid.equals(other.mid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.rest.SYSMessages[mid=" + mid + "]";
    }
}