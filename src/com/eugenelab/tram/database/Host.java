/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.database;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = "Host.findAll", query = "SELECT h FROM Host h"),
    @NamedQuery(name = "Host.findById", query = "SELECT h FROM Host h WHERE h.id = :id"),
    @NamedQuery(name = "Host.findByName", query = "SELECT h FROM Host h WHERE h.name = :name"),
    @NamedQuery(name = "Host.findByIp", query = "SELECT h FROM Host h WHERE h.ip = :ip"),
    @NamedQuery(name = "Host.findByProcessor", query = "SELECT h FROM Host h WHERE h.processor = :processor"),
    @NamedQuery(name = "Host.findByRam", query = "SELECT h FROM Host h WHERE h.ram = :ram"),
    @NamedQuery(name = "Host.findByHd", query = "SELECT h FROM Host h WHERE h.hd = :hd"),
    @NamedQuery(name = "Host.findByOs", query = "SELECT h FROM Host h WHERE h.os = :os"),
    @NamedQuery(name = "Host.findByOsKernel", query = "SELECT h FROM Host h WHERE h.osKernel = :osKernel"),
    @NamedQuery(name = "Host.findByOsName", query = "SELECT h FROM Host h WHERE h.osName = :osName"),
    @NamedQuery(name = "Host.findByOsVersion", query = "SELECT h FROM Host h WHERE h.osVersion = :osVersion"),
    @NamedQuery(name = "Host.findByLocation", query = "SELECT h FROM Host h WHERE h.location = :location"),
    @NamedQuery(name = "Host.findByUser", query = "SELECT h FROM Host h WHERE h.user = :user")})
public class Host implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "ip")
    private String ip;
    @Column(name = "speed")
    private String speed;
    @Column(name = "processor")
    private String processor;
    @Column(name = "ram")
    private String ram;
    @Column(name = "hd")
    private String hd;
    @Column(name = "os")
    private String os;
    @Column(name = "os_kernel")
    private String osKernel;
    @Column(name = "os_name")
    private String osName;
    @Column(name = "os_version")
    private String osVersion;
    @Column(name = "location")
    private String location;
    @Column(name = "user")
    private String user;
    @Lob
    @Column(name = "ssh_public_key")
    private String sshPublicKey;

    public Host() {
    }

    public Host(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Host[" + id + "]: " + name + ", ip: " + ip + ", os: " + osName + ", location: " + location;
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
        if (!(object instanceof Host)) {
            return false;
        }
        Host other = (Host) object;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsKernel() {
        return osKernel;
    }

    public void setOsKernel(String osKernel) {
        this.osKernel = osKernel;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSshPublicKey() {
        return sshPublicKey;
    }

    public void setSshPublicKey(String sshPublicKey) {
        this.sshPublicKey = sshPublicKey;
    }

}
