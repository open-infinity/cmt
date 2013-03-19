package org.openinfinity.cloud.domain.repository.administrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 15.3.2013
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@Repository("portalDbUserRepository")
public class PortalDbUserRepositoryImpl implements PortalDbUserRepository {

    private JdbcTemplate template;
    private CallableStatementCreator csc;
    private CallableStatementCallback<String> callback;

    @Autowired
    public PortalDbUserRepositoryImpl(@Qualifier("portalUserDataSource") DataSource ds) {
        template = new JdbcTemplate(ds);
        csc = new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                CallableStatement cstmt = connection.prepareCall("CALL OP.PZZZ_GET_USER1(?,?)");
                cstmt.registerOutParameter(1, Types.CHAR);
                cstmt.registerOutParameter(2, Types.VARCHAR);
                return cstmt;
            }
        };
        callback = new CallableStatementCallback<String>() {
            @Override
            public String doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                callableStatement.execute();
                return callableStatement.getString(1);
            }
        };
    }

    @Override
    public String getNextFreeUserid() {
        return template.execute(csc, callback);
    }
}
