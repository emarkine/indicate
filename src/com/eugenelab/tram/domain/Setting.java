/*
 * Copyright (c) 2004 - 2015, EugeneLab. All Rights Reserved
 */
package com.eugenelab.tram.domain;

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
@Table(name = "settings")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Setting.findAll", query = "SELECT i FROM Setting i"),
    @NamedQuery(name = "Setting.findById", query = "SELECT i FROM Setting i WHERE i.id = :id"),
    @NamedQuery(name = "Setting.findByName", query = "SELECT i FROM Setting i WHERE i.name = :name"),
//    @NamedQuery(name = "Setting.findByIndicatorId", query = "SELECT i FROM Setting i WHERE i.indicatorId = :indicatorId"),
//    @NamedQuery(name = "Setting.findByFrameId", query = "SELECT i FROM Setting i WHERE i.frameId = :frameId"),
    @NamedQuery(name = "Setting.findBySource", query = "SELECT i FROM Setting i WHERE i.source = :source"),
    @NamedQuery(name = "Setting.findByPeriod", query = "SELECT i FROM Setting i WHERE i.period = :period"),
    @NamedQuery(name = "Setting.findByMethod", query = "SELECT i FROM Setting i WHERE i.method = :method"),
    @NamedQuery(name = "Setting.findByColor", query = "SELECT i FROM Setting i WHERE i.color = :color"),
    @NamedQuery(name = "Setting.findByFill", query = "SELECT i FROM Setting i WHERE i.fill = :fill"),
    @NamedQuery(name = "Setting.findByLineWidth", query = "SELECT i FROM Setting i WHERE i.lineWidth = :lineWidth"),
    @NamedQuery(name = "Setting.findByLineDash", query = "SELECT i FROM Setting i WHERE i.lineDash = :lineDash"),
    @NamedQuery(name = "Setting.findByRadius", query = "SELECT i FROM Setting i WHERE i.radius = :radius"),
//    @NamedQuery(name = "Setting.findByLevel", query = "SELECT i FROM Setting i WHERE i.level = :level"),
//    @NamedQuery(name = "Setting.findByDepth", query = "SELECT i FROM Setting i WHERE i.depth = :depth"),
//    @NamedQuery(name = "Setting.findByLastDepth", query = "SELECT i FROM Setting i WHERE i.lastDepth = :lastDepth"),
    @NamedQuery(name = "Setting.findByIncrementalStep", query = "SELECT i FROM Setting i WHERE i.incrementalStep = :incrementalStep"),
    @NamedQuery(name = "Setting.findByInitialStep", query = "SELECT i FROM Setting i WHERE i.initialStep = :initialStep"),
    @NamedQuery(name = "Setting.findByMaximumStep", query = "SELECT i FROM Setting i WHERE i.maximumStep = :maximumStep"),
    @NamedQuery(name = "Setting.findByStandardDeviations", query = "SELECT i FROM Setting i WHERE i.standardDeviations = :standardDeviations")})
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "indicator_id")
    private Indicator indicator;
//    @Column(name = "indicator_id")
//    private Integer indicatorId;

//    @ManyToOne
//    @JoinColumn(name = "frame_id")
//    private Frame frame;
//    @Column(name = "frame_id")
//    private Integer frameId;

    @Column(name = "source")
    private String source;
    @Column(name = "period")
    private int period;
    @Column(name = "method")
    private String method;
    @Column(name = "color")
    private String color;
    @Column(name = "fill")
    private String fill;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "line_width")
    private Float lineWidth;
    @Column(name = "line_dash")
    private Integer lineDash;
    @Column(name = "radius")
    private Float radius;
    @Column(name = "level_depth")
    private Integer level_depth;
//    @Column(name = "depth")
//    private Integer depth;
//    @Column(name = "last_depth")
//    private Integer lastDepth;
    @Column(name = "incremental_step")
    private Float incrementalStep;
    @Column(name = "initial_step")
    private Float initialStep;
    @Column(name = "maximum_step")
    private Float maximumStep;
    @Column(name = "standard_deviations")
    private Float standardDeviations;
    
    @Column(name = "first")
    private String first;
    @Column(name = "second")
    private String second;

    @Column(name = "delta")
    private Integer delta;

    
    public Setting() {
    }

    public Setting(Integer id) {
        this.id = id;
    }

    
    @Override
    public String toString() {
        return "Set[" + id + "] "+name;
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
        if (!(object instanceof Setting)) {
            return false;
        }
        Setting other = (Setting) object;
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

//    public Integer getIndicatorId() {
//        return indicatorId;
//    }
//
//    public void setIndicatorId(Integer indicatorId) {
//        this.indicatorId = indicatorId;
//    }
//
//    public Integer getFrameId() {
//        return frameId;
//    }
//
//    public void setFrameId(Integer frameId) {
//        this.frameId = frameId;
//    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public Float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(Float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Integer getLineDash() {
        return lineDash;
    }

    public void setLineDash(Integer lineDash) {
        this.lineDash = lineDash;
    }

    public Float getRadius() {
        return radius;
    }

    public void setRadius(Float radius) {
        this.radius = radius;
    }

    public Integer getLevelDepth() {
        return level_depth;
    }

    public void setLevel(Integer level_depth) {
        this.level_depth = level_depth;
    }
//
//    public Integer getDepth() {
//        return depth;
//    }
//
//    public void setDepth(Integer depth) {
//        this.depth = depth;
//    }
//
//    public Integer getLastDepth() {
//        return lastDepth;
//    }
//
//    public void setLastDepth(Integer lastDepth) {
//        this.lastDepth = lastDepth;
//    }

    public Float getIncrementalStep() {
        return incrementalStep;
    }

    public void setIncrementalStep(Float incrementalStep) {
        this.incrementalStep = incrementalStep;
    }

    public Float getInitialStep() {
        return initialStep;
    }

    public void setInitialStep(Float initialStep) {
        this.initialStep = initialStep;
    }

    public Float getMaximumStep() {
        return maximumStep;
    }

    public void setMaximumStep(Float maximumStep) {
        this.maximumStep = maximumStep;
    }

    public Float getStandardDeviations() {
        return standardDeviations;
    }

    public void setStandardDeviations(Float standardDeviations) {
        this.standardDeviations = standardDeviations;
    }


    /**
     * @return the indicator
     */
    public Indicator getIndicator() {
        return indicator;
    }

    /**
     * @param indicator the indicator to set
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

//    /**
//     * @return the frame
//     */
//    public Frame getFrame() {
//        return frame;
//    }
//
//    /**
//     * @param frame the frame to set
//     */
//    public void setFrame(Frame frame) {
//        this.frame = frame;
//    }

    /**
     * @return the first
     */
    public String getFirst() {
        return first;
    }

    /**
     * @param first the first to set
     */
    public void setFirst(String first) {
        this.first = first;
    }

    /**
     * @return the second
     */
    public String getSecond() {
        return second;
    }

    /**
     * @param second the second to set
     */
    public void setSecond(String second) {
        this.second = second;
    }

    /**
     * @return the delta
     */
    public Integer getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(Integer delta) {
        this.delta = delta;
    }

}
