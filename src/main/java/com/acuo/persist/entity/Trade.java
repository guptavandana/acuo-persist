package com.acuo.persist.entity;

import com.acuo.persist.neo4j.converters.CurrencyConverter;
import com.acuo.persist.neo4j.converters.LocalDateConverter;
import com.opengamma.strata.basics.currency.Currency;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"valuation"})
@ToString(exclude = {"valuation"})
public abstract class Trade<T extends Trade> extends Entity implements Comparable<T> {

    private String underlyingAssetId;

    private Double notional;

    private  String buySellProtection;

    private Double couponRate;

    @Convert(LocalDateConverter.class)
    private LocalDate tradeDate;

    @Convert(LocalDateConverter.class)
    private LocalDate maturity;

    @Convert(LocalDateConverter.class)
    private LocalDate clearingDate;

    @Convert(CurrencyConverter.class)
    private Currency currency;

    private String underlyingEntity;

    private Double factor;

    private String seniority;

    @Index(unique = true)
    @Property(name="id")
    protected String tradeId;

    @Relationship(type = "VALUATED")
    private Valuation valuation;

    @Relationship(type = "POSITIONS_ON", direction = Relationship.INCOMING)
    private TradingAccount account;

    @Relationship(type = "BELONGS_TO")
    private Portfolio portfolio;

    @Override
    public int compareTo(T o) {
        return this.getTradeId().compareTo(o.getTradeId());
    }
}
