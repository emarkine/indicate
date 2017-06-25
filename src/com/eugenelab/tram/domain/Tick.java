/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eugenelab.tram.domain;

import com.eugenelab.tram.interfaces.Rateable;
import static com.eugenelab.tram.util.Constant.FORMAT_MS;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "ticks")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tick.findAll", query = "SELECT t FROM Tick t"),
    @NamedQuery(name = "Tick.findById", query = "SELECT t FROM Tick t WHERE t.id = :id"),
//    @NamedQuery(name = "Tick.findByFundId", query = "SELECT t FROM Tick t WHERE t.fundId = :fundId"),
    @NamedQuery(name = "Tick.findByTime", query = "SELECT t FROM Tick t WHERE t.time = :time"),
    @NamedQuery(name = "Tick.findByRate", query = "SELECT t FROM Tick t WHERE t.rate = :rate"),
    @NamedQuery(name = "Tick.findByVolume", query = "SELECT t FROM Tick t WHERE t.volume = :volume"),
    @NamedQuery(name = "Tick.findByBid", query = "SELECT t FROM Tick t WHERE t.bid = :bid"),
    @NamedQuery(name = "Tick.findByAsk", query = "SELECT t FROM Tick t WHERE t.ask = :ask"),
    @NamedQuery(name = "Tick.findByBidSize", query = "SELECT t FROM Tick t WHERE t.bidSize = :bidSize"),
    @NamedQuery(name = "Tick.findByAskSize", query = "SELECT t FROM Tick t WHERE t.askSize = :askSize"),
    @NamedQuery(name = "Tick.findByLastSize", query = "SELECT t FROM Tick t WHERE t.lastSize = :lastSize")})
public class Tick implements Serializable, Rateable, Comparable<Tick> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
//    @Column(name = "fund_id")
//    private Integer fundId;
    @ManyToOne
    @JoinColumn(name = "fund_id")
    private Fund fund;
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "ms")
    private Long ms;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "volume")
    private Integer volume;
    @Column(name = "bid")
    private BigDecimal bid;
    @Column(name = "ask")
    private BigDecimal ask;
    @Column(name = "bid_size")
    private Integer bidSize;
    @Column(name = "ask_size")
    private Integer askSize;
    @Column(name = "last_size")
    private Integer lastSize;

    public Tick() {
    }

    public Tick(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tick[" + id + "] " + fund.getName() + "\t" + volume +
               "\t" + rate + "\t" + FORMAT_MS.format(new Date(ms));
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
        return rate;
    }

    @Override
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    @Override
    public void setRate(double rate) {
        this.rate = BigDecimal.valueOf(rate);
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public Integer getBidSize() {
        return bidSize;
    }

    public void setBidSize(Integer bidSize) {
        this.bidSize = bidSize;
    }

    public Integer getAskSize() {
        return askSize;
    }

    public void setAskSize(Integer askSize) {
        this.askSize = askSize;
    }

    public Integer getLastSize() {
        return lastSize;
    }

    public void setLastSize(Integer lastSize) {
        this.lastSize = lastSize;
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
        if (!(object instanceof Tick)) {
            return false;
        }
        Tick other = (Tick) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    
    @Override
    public int compareTo(Tick t) {
        double diff = this.rate.doubleValue() - t.getRate().doubleValue();
        if (diff > 0) 
            return 1;
        if (diff < 0)
            return -1;
        return 0;
    }

    /**
     * @return the ms
     */
    public long getMs() {
        return ms.longValue();
    }

    /**
     * @param ms the ms to set
     */
    public void setMs(long ms) {
        this.ms = ms;
    }


}
