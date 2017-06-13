/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "crystals")
@XmlRootElement
public class Crystal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy="crystal", cascade=CascadeType.ALL)
    private Set<Structure> structures;

//    @OneToMany(mappedBy="crystal", cascade=CascadeType.ALL)
//    private Set<Edge> edges;

    public Crystal() {
    }

    public Crystal(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Crystal)) {
            return false;
        }
        Crystal other = (Crystal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Crystal[" + id + "] "+name;
        if (getStructures() != null) {
            s += ", structures: " + getStructures().size();
        }
        return s;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    /**
     * @return the structures
     */
    public Set<Structure> getStructures() {
        return structures;
    }

    /**
     * @param structures the structures to set
     */
    public void setStructures(Set<Structure> structures) {
        this.structures = structures;
    }

    /**
     * @return the edges
     */
//    public Set<Edge> getEdges() {
//        return edges;
//    }
//
//    /**
//     * @param edges the edges to set
//     */
//    public void setEdges(Set<Edge> edges) {
//        this.edges = edges;
//    }

}
