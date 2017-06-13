/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import static com.eugenelab.tram.util.Constant.FORMAT;
import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "responses")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Response.findAll", query = "SELECT r FROM Response r"),
    @NamedQuery(name = "Response.findById", query = "SELECT r FROM Response r WHERE r.id = :id"),
    @NamedQuery(name = "Response.findByValue", query = "SELECT r FROM Response r WHERE r.value = :value")})
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
//    @Basic(optional = false)
//    @Column(name = "nerve_id")
//    private int nerveId;
//    @Column(name = "fund_id")
//    private Integer fundId;
//    @Column(name = "frame_id")
//    private Integer frameId;
    
    @JoinColumn(name = "fund_id")
    @ManyToOne
    private Fund fund;
    
    @JoinColumn(name = "frame_id")
    @ManyToOne
    private Frame frame;

    @ManyToOne
    @JoinColumn(name = "nerve_id", nullable = false)
    private Nerve nerve;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    private Float value;

    public Response() {
    }

    public Response(Integer id) {
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
        if (!(object instanceof Response)) {
            return false;
        }
        Response other = (Response) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "Response[" + id + "], ";
        s += ", nerve: " + nerve;
//        s += ", nerve: " + nerve.getId();
        if (fund != null) {
            s += ", fund: " + fund.getName();
        }
        if (frame != null) {
            s += ", frame: " + frame.getName();
        }
        s += ", value: " + value;
        return s;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    
}
