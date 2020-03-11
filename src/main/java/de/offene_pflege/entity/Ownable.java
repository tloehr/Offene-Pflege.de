package de.offene_pflege.entity;

import de.offene_pflege.entity.system.Users;
import de.offene_pflege.op.OPDE;

/**
 * Created by tloehr on 31.08.15.
 */
public abstract class Ownable {
    public abstract Users getOwner();

    public boolean isMine(){
        return getOwner().equals(OPDE.getMe());
    }
}
