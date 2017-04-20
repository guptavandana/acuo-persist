package com.acuo.persist.services;

import com.acuo.persist.entity.Asset;
import com.acuo.persist.ids.ClientId;
import com.google.common.collect.ImmutableMap;

public class AssetServiceImpl extends GenericService<Asset> implements AssetService {

    private static String ELIGIBLE_ASSET_WITH_ACCT_AND_TRANSFER_INFO =
            "MATCH (client:Client {id:{clientId}})-[:MANAGES]->(entity:LegalEntity)-[:CLIENT_SIGNS]->(agreement:Agreement)<-[:IS_AVAILABLE_FOR]-(asset:Asset) " +
            "WITH asset, client " +
            "MATCH path=(ca:CustodianAccount)-[holds:HOLDS]->(asset:Asset)-[:VALUATED]->(valuation:Valuation)-[:VALUE]->(value:AssetValue) " +
            "WITH asset, client, path " +
            "OPTIONAL MATCH transfer=(asset)<-[:OF]-(:AssetTransfer)-[:FROM|TO]->(:CustodianAccount)<-[:HAS]-(client) " +
            "RETURN asset, nodes(path), relationships(path), nodes(transfer), relationships(transfer)";

    private static String ELIGIBLE_ASSET_BY_CLIENT_AND_CALLID =
            "MATCH (client:Client {id:{clientId}})-[:MANAGES]->(entity:LegalEntity)-[:CLIENT_SIGNS]->(agreement:Agreement)<-[is:IS_AVAILABLE_FOR]-(asset:Asset) " +
            "WITH asset, client, agreement, entity, is " +
            "MATCH (agreement)<-[:STEMS_FROM]-(ms:MarginStatement)<-[*1..2]-(marginCall:MarginCall {id:'mcp26'}),(ms)-[:DIRECTED_TO]->(entity) " +
            "WHERE marginCall.marginType IN is.marginType " +
            "AND NOT (asset)-[:EXCLUDED]->(marginCall) " +
            "WITH DISTINCT asset " +
            "MATCH path=(ca:CustodianAccount)-[holds]->(asset)-[:VALUATED]->(valuation:Valuation)-[:VALUE]->(value:AssetValue) " +
            "RETURN asset, nodes(path), relationships(path)";

    @Override
    public Iterable<Asset> findEligibleAssetByClientId(ClientId clientId) {
        String query = ELIGIBLE_ASSET_WITH_ACCT_AND_TRANSFER_INFO;
        return sessionProvider.get().query(getEntityType(), query, ImmutableMap.of("clientId",clientId.toString()));
    }

    @Override
    public Iterable<Asset> findAvailableAssetByClientIdAndCallId(ClientId clientId, String callId) {
        String query = ELIGIBLE_ASSET_BY_CLIENT_AND_CALLID;
        return sessionProvider.get().query(getEntityType(), query, ImmutableMap.of("clientId",clientId.toString(), "callId", callId));
    }

    @Override
    public Class<Asset> getEntityType() {
        return Asset.class;
    }
}
