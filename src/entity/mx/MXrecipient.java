package entity.mx;


import entity.system.Users;
import op.tools.SYSConst;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * This entity maintains the list of recipients for every single MXmsg.
 *
 * @author tloehr
 */
@Entity
@Table(name = "mxrecipient")
public class MXrecipient implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MXRID")
    private Long mxrid;

    /**
     * the pit when this message was received by the recipient.
     */
    @Basic(optional = false)
    @Column(name = "received")
    @Temporal(TemporalType.TIMESTAMP)
    private Date received;

    /**
     * tells us, whether the recipient has trashed this message
     */
    @Basic(optional = false)
    @Column(name = "trashed")
    private boolean trashed;

    /**
     * is true, when the recipient didnt yet read the message. this is a little bit redundant, as we could also
     * tell this fact by evaluating the received attribute. But a boolean is so much easier to index. :D
     */
    @Basic(optional = false)
    @Column(name = "unread")
    private boolean unread;

    /**
     * who the message was sent to
     */
    @JoinColumn(name = "recipient", referencedColumnName = "UKennung")
    @ManyToOne
    private Users recipient;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * the reference to message being sent
     */
    @JoinColumn(name = "MXID", referencedColumnName = "MXID")
    @ManyToOne
    private MXmsg msg;

    public MXrecipient() {
    }

    public MXrecipient(Users recipient, MXmsg msg) {
        this.recipient = recipient;
        this.msg = msg;
        this.trashed = false;
        this.unread = true;
        this.received = SYSConst.DATE_UNTIL_FURTHER_NOTICE;
    }

    public Long getMxrid() {
        return mxrid;
    }

    public void setMxrid(Long mxrid) {
        this.mxrid = mxrid;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public Users getRecipient() {
        return recipient;
    }

    public void setRecipient(Users recipient) {
        this.recipient = recipient;
    }

    public MXmsg getMsg() {
        return msg;
    }

    public void setMsg(MXmsg msg) {
        this.msg = msg;
    }

    public boolean isTrashed() {
        return trashed;
    }

    public void setTrashed(boolean trashed) {
        this.trashed = trashed;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MXrecipient that = (MXrecipient) o;
        return Objects.equals(mxrid, that.mxrid) &&
                Objects.equals(received, that.received) &&
                Objects.equals(trashed, that.trashed) &&
                Objects.equals(unread, that.unread) &&
                Objects.equals(recipient, that.recipient) &&
                Objects.equals(version, that.version) &&
                Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mxrid, received, trashed, unread, recipient, version, msg);
    }

    @Override
    public String toString() {
        return "MXrecipient{" +
                "mxrid=" + mxrid +
                ", received=" + received +
                ", trashed=" + trashed +
                ", unread=" + unread +
                ", recipient=" + recipient +
                ", version=" + version +
                ", msg=" + msg +
                '}';
    }
}

