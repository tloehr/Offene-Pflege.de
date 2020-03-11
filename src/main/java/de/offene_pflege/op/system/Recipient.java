package de.offene_pflege.op.system;

import de.offene_pflege.entity.system.Users;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * Created by tloehr on 08.07.14.
 */
public class Recipient {

    String mailaddress, fullname;

    public Recipient(Users user) {
        this.mailaddress = user.getEMail();
        this.fullname = user.getFullname();
    }

    public Recipient(String mailaddress, String fullname) {
        this.mailaddress = mailaddress;
        this.fullname = fullname;
    }

    public String getMailaddress() {
        return mailaddress;
    }

    public void setMailaddress(String mailaddress) {
        this.mailaddress = mailaddress;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public InternetAddress getInternetAddress() throws UnsupportedEncodingException{
        return new InternetAddress(mailaddress, fullname);
    }
}
