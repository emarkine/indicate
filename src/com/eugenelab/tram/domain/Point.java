/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.domain;

import com.eugenelab.tram.interfaces.Rateable;
import static com.eugenelab.tram.util.Constant.FORMAT;
import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "points")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Point.findAll", query = "SELECT p FROM Point p"),
    @NamedQuery(name = "Point.findById", query = "SELECT p FROM Point p WHERE p.id = :id"),
    @NamedQuery(name = "Point.findByTime", query = "SELECT p FROM Point p WHERE p.time = :time"),
    @NamedQuery(name = "Point.findByValue", query = "SELECT p FROM Point p WHERE p.value = :value")})
public class Point implements Serializable, Rateable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    @ManyToOne
    @JoinColumn(name = "fund_id")
    private Fund fund;

    @ManyToOne
    @JoinColumn(name = "frame_id")
    private Frame frame;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceData service;

//    @OneToOne(targetEntity=Point.class)
//    @PrimaryKeyJoinColumn
//    private Point prev;
//    @JoinColumn(name = "prev_id")
    @Column(name = "prev_id")
    private Integer prevId;

//    @OneToOne(fetch=FetchType.LAZY,targetEntity=Point.class)
//    @OneToOne(targetEntity=Point.class)
//    private Point next;
//    @JoinColumn(name = "next_id")
    @Column(name = "next_id")
    private Integer nextId;

    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "derivative")
    private Double derivative;

    @Column(name = "second_derivative")
    private Double secondDerivative;

    @Column(name = "angle_degrees")
    private Integer angleDegrees;

    @Column(name = "alpha")
    private Double alpha;

    public Point() {
    }

    public Point(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String s = "Point[" + id + "] " + setting.getName() + ", ";
        if (getPrevId() != null) {
            s += "prev[" + getPrevId() + "], ";
        }
        if (getNextId() != null) {
            s += "next[" + getNextId()+ "], ";
        }
        s += "fund: " + fund.getName() + ", ";
        s += "frame: " + frame.getName() + ", ";
        s += "service: " + service.getName() + ", ";
        s += "value: " + String.format("%.5f", value) + ", ";
        s += "alpha: " + String.format("%.4f", alpha) + ", ";
        s += "time: " + FORMAT.format(time);
        return s;
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
        if (!(object instanceof Point)) {
            return false;
        }
        Point other = (Point) object;
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

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
    @Override
    public Date getTime() {
        return time;
    }

    @Override
    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public void setTime(long time) {
        this.time = new Date(time);
    }

    @Override
    public BigDecimal getRate() {
        return value;
    }

    @Override
    public void setRate(BigDecimal rate) {
        this.value = rate;
    }

    @Override
    public void setRate(double rate) {
        this.value = BigDecimal.valueOf(rate);
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setValue(double value) {
        this.value = BigDecimal.valueOf(value);
    }

    /**
     * @return the indicator
     */
//    public Indicator getIndicator() {
//        return indicator;
//    }
//
//    /**
//     * @param indicator the indicator to set
//     */
//    public void setIndicator(Indicator indicator) {
//        this.indicator = indicator;
//    }
//
    /**
     * @return the fund
     */
    public Fund getFund() {
        return fund;
    }

    /**
     * @param fund the fund to set
     */
    public void setFund(Fund fund) {
        this.fund = fund;
    }

    /**
     * @return the frame
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(Frame frame) {
        this.frame = frame;
    }

//    /**
//     * @return the signal
//     */
//    public Double getSignal() {
//        return signal;
//    }
//
//    /**
//     * @param signal the signal to set
//     */
//    public void setSignal(Double signal) {
//        this.signal = signal;
//    }
//
//    /**
//     * @return the level
//     */
//    public Integer getLevel() {
//        return level;
//    }
//
//    /**
//     * @param level the level to set
//     */
//    public void setLevel(Integer level) {
//        this.level = level;
//    }
//

    /**
     * @return the service
     */
    public ServiceData getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(ServiceData service) {
        this.service = service;
    }


    /**
     * @return the alpha
     */
    public Double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the derivative
     */
    public Double getDerivative() {
        return derivative;
    }

    /**
     * @param derivative the derivative to set
     */
    public void setDerivative(Double derivative) {
        this.derivative = derivative;
    }

    /**
     * @return the second_derivative
     */
    public Double getSecondDerivative() {
        return secondDerivative;
    }

    /**
     * @param secondDerivative the second_derivative to set
     */
    public void setSecondDerivative(Double secondDerivative) {
        this.secondDerivative = secondDerivative;
    }

    /**
     * @return the angleDegrees
     */
    public Integer getAngleDegrees() {
        return angleDegrees;
    }

    /**
     * @param angleDegrees the angleDegrees to set
     */
    public void setAngleDegrees(Integer angleDegrees) {
        this.angleDegrees = angleDegrees;
    }

    /**
     * @return the setting
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     * @param setting the setting to set
     */
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

//    public Point getPrev() {
//        return prev;
//    }
//
//    public void setPrev(Point prev) {
//        this.prev = prev;
//    }
//
//    public Point getNext() {
//        return next;
//    }
//
//    public void setNext(Point next) {
//        this.next = next;
//    }

    /**
     * @return the prevId
     */
    public Integer getPrevId() {
        return prevId;
    }

    /**
     * @param prevId the prevId to set
     */
    public void setPrevId(Integer prevId) {
        this.prevId = prevId;
    }

    /**
     * @return the nextId
     */
    public Integer getNextId() {
        return nextId;
    }

    /**
     * @param nextId the nextId to set
     */
    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

}
