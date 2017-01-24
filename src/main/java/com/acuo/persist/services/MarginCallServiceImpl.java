package com.acuo.persist.services;

import com.acuo.persist.entity.CallStatus;
import com.acuo.persist.entity.MarginCall;
import com.acuo.persist.entity.Next;
import com.acuo.persist.entity.Step;
import com.acuo.persist.spring.Call;
import com.google.common.collect.ImmutableMap;
import com.google.inject.persist.Transactional;

@Transactional
public class MarginCallServiceImpl extends GenericService<MarginCall> implements MarginCallService {

    @Override
    public Class<MarginCall> getEntityType() {
        return MarginCall.class;
    }

    @Override
    public void setStatus(String marginCallId, CallStatus status) {
        MarginCall marginCall = this.findById(marginCallId, 1);
        Step previousStep = marginCall.getLastStep();
        Step lastStep = new Step();
        Next next = new Next();
        next.setStart(previousStep);
        next.setEnd(lastStep);
        previousStep.setNext(next);
        lastStep.setStatus(status);
        marginCall.setLastStep(lastStep);
        this.createOrUpdate(marginCall);
    }

    @Override
    public Iterable<MarginCall> allCallsFor(String clientId, CallStatus... statuses) {
        String query =
                "MATCH p=(:Client {id:{clientId}})-[:MANAGES]->(l:LegalEntity)-[r:CLIENT_SIGNS]->(a:Agreement)<-[:STEMS_FROM]-" +
                        "(m:MarginStatement)<-[*1..2]-(mc:MarginCall)-[:LAST]->(step:Step) " +
                        "WHERE step.status IN {statuses} " +
                        "RETURN mc, nodes(p), rels(p)";
        return session.query(MarginCall.class, query, ImmutableMap.of("clientId", clientId, "statuses", statuses));
    }

    @Override
    public Iterable<MarginCall> callFor(String marginStatementId, CallStatus... statuses) {
        String query =
                "MATCH p=(:Firm)-[:MANAGES]->(l:LegalEntity)-[]->(a:Agreement)<-[]-(m:MarginStatement {id:{msId}})<-[*1..2]-(mc:MarginCall)-[:LAST]->(step:Step) " +
                "WHERE step.status IN {statuses} " +
                "RETURN mc, nodes(p), rels(p)";
        return session.query(MarginCall.class, query, ImmutableMap.of("msId", marginStatementId, "statuses", statuses));
    }

    @Override
    @Deprecated
    public Iterable<MarginCall> callsForExpected(String marginStatementId) {
        String query = "MATCH (:Firm)-[:MANAGES]->(l:LegalEntity)-[]->(a:Agreement)<-[]-(m:MarginStatement {id:{msId}})<-[]-(mc1:MarginCall)-[:LAST]->(step:Step {status:'Unrecon'}) " +
                "MATCH (mc2:MarginCall)<-[MATCHED_TO_EXPECTED]-(mc1) " +
                "return mc2";
        return session.query(MarginCall.class, query, ImmutableMap.of("msId", marginStatementId));
    }

    @Override
    public Iterable<Call> notToUseYetallCallsFor(String clientId, String dateTime) {
        String query =
                "MATCH (:Client {id:{clientId}})-[:MANAGES]->(l:LegalEntity)-[r:CLIENT_SIGNS]->(a:Agreement)<-[:STEMS_FROM]-(m:MarginStatement {date:{dateTime}}) " +
                        "WITH a, m " +
                        "MATCH (m)<-[]-(mc:MarginCall)-[:LAST]->(step:Step) " +
                        "WITH {category:a.type, type:mc.callType, status:step.status, balance: mc.balanceAmount, excess: mc.excessAmount} AS Call " +
                        "RETURN Call";
        return session.query(Call.class, query, ImmutableMap.of("clientId", clientId, "dateTime", dateTime));
    }
}