/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eugenelab.tram.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
@Table(name = "funds")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fund.findAll", query = "SELECT f FROM Fund f"),
    @NamedQuery(name = "Fund.findById", query = "SELECT f FROM Fund f WHERE f.id = :id"),
    @NamedQuery(name = "Fund.findByName", query = "SELECT f FROM Fund f WHERE f.name = :name"),
    @NamedQuery(name = "Fund.findByFullName", query = "SELECT f FROM Fund f WHERE f.fullName = :fullName"),
    @NamedQuery(name = "Fund.findByIsin", query = "SELECT f FROM Fund f WHERE f.isin = :isin"),
    @NamedQuery(name = "Fund.findByProduct", query = "SELECT f FROM Fund f WHERE f.product = :product"),
    @NamedQuery(name = "Fund.findByLocalSymbol", query = "SELECT f FROM Fund f WHERE f.localSymbol = :localSymbol"),
    @NamedQuery(name = "Fund.findByCategory", query = "SELECT f FROM Fund f WHERE f.category = :category"),
    @NamedQuery(name = "Fund.findByExchange", query = "SELECT f FROM Fund f WHERE f.exchange = :exchange"),
    @NamedQuery(name = "Fund.findByPrimaryExchange", query = "SELECT f FROM Fund f WHERE f.primaryExchange = :primaryExchange"),
    @NamedQuery(name = "Fund.findByLocation", query = "SELECT f FROM Fund f WHERE f.location = :location"),
    @NamedQuery(name = "Fund.findBySector", query = "SELECT f FROM Fund f WHERE f.sector = :sector"),
    @NamedQuery(name = "Fund.findByIndustry", query = "SELECT f FROM Fund f WHERE f.industry = :industry"),
    @NamedQuery(name = "Fund.findByMarketCategory", query = "SELECT f FROM Fund f WHERE f.marketCategory = :marketCategory"),
    @NamedQuery(name = "Fund.findBySecurityType", query = "SELECT f FROM Fund f WHERE f.securityType = :securityType"),
    @NamedQuery(name = "Fund.findByUnderlying", query = "SELECT f FROM Fund f WHERE f.underlying = :underlying"),
    @NamedQuery(name = "Fund.findByContractMonth", query = "SELECT f FROM Fund f WHERE f.contractMonth = :contractMonth"),
    @NamedQuery(name = "Fund.findByExpiration", query = "SELECT f FROM Fund f WHERE f.expiration = :expiration"),
    @NamedQuery(name = "Fund.findByMultiplier", query = "SELECT f FROM Fund f WHERE f.multiplier = :multiplier"),
//    @NamedQuery(name = "Fund.findByStrike", query = "SELECT f FROM Fund f WHERE f.strike = :strike"),
    @NamedQuery(name = "Fund.findByDirection", query = "SELECT f FROM Fund f WHERE f.direction = :direction"),
    @NamedQuery(name = "Fund.findByExerciseStyle", query = "SELECT f FROM Fund f WHERE f.exerciseStyle = :exerciseStyle"),
    @NamedQuery(name = "Fund.findByTradingClass", query = "SELECT f FROM Fund f WHERE f.tradingClass = :tradingClass"),
    @NamedQuery(name = "Fund.findByProductType", query = "SELECT f FROM Fund f WHERE f.productType = :productType"),
    @NamedQuery(name = "Fund.findByIssuer", query = "SELECT f FROM Fund f WHERE f.issuer = :issuer"),
    @NamedQuery(name = "Fund.findBySettlementMethod", query = "SELECT f FROM Fund f WHERE f.settlementMethod = :settlementMethod"),
    @NamedQuery(name = "Fund.findByOpenTime", query = "SELECT f FROM Fund f WHERE f.openTime = :openTime"),
    @NamedQuery(name = "Fund.findByCloseTime", query = "SELECT f FROM Fund f WHERE f.closeTime = :closeTime"),
    @NamedQuery(name = "Fund.findByUrl", query = "SELECT f FROM Fund f WHERE f.url = :url"),
    @NamedQuery(name = "Fund.findByXpathRate", query = "SELECT f FROM Fund f WHERE f.xpathRate = :xpathRate"),
    @NamedQuery(name = "Fund.findByXpathVolume", query = "SELECT f FROM Fund f WHERE f.xpathVolume = :xpathVolume"),
    @NamedQuery(name = "Fund.findByXpathBid", query = "SELECT f FROM Fund f WHERE f.xpathBid = :xpathBid"),
    @NamedQuery(name = "Fund.findByXpathAsk", query = "SELECT f FROM Fund f WHERE f.xpathAsk = :xpathAsk"),
    @NamedQuery(name = "Fund.findByXpathBidVolume", query = "SELECT f FROM Fund f WHERE f.xpathBidVolume = :xpathBidVolume"),
    @NamedQuery(name = "Fund.findByXpathAskVolume", query = "SELECT f FROM Fund f WHERE f.xpathAskVolume = :xpathAskVolume")})
public class Fund implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "isin")
    private String isin;
    @Column(name = "product")
    private String product;
    @Column(name = "local_symbol")
    private String localSymbol;
    @Column(name = "category")
    private String category;
    
    @ManyToOne
    @JoinColumn(name="currency_id")
    private Currency currency;
//    @Column(name = "currency_id")
//    private Integer currencyId;

    @Column(name = "exchange")
    private String exchange;
    @Column(name = "primary_exchange")
    private String primaryExchange;
    @Column(name = "location")
    private String location;
    @Column(name = "sector")
    private String sector;
    @Column(name = "industry")
    private String industry;
    @Column(name = "market_category")
    private String marketCategory;
    @Column(name = "security_type")
    private String securityType;
    @Column(name = "underlying")
    private String underlying;
    @Column(name = "contract_month")
    @Temporal(TemporalType.DATE)
    private Date contractMonth;
    @Column(name = "expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;
    @Column(name = "multiplier")
    private Integer multiplier;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
//    @Column(name = "strike")
//    private BigDecimal strike;
    @Column(name = "direction")
    private String direction;
    @Column(name = "exercise_style")
    private String exerciseStyle;
    @Column(name = "trading_class")
    private String tradingClass;
    @Column(name = "product_type")
    private String productType;
    @Column(name = "issuer")
    private String issuer;
    @Column(name = "comma")
    private Integer comma;
    @Column(name = "settlement_method")
    private String settlementMethod;
    @Column(name = "open_time")
    @Temporal(TemporalType.TIME)
    private Date openTime;
    @Column(name = "close_time")
    @Temporal(TemporalType.TIME)
    private Date closeTime;
    @Column(name = "watch")
    private boolean watch;
    @Column(name = "url")
    private String url;
    @Column(name = "xpath_rate")
    private String xpathRate;
    @Column(name = "xpath_volume")
    private String xpathVolume;
    @Column(name = "xpath_bid")
    private String xpathBid;
    @Column(name = "xpath_ask")
    private String xpathAsk;
    @Column(name = "xpath_bid_volume")
    private String xpathBidVolume;
    @Column(name = "xpath_ask_volume")
    private String xpathAskVolume;
//    @Lob
//    @Column(name = "properties")
//    private String properties;

    public Fund() {
    }

    public Fund(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Fund["+id+"] "+name;
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
        if (!(object instanceof Fund)) {
            return false;
        }
        Fund other = (Fund) object;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLocalSymbol() {
        return localSymbol;
    }

    public void setLocalSymbol(String localSymbol) {
        this.localSymbol = localSymbol;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

//    public Integer getCurrencyId() {
//        return currencyId;
//    }
//
//    public void setCurrencyId(Integer currencyId) {
//        this.currencyId = currencyId;
//    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getPrimaryExchange() {
        return primaryExchange;
    }

    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getMarketCategory() {
        return marketCategory;
    }

    public void setMarketCategory(String marketCategory) {
        this.marketCategory = marketCategory;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public String getUnderlying() {
        return underlying;
    }

    public void setUnderlying(String underlying) {
        this.underlying = underlying;
    }

    public Date getContractMonth() {
        return contractMonth;
    }

    public String getContractMonthString() {
        if (contractMonth != null)
            return new SimpleDateFormat("yyyyMM").format(contractMonth);
        else
            return null;
    }
        
              
    public void setContractMonth(Date contractMonth) {
        this.contractMonth = contractMonth;
    }

   
    public Date getExpiration() {
        return expiration;
    }

    public String getExpirationString() {
        if (expiration != null)
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expiration);
        else
            return null;
    }
    
            
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

//    public BigDecimal getStrike() {
//        return strike;
//    }
//
//    public void setStrike(BigDecimal strike) {
//        this.strike = strike;
//    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getExerciseStyle() {
        return exerciseStyle;
    }

    public void setExerciseStyle(String exerciseStyle) {
        this.exerciseStyle = exerciseStyle;
    }

    public String getTradingClass() {
        return tradingClass;
    }

    public void setTradingClass(String tradingClass) {
        this.tradingClass = tradingClass;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSettlementMethod() {
        return settlementMethod;
    }

    public void setSettlementMethod(String settlementMethod) {
        this.settlementMethod = settlementMethod;
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

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getXpathRate() {
        return xpathRate;
    }

    public void setXpathRate(String xpathRate) {
        this.xpathRate = xpathRate;
    }

    public String getXpathVolume() {
        return xpathVolume;
    }

    public void setXpathVolume(String xpathVolume) {
        this.xpathVolume = xpathVolume;
    }

    public String getXpathBid() {
        return xpathBid;
    }

    public void setXpathBid(String xpathBid) {
        this.xpathBid = xpathBid;
    }

    public String getXpathAsk() {
        return xpathAsk;
    }

    public void setXpathAsk(String xpathAsk) {
        this.xpathAsk = xpathAsk;
    }

    public String getXpathBidVolume() {
        return xpathBidVolume;
    }

    public void setXpathBidVolume(String xpathBidVolume) {
        this.xpathBidVolume = xpathBidVolume;
    }

    public String getXpathAskVolume() {
        return xpathAskVolume;
    }

    public void setXpathAskVolume(String xpathAskVolume) {
        this.xpathAskVolume = xpathAskVolume;
    }

//    public String getProperties() {
//        return properties;
//    }
//
//    public void setProperties(String properties) {
//        this.properties = properties;
//    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the comma
     */
    public Integer getComma() {
        return comma;
    }

    /**
     * @param comma the comma to set
     */
    public void setComma(Integer comma) {
        this.comma = comma;
    }
    
}
