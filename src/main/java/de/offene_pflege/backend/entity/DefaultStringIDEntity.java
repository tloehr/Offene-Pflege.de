package de.offene_pflege.backend.entity;


import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.Tools;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class DefaultStringIDEntity implements HasLogger, Serializable {
    private String id;
    private int version;
//    Field field = Class.class.getDeclaredField("id");

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Version
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }

        return 31 + id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        boolean equal;

        if (id == null) {
            // New entities are only equal if the instance if the same
            equal = super.equals(other);
        } else if (this == other) {
            equal = true;
        } else if (!(other instanceof DefaultStringIDEntity)) {
            equal = false;
        } else {
            equal = id.equals(((DefaultStringIDEntity) other).id);
        }

//        getLogger().debug("this: " + toString() + " other: " + Tools.catchNull(other, "null") + ": is equal ??: " + Boolean.toString(equal));
        return equal;
    }

    @Override
    public String toString() {
        return Tools.toString(this, this.getClass());
    }
}
