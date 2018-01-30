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
    @NamedQuery(name = "Node.findByName", query = "SELECT n FROM Node n WHERE n.name = :name")})

public class Node implements Serializable {

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "file")
    private String file;
    
//    @OneToMany(mappedBy="node", cascade=CascadeType.ALL)
//    private List<Nerve> nerves;

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

//    /**
//     * @return the nerves
//     */
//    public List<Nerve> getNerves() {
//        return nerves;
//    }
//
//    /**
//     * @param nerves the nerves to set
//     */
//    public void setNerves(List<Nerve> nerves) {
//        this.nerves = nerves;
//    }

}
