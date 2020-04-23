package de.offene_pflege.backend.entity;

import de.offene_pflege.backend.entity.system.OPUsers;
import de.offene_pflege.op.OPDE;

/**
 * Created by tloehr on 31.08.15.
 */
public interface Ownable {
    OPUsers getOwner();
    default boolean isMine(){
        return getOwner().equals(OPDE.getMe());
    }

}
