package com.acuo.persist.spring;

//@Repository
public interface ClientRepository /*extends Neo4jRepository<Client, Long>*/ {

  /*@Query("MATCH (:Client {id:{0}})-[:MANAGES]->(l:LegalEntity)-[r:CLIENT_SIGNS]->(a:Agreement)<-[:STEMS_FROM]-(m:MarginStatement {date:{1}}) " +
          "WITH a, m " +
          "MATCH (m)<-[]-(mc:MarginCall)-[:LAST]->(step:Step) " +
          "WITH {category:a.type, type:mc.callType, status:step.status, balance: mc.balanceAmount, excess: mc.excessAmount} AS Call " +
          "RETURN Call")*/
  public Iterable<Call> allCallsFor(String clientId, String dateTime);
}