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
@Table(name = "nerves")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Nerve.findAll", query = "SELECT r FROM Nerve r"),
    @NamedQuery(name = "Nerve.findById", query = "SELECT r FROM Nerve r WHERE r.id = :id")})
public class Nerve implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private Neuron source;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Neuron recipient;
    
    @JoinColumn(name = "fund_id")
    @ManyToOne
    private Fund fund;
    
    @JoinColumn(name = "frame_id")
    @ManyToOne
    private Frame frame;

    @Column(name = "value")
    private Float value;
    
    @Column(name = "level")
    private Integer level;
  
//    @Column(name = "source_id")
//    private Integer sourceId;
//    @Column(name = "recipient_id")
//    private Integer recipientId;

    public Nerve() {
    }

    public Nerve(Integer id) {
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
        if (!(object instanceof Nerve)) {
            return false;
        }
        Nerve other = (Nerve) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Nerve[" + id + "], ";
        s += "source: " + source.getType()+ ", ";
        s += "recipient: " + recipient.getType();
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

    public Neuron getRecipient() {
        return recipient;
    }

    public void setRecipient(Neuron recipient) {
        this.recipient = recipient;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
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

    
}
