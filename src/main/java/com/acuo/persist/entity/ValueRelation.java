package com.acuo.persist.entity;

import com.acuo.persist.neo4j.converters.LocalDateConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
abstract class ValueRelation extends Entity<ValueRelation>{

    @Convert(LocalDateConverter.class)
    private LocalDate dateTime;
}
