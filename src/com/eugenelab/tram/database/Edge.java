/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import static com.eugenelab.tram.util.Constant.FORMAT;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "edges")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Edge.findAll", query = "SELECT e FROM Edge e"),
    @NamedQuery(name = "Edge.findById", query = "SELECT e FROM Edge e WHERE e.id = :id")})
//    @NamedQuery(name = "Edge.findByName", query = "SELECT e FROM Edge e WHERE e.name = :name")})
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @JoinColumn(name = "setting_id", nullable=false)
    @ManyToOne
    private Setting setting;
    
//    @OneToMany(targetEntity=Neuron.class)
    @OneToMany(mappedBy="edge", cascade=CascadeType.ALL)
    @OrderBy("position DESC")
    private List<Neuron> neurons;

    @OneToMany(mappedBy="edge", cascade=CascadeType.ALL)
    private Set<Structure> structures;


    public Edge() {
    }

    public Edge(Integer id) {
        this.id = id;
    }

    public Edge(Integer id, Setting set) {
        this.id = id;
        this.setting = set;
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
        if (!(object instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Edge[" + id + "], ";
        if (setting != null) {
            s += "setting: " + setting.getName() + ", ";
        }
        if (structures != null) {
            s += "structures: " + structures.size();
        }
        if (neurons != null) {
            s += "neurons: " + neurons.size();
        }
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return setting.getName();
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }

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
    
}
