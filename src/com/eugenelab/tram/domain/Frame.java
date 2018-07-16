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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "frames")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Frame.findAll", query = "SELECT f FROM Frame f"),
    @NamedQuery(name = "Frame.findById", query = "SELECT f FROM Frame f WHERE f.id = :id"),
    @NamedQuery(name = "Frame.findByName", query = "SELECT f FROM Frame f WHERE f.name = :name")})
public class Frame implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ms")
    private Integer ms;
    @Column(name = "name")
    private String name;
    @Column(name = "unit")
    private String unit;
    @Column(name = "duration")
    private Integer duration;

    public Frame() {
    }

    public Frame(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Frame[" + id + "]: " + name;
//        return "Frame[" + id + "]: " + name + ", unit: " + unit + ", duration: " + duration;
    }


//    public Integer getId() {
//        return id;
//    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof Frame)) {
            return false;
        }
        Frame other = (Frame) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    /**
     * @return the ms
     */
    public Integer getMs() {
        return ms;
    }

    /**
     * @param ms the ms to set
     */
    public void setMs(Integer ms) {
        this.ms = ms;
    }
}
