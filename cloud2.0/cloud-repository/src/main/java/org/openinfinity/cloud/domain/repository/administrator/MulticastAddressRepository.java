package org.openinfinity.cloud.domain.repository.administrator;

import java.util.Collection;

import org.openinfinity.cloud.domain.MulticastAddress;

public interface MulticastAddressRepository {
	void addAddress(MulticastAddress address);
	MulticastAddress getAddress(int id);
	Collection<MulticastAddress> getAddresses();
	void deleteMulticastAddress(int id);
	void deleteMulticastAddress(String address);
	void deleteMulticastAddressForCluster(int clusterId);
}
