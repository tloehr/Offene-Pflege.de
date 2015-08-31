package entity;

import entity.system.Users;
import op.OPDE;

/**
 * Created by tloehr on 31.08.15.
 */
public abstract class Ownable {
    public abstract Users getOwner();

    public boolean isMine(){
        return OPDE.isAdmin() || getOwner().equals(OPDE.getLogin().getUser());
    }
}
