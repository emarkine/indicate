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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
//import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "weights")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Weight.findAll", query = "SELECT w FROM Weight w"),
    @NamedQuery(name = "Weight.findById", query = "SELECT w FROM Weight w WHERE w.id = :id"),
//    @NamedQuery(name = "Weight.findBySetId", query = "SELECT w FROM Weight w WHERE w.setId = :setId"),
    @NamedQuery(name = "Weight.findByLevel", query = "SELECT w FROM Weight w WHERE w.level = :level"),
    @NamedQuery(name = "Weight.findByTurn", query = "SELECT w FROM Weight w WHERE w.turn = :turn"),
    @NamedQuery(name = "Weight.findByAngle", query = "SELECT w FROM Weight w WHERE w.angle = :angle"),
    @NamedQuery(name = "Weight.findByTrend", query = "SELECT w FROM Weight w WHERE w.trend = :trend"),
    @NamedQuery(name = "Weight.findBySign", query = "SELECT w FROM Weight w WHERE w.sign = :sign")})
public class Weight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

//    @Column(name = "set_id")
//    private Integer setId;

//    @OneToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    @Column(name = "level")
    private Integer level;
    @Column(name = "turn")
    private Integer turn;
    @Column(name = "angle")
    private Integer angle;
    @Column(name = "trend")
    private Integer trend;
    @Column(name = "sign")
    private Integer sign;

    public Weight() {
    }

    public Weight(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Weight[" + setting.getName() + "]"
                + " level: " + level
                + ", turn: " + turn
                + ", angle: " + angle
                + ", trend: " + trend
                + ", sign: " + getSignStr();
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
        if (!(object instanceof Weight)) {
            return false;
        }
        Weight other = (Weight) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
//
//    public Integer getSetId() {
//        return setId;
//    }
//
//    public void setSetId(Integer setId) {
//        this.setId = setId;
//    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getAngle() {
        return angle;
    }

    public void setAngle(Integer angle) {
        this.angle = angle;
    }

    public Integer getTrend() {
        return trend;
    }

    public void setTrend(Integer trend) {
        this.trend = trend;
    }

    public Integer getSign() {
        return sign;
    }

    public String getSignStr() {
        if (sign > 0) {
            return "+";
        } else if (sign < 0) {
            return "-";
        } else {
            return "0";
        }
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    /**
     * @return the set
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     * @param set the set to set
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    /**
     * Суммарный вес всех параметров кроме level
     * @return 
     */
    public int totalWeight() {
        return Math.abs(turn) + Math.abs(angle) + Math.abs(trend) + Math.abs(sign);
    }
            
    
}
