/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import static com.eugenelab.tram.util.Constant.FORMAT;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "data")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Datum.findAll", query = "SELECT d FROM Datum d"),
    @NamedQuery(name = "Datum.findById", query = "SELECT d FROM Datum d WHERE d.id = :id"),
    @NamedQuery(name = "Datum.findByTime", query = "SELECT d FROM Datum d WHERE d.time = :time"),
    @NamedQuery(name = "Datum.findByValue", query = "SELECT d FROM Datum d WHERE d.value = :value")})
public class Datum implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

//    @Basic(optional = false)
    @JoinColumn(name = "fund_id")
    @ManyToOne
    private Fund fund;

//    @Basic(optional = false)
    @JoinColumn(name = "frame_id")
    @ManyToOne
    private Frame frame;

//    @Basic(optional = false)
    @JoinColumn(name = "neuron_id")
    @ManyToOne
    private Neuron neuron;

    @Basic(optional = false)
    @Column(name = "value")
    private int value;

    @Basic(optional = false)
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Column(name = "prev_id")
    private Integer prevId;

    @Column(name = "next_id")
    private Integer nextId;

//    @OneToOne(fetch=FetchType.LAZY,targetEntity=Datum.class)
//    @JoinColumn(name = "prev_id")
//    private Datum prev;
//
//    @OneToOne(fetch=FetchType.LAZY,targetEntity=Datum.class)
//    @JoinColumn(name = "next_id")
//    private Datum next;
    public Datum() {
    }

    public Datum(Integer id) {
        this.id = id;
    }

    public Datum(Integer id, Fund fund, Frame frame, Neuron neuron, Date time, int value) {
        this.id = id;
        this.neuron = neuron;
        this.fund = fund;
        this.frame = frame;
        this.time = time;
        this.value = value;
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
        if (!(object instanceof Datum)) {
            return false;
        }
        Datum other = (Datum) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Datum[" + id + "], ";
        if (prevId != null) {
            s += "prev[" + prevId + "], ";
        }
        if (nextId != null) {
            s += "next[" + nextId + "], ";
        }
        s += "edge: " + neuron.getEdge().getName() + ", ";
        s += "neuron: " + neuron.getType() + ", ";
        s += "fund: " + fund.getName() + ", ";
        s += "frame: " + frame.getName() + ", ";
        s += "value: " + value + ", ";
        s += "time: " + FORMAT.format(time);
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Neuron getNeuron() {
        return neuron;
    }

    public void setNeuron(Neuron neuron) {
        this.neuron = neuron;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value > 9) {
            value = 9;
        } else if (value < -9) {
            value = -9;
        }
        this.value = value;
    }

    public int getSignValue() {
        if ("Neuron::Sign".equals(neuron.getType())) {
            if (value > 0) {
                return 9;
            } else if (value < 0) {
                return -9;
            } else {
                return 0;
            }
        } else {
            return value;
        }
    }

    public void setSignValue(int value) {
        if ("Neuron::Sign".equals(neuron.getType())) {
            if (value > 0) {
                value = 1;
            } else if (value < 0) {
                value = -1;
            }
        }
        setValue(value);
    }

    public Integer getPrevId() {
        return prevId;
    }

    public void setPrevId(Integer prevId) {
        this.prevId = prevId;
    }

    public Integer getNextId() {
        return nextId;
    }

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

//    public Datum getPrev() {
//        return prev;
//    }
//
//    public void setPrev(Datum prev) {
//        this.prev = prev;
//    }
//
//    public Datum getNext() {
//        return next;
//    }
//
//    public void setNext(Datum next) {
//        this.next = next;
//    }
}
