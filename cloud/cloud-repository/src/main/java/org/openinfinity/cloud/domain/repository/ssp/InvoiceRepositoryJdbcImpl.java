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
import org.openinfinity.cloud.domain.ssp.Invoice;
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
 * Invoice repository implementation.
 *
 * @author Vedran Bartonicek
 */
@Repository
public class InvoiceRepositoryJdbcImpl implements InvoiceRepository{

    private static final Logger LOG = Logger.getLogger(InvoiceRepositoryJdbcImpl.class.getName());

    private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
    public InvoiceRepositoryJdbcImpl(@Qualifier("sspDataSource") DataSource dataSource) {
        Assert.notNull(dataSource, "Please define data source for instance repository.");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }
    /* AbstractCrudRepositoryInterface */
    @AuditTrail
    public Invoice create(final Invoice invoice){
        SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("user_tbl").usingGeneratedKeyColumns("id");
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put("account_id", invoice.getAccountId());
        parameters.put("period_from", invoice.getPeriodFrom());
        parameters.put("period_to", invoice.getPeriodTo());
        parameters.put("status", invoice.getStatus());
        LOG.info(parameters.toString());
        Number id = insert.executeAndReturnKey(parameters);
        invoice.setId(BigInteger.valueOf((Long)id));
        return invoice;
    }

    @AuditTrail
    public void update(final Invoice invoice) {
        jdbcTemplate.update("update invoice set account_id = ?, period_from = ?, period_to = ?, status = ?",
              new PreparedStatementSetter() {
                  public void setValues(PreparedStatement ps) throws SQLException {
                      ps.setInt(1, invoice.getAccountId().intValue());
                      ps.setTimestamp(2, invoice.getPeriodFrom());
                      ps.setTimestamp(3, invoice.getPeriodTo());
                      ps.setInt(4, invoice.getStatus());
                  }
              }
        );
    }

    @AuditTrail
    public Collection<Invoice> loadAll(){
        return this.jdbcTemplate.query("select * from user_tbl", new InvoiceRowMapper());
    }

    @AuditTrail
    public Invoice load(BigInteger id){
        return this.jdbcTemplate.queryForObject("select * from invoice_tbl where user_id = ?", new Object[] { id }, new InvoiceRowMapper());
    }

    @AuditTrail
    public void delete (Invoice invoice){}

    @AuditTrail
    public Invoice loadLast(BigInteger accountId){
        return this.jdbcTemplate.queryForObject("select * from invoice_tbl where user_id = ?", new Object[] { accountId }, new InvoiceRowMapper());
    }

    private static final class InvoiceRowMapper implements RowMapper<Invoice> {
        public Invoice mapRow(ResultSet rs, int rowNumber) throws SQLException {
            Invoice invoice = new Invoice();
            invoice.setId(BigInteger.valueOf(rs.getInt("id")));
            invoice.setAccountId(BigInteger.valueOf(rs.getInt("account_id")));
            invoice.setPeriodFrom(rs.getTimestamp("period_from"));
            invoice.setPeriodTo(rs.getTimestamp("period_to"));
            invoice.setStatus(rs.getInt("status"));
            return invoice;
        }
    }

}
