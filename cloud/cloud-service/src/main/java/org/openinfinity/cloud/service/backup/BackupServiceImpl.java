package org.openinfinity.cloud.service.backup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openinfinity.cloud.domain.BackupRule;

@Service("backupService")
public class BackupServiceImpl {

	public void backupCluster(int clusterId) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
	
	public void analyze(RestoreInfo info) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
	
	public void fullRestore(int oldClusterId, int newClusterId) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}

	public void partialRestore(RestoreInfo info) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
	
	public List<BackupRule> getClusterBackupRules(int cluster_id) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}

	public void deleteClusterBackupRules(int cluster_id) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}

	public void createBackupRule(BackupRule br) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
	
	public void deleteBackupRule(BackupRule br) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
	
	public void updateBackupRule(BackupRule br) {
		throw new RuntimeException("UNIMPLEMENTED"); // TODO
	}
}
