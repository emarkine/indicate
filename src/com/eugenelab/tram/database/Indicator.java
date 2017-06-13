/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eugene
 */
@Entity
@Table(name = "indicators")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Indicator.findAll", query = "SELECT i FROM Indicator i"),
    @NamedQuery(name = "Indicator.findById", query = "SELECT i FROM Indicator i WHERE i.id = :id"),
    @NamedQuery(name = "Indicator.findByType", query = "SELECT i FROM Indicator i WHERE i.type = :type"),
    @NamedQuery(name = "Indicator.findByName", query = "SELECT i FROM Indicator i WHERE i.name = :name"),
    @NamedQuery(name = "Indicator.findByTitle", query = "SELECT i FROM Indicator i WHERE i.title = :title"),
    @NamedQuery(name = "Indicator.findByFullName", query = "SELECT i FROM Indicator i WHERE i.fullName = :fullName"),
    @NamedQuery(name = "Indicator.findByCanvas", query = "SELECT i FROM Indicator i WHERE i.canvas = :canvas"),
    @NamedQuery(name = "Indicator.findByLink", query = "SELECT i FROM Indicator i WHERE i.link = :link")})
public class Indicator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "type")
    private String type;
    @Column(name = "name")
    protected String name;
    @Column(name = "title")
    private String title;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "canvas")
    private String canvas;
    @Column(name = "link")
    private String link;
    
//    @OneToMany(mappedBy="settings")
//    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="id")
//    public List<Setting> settings;

    

    public Indicator() {
    }

    public Indicator(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Indicator[" + id + "] "+name;
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
        if (!(object instanceof Indicator)) {
            return false;
        }
        Indicator other = (Indicator) object;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCanvas() {
        return canvas;
    }

    public void setCanvas(String canvas) {
        this.canvas = canvas;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
