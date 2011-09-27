/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author tloehr
 */
@Entity
@Table(name = "OCGroups")
@NamedQueries({
    @NamedQuery(name = "Groups.findAll", query = "SELECT g FROM Groups g"),
    @NamedQuery(name = "Groups.findAllSorted", query = "SELECT g FROM Groups g ORDER BY g.gkennung"),
    @NamedQuery(name = "Groups.findAllUnassigned", query = "SELECT g FROM Groups g WHERE :ocuser NOT MEMBER OF g.members ORDER BY g.gkennung"),
    @NamedQuery(name = "Groups.findByUserAndAdmin", query = "SELECT g FROM Groups g WHERE g.gkennung = 'admin' AND :user MEMBER OF g.members "),
    @NamedQuery(name = "Groups.findByUserAndExamen", query = "SELECT g FROM Groups g WHERE g.examen = true AND :user MEMBER OF g.members "),
    @NamedQuery(name = "Groups.findByGkennung", query = "SELECT g FROM Groups g WHERE g.gkennung = :gkennung")})
public class Groups implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "GKENNUNG")
    private String gkennung;
    @Lob
    @Column(name = "Beschreibung")
    private String beschreibung;
    @Basic(optional = false)
    @Column(name = "System")
    private boolean system;
    @Basic(optional = false)
    @Column(name = "Examen")
    private boolean examen;
    @ManyToMany(mappedBy="groups")
    private Collection<Users> members;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groups")
    private Collection<IntClasses> icCollection;

    public Groups() {
        this.gkennung = "";
        this.beschreibung = "Neue Gruppe";
    }

    public void setMembers(Collection<Users> members) {
        this.members = members;
    }

    public Collection<Users> getMembers() {
        return members;
    }

    public Collection<IntClasses> getIcCollection() {
        return icCollection;
    }

    public void setIcCollection(Collection<IntClasses> icCollection) {
        this.icCollection = icCollection;
    }

    public String getGkennung() {
        return gkennung;
    }

    public void setGkennung(String gkennung) {
        this.gkennung = gkennung;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isExamen() {
        return examen;
    }

    public void setExamen(boolean examen) {
        this.examen = examen;
    }
    
    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gkennung != null ? gkennung.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Groups)) {
            return false;
        }
        Groups other = (Groups) object;
        if ((this.gkennung == null && other.gkennung != null) || (this.gkennung != null && !this.gkennung.equals(other.gkennung))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "<html><b>" + gkennung.toUpperCase() + "</b> (" + beschreibung + ")</html>";
    }
}
