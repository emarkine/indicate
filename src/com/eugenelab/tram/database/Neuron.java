/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "neurons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Neuron.findAll", query = "SELECT n FROM Neuron n"),
    @NamedQuery(name = "Neuron.findById", query = "SELECT n FROM Neuron n WHERE n.id = :id"),
    @NamedQuery(name = "Neuron.findByType", query = "SELECT n FROM Neuron n WHERE n.type = :type"),
    @NamedQuery(name = "Neuron.findByEdge", query = "SELECT n FROM Neuron n WHERE n.edge = :edge"),
    @NamedQuery(name = "Neuron.findByPosition", query = "SELECT n FROM Neuron n WHERE n.position = :position")})
public class Neuron implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Column(name = "position")
    private Integer position;
    @Column(name = "factor")
    private Float factor;

    @ManyToOne
    @JoinColumn(name = "edge_id", nullable = false)
    private Edge edge;

    public Neuron() {
    }

    public Neuron(Integer id) {
        this.id = id;
    }

    public Neuron(Integer id, String type, Edge edge) {
        this.id = id;
        this.type = type;
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
        if (!(object instanceof Neuron)) {
            return false;
        }
        Neuron other = (Neuron) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Neuron[" + id + "], ";
        s += "edge: " + edge.getName() + ", ";
        s += "type: " + type + ", ";
        s += "position: " + position + ", ";
        s += "factor: " + factor + "";
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

}
