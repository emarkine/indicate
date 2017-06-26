/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eugenelab.tram.domain;

import com.eugenelab.tram.interfaces.Rateable;
import static com.eugenelab.tram.util.Constant.FORMAT_DATE;
import static com.eugenelab.tram.util.Constant.FORMAT_TIME;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "bars")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bar.findAll", query = "SELECT b FROM Bar b"),
    @NamedQuery(name = "Bar.findById", query = "SELECT b FROM Bar b WHERE b.id = :id"),
//    @NamedQuery(name = "Bar.findByFundId", query = "SELECT b FROM Bar b WHERE b.fundId = :fundId"),
//    @NamedQuery(name = "Bar.findByFrame", query = "SELECT b FROM Bar b WHERE b.frame = :frame"),
    @NamedQuery(name = "Bar.findByTime", query = "SELECT b FROM Bar b WHERE b.time = :time"),
    @NamedQuery(name = "Bar.findByOpenTime", query = "SELECT b FROM Bar b WHERE b.openTime = :openTime"),
    @NamedQuery(name = "Bar.findByCloseTime", query = "SELECT b FROM Bar b WHERE b.closeTime = :closeTime"),
    @NamedQuery(name = "Bar.findByRate", query = "SELECT b FROM Bar b WHERE b.rate = :rate"),
    @NamedQuery(name = "Bar.findByOpen", query = "SELECT b FROM Bar b WHERE b.open = :open"),
    @NamedQuery(name = "Bar.findByClose", query = "SELECT b FROM Bar b WHERE b.close = :close"),
//    @NamedQuery(name = "Bar.findByMax", query = "SELECT b FROM Bar b WHERE b.max = :max"),
//    @NamedQuery(name = "Bar.findByMin", query = "SELECT b FROM Bar b WHERE b.min = :min"),
    @NamedQuery(name = "Bar.findByVolume", query = "SELECT b FROM Bar b WHERE b.volume = :volume")})
//    @NamedQuery(name = "Bar.findByFundFrameTime", query = "SELECT b FROM Bar b WHERE b.fund_id = :fund AND b.frame = :frame AND b.open_time = :time")})
    
public class Bar implements Serializable, Rateable {
    
    private static final long serialVersionUID = 1L;
    @OrderBy("open_time ASC") 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fund_id")
    private Fund fund;
//    @Column(name = "fund_id")
//    private Integer fundId;

    @ManyToOne
    @JoinColumn(name = "frame_id")
    private Frame frame;
//    @Column(name = "frame")
//    private Integer frame;
  
    @Column(name = "time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "ms")
    private Long ms;
    @Column(name = "open_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date openTime;
    @Column(name = "close_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closeTime;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "open")
    private BigDecimal open;
    @Column(name = "close")
    private BigDecimal close;
    @Column(name = "high")
    private BigDecimal high;
    @Column(name = "low")
    private BigDecimal low;
    @Column(name = "volume")
    private Integer volume;
    @Column(name = "last")
    private boolean last;
    
//    @OneToOne
//    @JoinColumn(name = "prev_id")
//    private Bar prev;
//    @OneToOne
//    @JoinColumn(name = "next_id")
//    private Bar next;
    @Column(name = "prev_id")
    private Integer prev_id;
    @Column(name = "next_id")
    private Integer next_id;
    
    
    public Bar() {
    }

    public Bar(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String s = "Bar[" + id + "] " + fund.getName() + " " + frame.getName();
        s += " " + FORMAT_DATE.format(openTime);
        s += " " + FORMAT_TIME.format(openTime);
        s += " " + FORMAT_TIME.format(time);
        s += " " + FORMAT_TIME.format(closeTime);
        s += " open:" + open + " low:" + low + " high:" + high + " close:" + close;
        if (isLast())
            s += " last";
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

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    @Override
    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public void setRate(double rate) {
        this.rate = BigDecimal.valueOf(rate);
    }

    @Override
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = BigDecimal.valueOf(open);
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = BigDecimal.valueOf(close);
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = BigDecimal.valueOf(high);
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = BigDecimal.valueOf(low);
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
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
        if (!(object instanceof Bar)) {
            return false;
        }
        Bar other = (Bar) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the last
     */
    public boolean isLast() {
        return last;
    }

    /**
     * @param last the last to set
     */
    public void setLast(boolean last) {
        this.last = last;
    }

//    /**
//     * @return the prev
//     */
//    public Bar getPrev() {
//        return prev;
//    }
//
//    /**
//     * @param prev the prev to set
//     */
//    public void setPrev(Bar prev) {
//        this.prev = prev;
//    }
//
//    /**
//     * @return the next
//     */
//    public Bar getNext() {
//        return next;
//    }
//
//    /**
//     * @param next the next to set
//     */
//    public void setNext(Bar next) {
//        this.next = next;
//    }

    /**
     * @return the prev_id
     */
    public Integer getPrev_id() {
        return prev_id;
    }

    /**
     * @param prev_id the prev_id to set
     */
    public void setPrev_id(Integer prev_id) {
        this.prev_id = prev_id;
    }

    /**
     * @return the next_id
     */
    public Integer getNext_id() {
        return next_id;
    }

    /**
     * @param next_id the next_id to set
     */
    public void setNext_id(Integer next_id) {
        this.next_id = next_id;
    }

    public boolean isEmpty() {
        return open.doubleValue() == close.doubleValue();
    }

    public boolean isUp() {
        return close.doubleValue() > open.doubleValue();
    }

    public boolean isDown() {
        return close.doubleValue() < open.doubleValue();
    }
    
    public boolean isInside(double rate) {
        return rate >= low.doubleValue() && rate <= high.doubleValue();
    }

    public boolean isAbove(double rate) {
        return rate < low.doubleValue();
    }
            
    public boolean isBelow(double rate) {
        return rate > high.doubleValue();
    }
        
    public long getMs() {
        return ms;
    }

    public void setMs(long ms) {
        this.ms = ms;
    }
        
  
}
