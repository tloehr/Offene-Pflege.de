package de.offene_pflege.backend.entity;


import de.offene_pflege.op.tools.HasLogger;
import de.offene_pflege.op.tools.Tools;

import javax.persistence.*;
import java.io.Serializable;


@MappedSuperclass
public abstract class DefaultEntity implements HasLogger, Serializable {
    private Long id;
    private int version;

    /*
    All field-level JPA annotations can be placed either on fields or on properties, it determines access type of the entity (i.e. how JPA provider will access fields of that entity - directly or using getters/setters).
    Default access type is determined by placement of @Id annotation, and it should be consistent for all fields of the entity (or hiererchy of inherited entities), unless explicitly overriden by @Access for some fields.
    So, @Transient on getters has the same meaning as @Transient on fields - if default access type for your entity is property access, you need to annotate all getters that don't correspond to persistent properties with @Transient.
     */

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        } else if (!(other instanceof DefaultEntity)) {
            equal = false;
        } else {
            equal = id.equals(((DefaultEntity) other).id);
        }

        return equal;
    }

    @Override
    public String toString() {
        return Tools.toString(this, this.getClass());
    }
}
