package org.openinfinity.cloud.domain.repository.administrator;

import java.util.Collection;

import org.openinfinity.cloud.domain.Password;

public class PasswordRepositoryJdbcImpl implements PasswordRepository {

	@Override
	public void addPassword(Password password) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePassword(Password password) {
		// TODO Auto-generated method stub

	}

	@Override
	public Password getPassword(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Password> getInstancePasswords(int instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Password> getClusterPasswords(int cluster) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Password> getClusterPasswordsByType(int cluster,
			int passwordType) {
		// TODO Auto-generated method stub
		return null;
	}

}
