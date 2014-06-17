class oi3-serviceplatform {
	require oi3-ebs
	require oi3-basic	
	include oi3-serviceplatform::install
	include oi3-serviceplatform::config
	include oi3-serviceplatform::service
}
