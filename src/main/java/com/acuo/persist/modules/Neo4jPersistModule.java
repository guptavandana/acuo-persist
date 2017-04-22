package com.acuo.persist.modules;

import com.acuo.persist.configuration.PropertiesHelper;
import com.acuo.persist.core.Neo4jPersistService;
import com.acuo.persist.core.Neo4jSessionFactory;
import com.acuo.persist.transaction.Neo4jLocalTxnInterceptor;
import com.acuo.persist.transaction.TransactionInterceptor;
import com.google.common.util.concurrent.Service;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.persist.PersistModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import org.aopalliance.intercept.MethodInterceptor;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Arrays;

public class Neo4jPersistModule extends PersistModule {

    private final MethodInterceptor transactionInterceptor = new TransactionInterceptor();//new Neo4jLocalTxnInterceptor();

    @Override
    protected void configurePersistence() {

        bind(Packages.class).toProvider(PackagesProvider.class);
        bind(SessionFactory.class).toProvider(Neo4jSessionFactory.class);
        bind(Neo4jPersistService.class).in(Singleton.class);
        bind(PersistService.class).to(Neo4jPersistService.class);
        bind(UnitOfWork.class).to(Neo4jPersistService.class);
        bind(Session.class).toProvider(Neo4jPersistService.class);

        requestInjection(transactionInterceptor);

        Multibinder<Service> services = Multibinder.newSetBinder(binder(), Service.class);
        services.addBinding().to(Neo4jPersistService.class);
    }

    @Override
    protected MethodInterceptor getTransactionInterceptor() {
        return this.transactionInterceptor;
    }

    public interface Packages {

        String[] value();
    }

    public static class PackagesProvider implements Provider<Packages> {

        private final String[] packages;

        @Inject
        PackagesProvider(@Named(PropertiesHelper.NEO4J_OGM_PACKAGES) String packages) {
            this.packages = Arrays.stream(packages.split(",")).map(x -> x.trim()).toArray(size -> new String[size]);
        }

        @Override
        public Packages get() {
            return new Packages() {
                @Override
                public String[] value() {
                    return packages;
                }
            };
        }
    }
}
