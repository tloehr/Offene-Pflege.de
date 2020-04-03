package de.offene_pflege.backend.entity;

import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;

/**
 * Created by tloehr on 31.08.15.
 */
public abstract class Ownable {
    public abstract OPUsers getOwner();

    public boolean isMine(){
        return getOwner().equals(OPDE.getMe());
    }
}
