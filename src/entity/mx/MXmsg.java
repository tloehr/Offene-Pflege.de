package entity.mx;

import entity.system.Users;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Entity to handle internal messages between users.
 *
 * @author tloehr
 */
@Entity
@Table(name = "mxmsg")
public class MXmsg implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MXID")
    private Long mxid;

    /**
     * who this message was sent by
     */
    @JoinColumn(name = "sender", referencedColumnName = "UKennung")
    @ManyToOne
    private Users sender;

    /**
     * when this msg is not yet ready, then its in draft mode and shall be completed later
     */
    @Basic(optional = false)
    @Column(name = "draft")
    private boolean draft;

    /**
     * the pit when this message was sent to the recipient.
     */
    @Basic(optional = false)
    @Column(name = "pit")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pit;

    /**
     * the subject line of this message
     */
    @Basic(optional = false)
    @Column(name = "subject")
    private String subject;

    /**
     * the message text itself.
     */
    @Lob
    @Column(name = "Text")
    private String text;

    /**
     * the list of the users to receive this message
     */
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "msg", fetch = FetchType.LAZY)
    private Set<MXrecipient> recipients;

    @Version
    @Column(name = "version")
    private Long version;

    public MXmsg() {
    }

    public MXmsg(Users sender) {
        this.sender = sender;
        this.text = "";
        this.subject = "";
        this.recipients = new HashSet<>();
        this.pit = new Date();
        this.draft = true;
    }

    public Long getMxid() {
        return mxid;
    }

    public void setMxid(Long mxid) {
        this.mxid = mxid;
    }

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Date getPit() {
        return pit;
    }

    public void setPit(Date pit) {
        this.pit = pit;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Set<MXrecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<MXrecipient> recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MXmsg mXmsg = (MXmsg) o;
        return Objects.equals(mxid, mXmsg.mxid) &&
                Objects.equals(sender, mXmsg.sender) &&
                Objects.equals(draft, mXmsg.draft) &&
                Objects.equals(pit, mXmsg.pit) &&
                Objects.equals(subject, mXmsg.subject) &&
                Objects.equals(text, mXmsg.text) &&
                Objects.equals(version, mXmsg.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mxid, sender, draft, pit, subject, text, version);
    }

    @Override
    public String toString() {
        return "MXmsg{" +
                "mxid=" + mxid +
                ", sender=" + sender +
                ", draft=" + draft +
                ", pit=" + pit +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", version=" + version +
                '}';
    }
}
