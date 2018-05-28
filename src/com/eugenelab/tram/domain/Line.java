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
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "lines")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Line.findAll", query = "SELECT r FROM Line r"),
    @NamedQuery(name = "Line.findById", query = "SELECT r FROM Line r WHERE r.id = :id")})
public class Line implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "node_id", nullable = false)
//    private Node node;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private Neuron source;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private Neuron target;

    @Column(name = "value")
    private Float value;
    
    @Column(name = "level")
    private Integer level;
  
//    @Column(name = "source_id")
//    private Integer sourceId;
//    @Column(name = "recipient_id")
//    private Integer recipientId;

    public Line() {
    }

    public Line(Integer id) {
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
        if (!(object instanceof Line)) {
            return false;
        }
        Line other = (Line) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Nerve[" + id + "], ";
        s += "source: " + source.getType()+ ", ";
        s += "target: " + target.getType();
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public Integer getSourceId() {
//        return sourceId;
//    }
//
//    public void setSourceId(Integer sourceId) {
//        this.sourceId = sourceId;
//    }
//
//    public Integer getRecipientId() {
//        return recipientId;
//    }
//
//    public void setRecipientId(Integer recipientId) {
//        this.recipientId = recipientId;
//    }

    public Neuron getSource() {
        return source;
    }

    public void setSource(Neuron source) {
        this.source = source;
    }

    public Neuron getTarget() {
        return target;
    }

    public void setTarget(Neuron target) {
        this.target = target;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

//    public Node getNode() {
//        return node;
//    }
//
//    public void setNode(Node node) {
//        this.node = node;
//    }
//    
}
