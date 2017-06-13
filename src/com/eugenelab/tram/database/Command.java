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
@Table(name = "commands")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Command.findAll", query = "SELECT c FROM Command c"),
    @NamedQuery(name = "Command.findById", query = "SELECT c FROM Command c WHERE c.id = :id"),
    @NamedQuery(name = "Command.findByName", query = "SELECT c FROM Command c WHERE c.name = :name"),
//    @NamedQuery(name = "Command.findByAction", query = "SELECT c FROM Command c WHERE c.action = :action"),
    @NamedQuery(name = "Command.findByCreatedAt", query = "SELECT c FROM Command c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Command.findByUpdatedAt", query = "SELECT c FROM Command c WHERE c.updatedAt = :updatedAt")})
public class Command implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
//    @Column(name = "action")
//    private String action;
    
    
    @ManyToOne
    @JoinColumn(name="fund_id")
    private Fund fund;
    @ManyToOne
    @JoinColumn(name="frame_id")
    private Frame frame;
    @ManyToOne
    @JoinColumn(name="service_id")
    private ServiceData service;
    
//    @Column(name = "success")
//    private boolean success;
//    @Column(name = "error")
//    private boolean error;
    @Column(name = "status")
    private String status;
    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Command() {
    }

    public Command(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Command[" + id + "] " + name + ", " +
                "service: " + getService().getName() + ", " +
//                "fund: " + getFund().getName() + ", " +
                "frame: " + getFrame().getName() + ", " +
                "status: " + status + ", " +
                "created: " + FORMAT.format(createdAt);
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
//
//    public String getAction() {
//        return action;
//    }
//
//    public void setAction(String action) {
//        this.action = action;
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
        if (!(object instanceof Command)) {
            return false;
        }
        Command other = (Command) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

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
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

  
 
}
