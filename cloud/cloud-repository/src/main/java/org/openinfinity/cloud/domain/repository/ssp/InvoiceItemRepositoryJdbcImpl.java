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

    @AuditTrail
        public InvoiceItem create(final InvoiceItem invoiceItem){
            SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("invoice_item").usingGeneratedKeyColumns("id");
            Map<String,Object> parameters = new HashMap<String,Object>();
            parameters.put("invoice_id", invoiceItem.getInvoiceId().intValue());
            parameters.put("machine_id", invoiceItem.getMachineId());
            parameters.put("cluster_id", invoiceItem.getClusterId());
            parameters.put("machine_uptime", invoiceItem.getMachineUptime());
            parameters.put("machine_type", invoiceItem.getMachineType());
            LOG.info(parameters.toString());
            Number id = insert.executeAndReturnKey(parameters);
            invoiceItem.setId(BigInteger.valueOf(id.longValue()));
            return invoiceItem;
        }

    @AuditTrail
    public void update(final InvoiceItem invoiceItem) {
        jdbcTemplate.update("update invoiceItem set invoice_id = ?, machine_id = ?, cluster_id =  ?, machine_uptime = ?, machine_type = ?",
            new PreparedStatementSetter() {
                public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, invoiceItem.getInvoiceId().intValue());
                ps.setInt(2, invoiceItem.getMachineId());
                ps.setInt(3, invoiceItem.getClusterId());
                ps.setLong(4, invoiceItem.getMachineUptime());
                ps.setInt(5, invoiceItem.getMachineType());
                }
            }
        );
    }

    @AuditTrail
    public Collection<InvoiceItem> loadAll(){
        return this.jdbcTemplate.query("select * from invoice_item", new InvoiceItemRowMapper());
    }

    @AuditTrail
    public InvoiceItem load(BigInteger id){
        return this.jdbcTemplate.queryForObject("select * from invoice_item where id = ?", new Object[] { id.intValue() }, new InvoiceItemRowMapper());
    }

    @AuditTrail
    public void delete (InvoiceItem invoiceItem){
        this.jdbcTemplate.update("delete from invoice_item where id = ?", invoiceItem.getId().intValue());
    }

    @Override
    public void delete(BigInteger id) {
        this.jdbcTemplate.update("delete from invoice_item where id = ?", id.intValue());
    }

    private static final class InvoiceItemRowMapper implements RowMapper<InvoiceItem> {
        public InvoiceItem mapRow(ResultSet rs, int rowNumber) throws SQLException {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoiceId(BigInteger.valueOf(rs.getInt("invoice_id")));
            invoiceItem.setMachineId(rs.getInt("machine_id"));
            invoiceItem.setClusterId(rs.getInt("cluster_id"));
            invoiceItem.setMachineUptime(rs.getLong("machine_uptime"));
            invoiceItem.setMachineType(rs.getInt("machine_type"));

            return invoiceItem;
        }
    }

}
