package org.openinfinity.cloud.domain.repository.administrator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.MulticastAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository("multicastAddressRepository")
public class MulticastAddressRepositoryImpl implements MulticastAddressRepository {
	private static final Logger LOG = Logger.getLogger(MulticastAddressRepositoryImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public MulticastAddressRepositoryImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for authorized routing repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
	}
	
	@Override
	public void addAddress(final MulticastAddress address) {
		jdbcTemplate.update("insert into reserved_multicast_ip_tbl (id, instance_id, cluster_id, address) values (?,?,?,?)",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, address.getId());
						ps.setInt(2, address.getInstanceId());
						ps.setInt(3, address.getClusterId());
						ps.setString(4, address.getAddress());
					}
				}
		);

	}

	@Override
	public MulticastAddress getAddress(int id) {
		List<MulticastAddress> list = jdbcTemplate.query("select * from reserved_multicast_ip_tbl", new MulticastAddressMapper());
		MulticastAddress ma = DataAccessUtils.singleResult(list);
		return ma;
	}

	@Override
	public Collection<MulticastAddress> getAddresses() {
		List<MulticastAddress> list = jdbcTemplate.query("select * from reserved_multicast_ip_tbl", new MulticastAddressMapper());
		return list;
	}
	
	private static final class MulticastAddressMapper implements RowMapper<MulticastAddress> {
		public MulticastAddress mapRow(ResultSet rs, int rowNumber) throws SQLException {
			MulticastAddress a = new MulticastAddress();
			a.setId(rs.getInt("id"));
			a.setInstanceId(rs.getInt("instance_id"));
			a.setClusterId(rs.getInt("cluster_id"));
			a.setAddress(rs.getString("address"));
			return a;
		}
	}

	@Override
	public void deleteMulticastAddress(int id) {
		jdbcTemplate.update("delete from reserved_multicast_ip_tbl where id = "+id);
	}

	@Override
	public void deleteMulticastAddress(String address) {
		jdbcTemplate.update("delete from reserved_multicast_ip_tbl where address = '"+address+"'");
	}
	
	@Override
	public void deleteMulticastAddressForCluster(int clusterId) {
		jdbcTemplate.update("delete from reserved_multicast_ip_tbl where cluster_id = '"+clusterId+"'");
	}

}
