/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.domain;

import com.eugenelab.tram.util.Constant;
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
@Table(name = "states")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "State.findAll", query = "SELECT h FROM State h"),
    @NamedQuery(name = "State.findById", query = "SELECT h FROM State h WHERE h.id = :id")})
public class State implements Serializable {
    private static final long serialVersionUID = 1L;
   
    public static final String LOAD = "load";
    public static final String START = "start";
    public static final String RUN = "run";
    public static final String CHECK = "check";
    public static final String STOP = "stop";
    public static final String UNLOAD = "unload";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
//    @Column(name = "status")
//    private String status;
    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceData service;
    
    @Column(name = "ms")
    private Long ms;


    public State() {
    }

    public State(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "State[" + id + "]: " + name + ", service: " + getService().getName() + ", ms: " + getMs();
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
        if (!(object instanceof State)) {
            return false;
        }
        State other = (State) object;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     * @return the ms
     */
    public Long getMs() {
        return ms;
    }

    /**
     * @param ms the ms to set
     */
    public void setMs(Long ms) {
        this.ms = ms;
    }


}
