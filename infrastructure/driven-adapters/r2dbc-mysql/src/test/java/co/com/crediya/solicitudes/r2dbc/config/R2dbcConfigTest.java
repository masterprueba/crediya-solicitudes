package co.com.crediya.solicitudes.r2dbc.config;


import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class R2dbcConfigTest {

    @Test
    void transactionManagerBeanShouldBeCreated() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        R2dbcConfig config = new R2dbcConfig();

        ReactiveTransactionManager manager = config.transactionManager(connectionFactory);

        assertThat(manager).isInstanceOf(R2dbcTransactionManager.class);
    }

    @Test
    void transactionalOperatorBeanShouldBeCreated() {
        ReactiveTransactionManager manager = mock(ReactiveTransactionManager.class);
        R2dbcConfig config = new R2dbcConfig();

        TransactionalOperator operator = config.transactionalOperator(manager);

        assertThat(operator).isNotNull();
    }
}