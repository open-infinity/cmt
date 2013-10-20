                      /*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openinfinity.cloud.domain.repository.ssp;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.ssp.InvoiceItem;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
* InvoiceItem repository implementation.
*
* @author Vedran Bartonicek
*/
@Repository
public class InvoiceItemRepositoryJdbcImpl implements InvoiceItemRepository{

private static final Logger LOG = Logger.getLogger(InvoiceItemRepositoryJdbcImpl.class.getName());

private JdbcTemplate jdbcTemplate;

private DataSource dataSource;

@Autowired
public InvoiceItemRepositoryJdbcImpl(@Qualifier("sspDataSource") DataSource dataSource) {
    Assert.notNull(dataSource, "Please define data source for instance repository.");
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.dataSource = dataSource;
}
/* AbstractCrudRepositoryInterface */
@AuditTrail
    public InvoiceItem create(final InvoiceItem invoiceItem){
    /*
    SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("user_tbl").usingGeneratedKeyColumns("id");
    Map<String,Object> parameters = new HashMap<String,Object>();
    parameters.put("account_id", invoiceItem.getAccountId());
    parameters.put("period_from", invoiceItem.getPeriodFrom());
    parameters.put("period_to", invoiceItem.getPeriodTo());
    parameters.put("status", invoiceItem.getStatus());
    LOG.info(parameters.toString());
    Number id = insert.executeAndReturnKey(parameters);
    invoiceItem.setId(BigInteger.valueOf((Long)id));
    */
    return invoiceItem;
    }

@AuditTrail
public void update(final InvoiceItem invoiceItem) {
jdbcTemplate.update("update invoiceItem set account_id = ?, period_from = ?, period_to = ?, status = ?",
    new PreparedStatementSetter() {
        public void setValues(PreparedStatement ps) throws SQLException {
			/*
            ps.setInt(1, invoiceItem.getAccountId().intValue());
            ps.setTimestamp(2, invoiceItem.getPeriodFrom());
            ps.setTimestamp(3, invoiceItem.getPeriodTo());
            ps.setInt(4, invoiceItem.getStatus());
            */
        }
    }
);
}

@AuditTrail
public Collection<InvoiceItem> loadAll(){
return this.jdbcTemplate.query("select * from user_tbl", new InvoiceItemRowMapper());
}

@AuditTrail
public InvoiceItem load(BigInteger id){
return this.jdbcTemplate.queryForObject("select * from invoice_tbl where user_id = ?", new Object[] { id }, new InvoiceItemRowMapper());
}

@AuditTrail
public void delete (InvoiceItem invoiceItem){}

@AuditTrail
public InvoiceItem loadLast(BigInteger accountId){
return this.jdbcTemplate.queryForObject("select * from invoice_tbl where user_id = ?", new Object[] { accountId }, new InvoiceItemRowMapper());
}

private static final class InvoiceItemRowMapper implements RowMapper<InvoiceItem> {
public InvoiceItem mapRow(ResultSet rs, int rowNumber) throws SQLException {
  InvoiceItem invoiceItem = new InvoiceItem();
  /*
  invoiceItem.setId(BigInteger.valueOf(rs.getInt("id")));
  invoiceItem.setAccountId(BigInteger.valueOf(rs.getInt("account_id")));
  invoiceItem.setPeriodFrom(rs.getTimestamp("period_from"));
  invoiceItem.setPeriodTo(rs.getTimestamp("period_to"));
  invoiceItem.setStatus(rs.getInt("status"));
  */
  return invoiceItem;
}
}

}
