class oi-identity-gateway::setup {
	
	exec {"configure-openam":
		command => "/opt/openinfinity/2.0.0/sso-tools/configure-openam.sh",
		user => 'toas',
		creates => "/opt/openinfinity/2.0.0/sso-tools/configure-run",
		timeout => 0,
		require => Class["oi-identity-gateway::service"]
	}
}
