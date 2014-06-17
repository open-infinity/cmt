package org.openinfinity.cloud.service.administrator;

import java.util.Collection;

import org.openinfinity.cloud.domain.MulticastAddress;
import org.openinfinity.cloud.domain.repository.administrator.MulticastAddressRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("multicastAddressService")
public class MulticastAddressServiceImpl implements MulticastAddressService {
	@Autowired
	@Qualifier("multicastAddressRepository")
	private MulticastAddressRepository multicastAddressRepository;
	
	@Log
	public void addAddress(MulticastAddress address) {
		multicastAddressRepository.addAddress(address);
	}

	@Log
	public MulticastAddress getAddress(int id) {
		return multicastAddressRepository.getAddress(id);
	}

	@Log
	public Collection<MulticastAddress> getAddresses() {
		return multicastAddressRepository.getAddresses();
	}

	@Override
	public void deleteMulticastAddress(int id) {
		multicastAddressRepository.deleteMulticastAddress(id);
	}

	@Override
	public void deleteMulticastAddress(String address) {
		multicastAddressRepository.deleteMulticastAddress(address);
	}
	
	@Override
	public void deleteMulticastAddressForCluster(int clusterId) {
		multicastAddressRepository.deleteMulticastAddressForCluster(clusterId);
	}

}
