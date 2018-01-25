/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "hosts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Node.findAll", query = "SELECT n FROM Node n"),
    @NamedQuery(name = "Node.findById", query = "SELECT n FROM Node n WHERE n.id = :id"),
    @NamedQuery(name = "Node.findByName", query = "SELECT n FROM Node n WHERE n.name = :name"),
    @NamedQuery(name = "Node.findByIp", query = "SELECT n FROM Node n WHERE n.ip = :ip"),
    @NamedQuery(name = "Node.findByProcessor", query = "SELECT n FROM Node n WHERE n.processor = :processor"),
    @NamedQuery(name = "Node.findByRam", query = "SELECT n FROM Node n WHERE n.ram = :ram"),
    @NamedQuery(name = "Node.findByHd", query = "SELECT n FROM Node n WHERE n.hd = :hd"),
    @NamedQuery(name = "Node.findByOs", query = "SELECT n FROM Node n WHERE n.os = :os"),
    @NamedQuery(name = "Node.findByOsKernel", query = "SELECT n FROM Node n WHERE n.osKernel = :osKernel"),
    @NamedQuery(name = "Node.findByOsName", query = "SELECT n FROM Node n WHERE n.osName = :osName"),
    @NamedQuery(name = "Node.findByOsVersion", query = "SELECT n FROM Node n WHERE n.osVersion = :osVersion"),
    @NamedQuery(name = "Node.findByLocation", query = "SELECT n FROM Node n WHERE n.location = :location"),
    @NamedQuery(name = "Node.findByUser", query = "SELECT n FROM Node n WHERE n.user = :user")})

public class Node implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Column(name = "name")
    private String name;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy="edge", cascade=CascadeType.ALL)
    private List<Nerve> nerves;

    public Node() {
    }

    public Node(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node[" + id + "]: " + name + ", title: " + title;
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
        if (!(object instanceof Node)) {
            return false;
        }
        Node other = (Node) object;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the nerves
     */
    public List<Nerve> getNerves() {
        return nerves;
    }

    /**
     * @param nerves the nerves to set
     */
    public void setNerves(List<Nerve> nerves) {
        this.nerves = nerves;
    }

}
