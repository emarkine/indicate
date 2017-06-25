/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "structures")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Structure.findAll", query = "SELECT s FROM Structure s"),
    @NamedQuery(name = "Structure.findById", query = "SELECT s FROM Structure s WHERE s.id = :id"),
    @NamedQuery(name = "Structure.findByPosition", query = "SELECT s FROM Structure s WHERE s.position = :position")})
public class Structure implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @Column(name = "position")
    private Integer position;
    
    @ManyToOne
    @JoinColumn(name = "crystal_id", nullable=false)
    private Crystal crystal;

    @ManyToOne
    @JoinColumn(name = "edge_id", nullable=false)
    private Edge edge;


    public Structure() {
    }

    public Structure(Integer id) {
        this.id = id;
    }

    public Structure(Integer id, Crystal crystal, Edge edge) {
        this.id = id;
        this.crystal = crystal;
        this.edge = edge;
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
        if (!(object instanceof Structure)) {
            return false;
        }
        Structure other = (Structure) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.eugenelab.tram.database.Structure[ id=" + id + " ]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Crystal getCrystal() {
        return crystal;
    }

    public void setCrystal(Crystal crystal) {
        this.crystal = crystal;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }
    
}
