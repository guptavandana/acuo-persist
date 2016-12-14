package com.acuo.persist.entity;

import com.acuo.persist.neo4j.converters.LocalDateConverter;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@NodeEntity
@Data
public class Valuation extends Entity{

    @Convert(LocalDateConverter.class)
    private LocalDate date;

    @Relationship(type = "VALUE")
    private Set<Value> values;
}