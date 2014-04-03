package org.openinfinity.cloud.service.backup;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openinfinity.cloud.domain.BackupOperation;
import org.openinfinity.cloud.domain.BackupRule;
import org.openinfinity.cloud.domain.repository.backup.BackupRuleRepository;
import org.openinfinity.cloud.domain.repository.backup.BackupWorkRepository;

@Service("backupService")
public class BackupServiceImpl implements BackupService {

	@Autowired
	private BackupWorkRepository backupWorkRepository;

	@Autowired
	private BackupRuleRepository backupRuleRepository;

	public List<BackupOperation> readBackupOperationsAfter(int id) {
		return backupWorkRepository.readBackupOperationsAfter(id);
	}

	public void writeBackupOperation(BackupOperation op) {
		backupWorkRepository.writeBackupOperation(op);
	}
	
	public BackupOperation readBackupOperation(int id) {
		return backupWorkRepository.readBackupOperation(id);
	}

	public boolean deleteBackupOperation(BackupOperation op) {
		return backupWorkRepository.deleteBackupOperation(op);
	}

	// ------------------------------------------------------------------------
	
	public Set<Integer> getBackupClusters() {
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
