package org.openinfinity.cloud.service.backup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openinfinity.cloud.domain.BackupRule;
import org.openinfinity.cloud.domain.repository.backup.BackupRuleRepository;

@Service("backupService")
public class BackupServiceImpl implements BackupService {

	@Autowired
	private BackupRuleRepository backupRuleRepository;
	
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
	
	// --------------------------------------------------------------
	
	public List<Integer> getBackupClusters() {
		return backupRuleRepository.getBackupClusters();
	}

	public List<BackupRule> getClusterBackupRules(int cluster_id) {
		return backupRuleRepository.getClusterBackupRules(cluster_id);
	}

	public void deleteClusterBackupRules(int cluster_id) {
		backupRuleRepository.deleteClusterBackupRules(cluster_id);
	}

	public void createBackupRule(BackupRule br) {
		backupRuleRepository.createBackupRule(br);
	}
	
	public void deleteBackupRule(BackupRule br) {
		backupRuleRepository.createBackupRule(br);
	}
	
	public void updateBackupRule(BackupRule br) {
		backupRuleRepository.createBackupRule(br);
	}
}
