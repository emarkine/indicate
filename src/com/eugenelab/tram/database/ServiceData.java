/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import java.io.Serializable;
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
@Table(name = "services")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceData.findAll", query = "SELECT s FROM ServiceData s"),
    @NamedQuery(name = "ServiceData.findById", query = "SELECT s FROM ServiceData s WHERE s.id = :id"),
    @NamedQuery(name = "ServiceData.findByName", query = "SELECT s FROM ServiceData s WHERE s.name = :name"),
//    @NamedQuery(name = "ServiceData.findByStatus", query = "SELECT s FROM ServiceData s WHERE s.status = :status"),
//    @NamedQuery(name = "Service.findByFundId", query = "SELECT s FROM Service s WHERE s.fundId = :fundId"),
//    @NamedQuery(name = "Service.findByFrameId", query = "SELECT s FROM Service s WHERE s.frameId = :frameId"),
    @NamedQuery(name = "ServiceData.findByCreatedAt", query = "SELECT s FROM ServiceData s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "ServiceData.findByUpdatedAt", query = "SELECT s FROM ServiceData s WHERE s.updatedAt = :updatedAt")})
public class ServiceData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    protected String name;
    @Column(name = "ngroup")
    private String ngroup;
    @Column(name = "position")
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

//    @Column(name = "status")
//    protected String status;
    @Column(name = "action")
    private String action;
    @Column(name = "active")
    private boolean active;
    @Column(name = "single")
    private boolean single;
    @Column(name = "refresh")
    private Integer refresh;
//    @Column(name = "delta")
//    private Integer delta;

    @ManyToOne
    @JoinColumn(name = "fund_id")
    protected Fund fund;
    @ManyToOne
    @JoinColumn(name = "frame_id")
    protected Frame frame;

    @ManyToOne
    @JoinColumn(name = "trigger_id")
    private ServiceData trigger;

    @ManyToOne
    @JoinColumn(name = "setting_id")
    protected Setting setting;

//    @Column(name = "date")
//    @Temporal(TemporalType.DATE)
//    @Column(name = "start_time")
//    @Temporal(TemporalType.TIME)
//    private Date startTime;
//    @Column(name = "stop_time")
//    @Temporal(TemporalType.TIME)
//    private Date stopTime;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public ServiceData() {
    }

    public ServiceData(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String s = "ServiceData[" + id + "] " + name;
        if (active) {
            s += ", active";
        }
        if (refresh != null) {
            s += ", refresh: " + refresh;
        }
        s += "\n " + host;
        s += "\n " + fund;
        s += "\n " + frame;
//        s += "\n " + indicator;
        s += "\n " + setting;
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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
        if (!(object instanceof ServiceData)) {
            return false;
        }
        ServiceData other = (ServiceData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

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

    /**
     * @return the Setting
     */
    public Setting getSetting() {
        return setting;
    }

    /**
     * @param Setting the Setting to set
     */
    public void setSetting(Setting Setting) {
        this.setting = Setting;
    }


//    public void start(EntityManager manager) {
//        long time = (new Date()).getTime();
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
//        String timeStr = dateFormat.format(time);
//        System.out.println( this + ", started at " + timeStr);
//        if (name.equals("rate")) {
//            initRate(manager);
//        }
//    }
//   
//    public void initRate(EntityManager manager) {
//        int period = setting.getPeriod(); 
//        long shift = (frame.getId() * 100 + period) * 1000L; // сдвиг во времени в ms
//        Date t2 = new Date(); // время в данный момент
//        Date t1 = new Date(t2.getTime() - shift); // время начала графика
//        System.out.println( this + ", begin_time: " + FORMAT.format(t1)+ ", end_time: " + FORMAT.format(t2));
//        RateService service = new RateService(manager);
//        service.init(fund, t1, t2);
//    }
//    protected  EntityManager manager;
//    protected Fund fund;
//    protected Frame frame;
//    public void init(Frame frameView) {
//        this.manager = manager;
//        this.fund = fund;
//        this.frame = setting.getFrame();
//    }
    /**
     * @return the refresh
     */
    public Integer getRefresh() {
        return refresh;
    }

    /**
     * @param refresh the refresh to set
     */
    public void setRefresh(Integer refresh) {
        this.refresh = refresh;
    }

    /**
     * @return the host
     */
    public Host getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(Host host) {
        this.host = host;
    }

//    /**
//     * @return the startTime
//     */
//    public Date getStartTime() {
//        try {
//            if (startTime == null) {
//                return null;
//            } else {
//                return FORMAT_DATE_TIME.parse(FORMAT_DATE.format(date) + " " + FORMAT_TIME.format(startTime));
//            }
//            
//        } catch (ParseException ex) {
//            Logger.getLogger(ServiceData.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//    }

//    /**
//     * @param startTime the startTime to set
//     */
//    public void setStartTime(Date startTime) {
//        this.startTime = startTime;
//    }
//
//    /**
//     * @return the stopTime
//     */
//    public Date getStopTime() {
//        try {
//            return FORMAT_DATE_TIME.parse(FORMAT_DATE.format(date) + " " + FORMAT_TIME.format(stopTime));
//        } catch (ParseException ex) {
//            Logger.getLogger(ServiceData.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//    }
//
//    /**
//     * @param stopTime the stopTime to set
//     */
//    public void setStopTime(Date stopTime) {
//        this.stopTime = stopTime;
//    }

    public String getGroup() {
        return ngroup;
    }

    public void setGroup(String group) {
        this.ngroup = group;
    }

//    /**
//     * @return the delta
//     */
//    public Integer getDelta() {
//        return delta;
//    }
//
//    /**
//     * @param delta the delta to set
//     */
//    public void setDelta(Integer delta) {
//        this.delta = delta;
//    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ServiceData getTrigger() {
        return trigger;
    }

    public void setTrigger(ServiceData trigger) {
        this.trigger = trigger;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }


}
