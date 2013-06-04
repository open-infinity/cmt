<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:defineObjects />

<script type="text/javascript">
	// Initialize the tabs
	$(function() {
		$( "#connection-pool-manager-tabs" ).tabs();
	});

	// Event handlers for changes
	$(document).ready(function() {
	     $('#connpool_defaultAutoCommit').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_defaultReadOnly').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'Yes','false':'No'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_defaultTransactionIsolation').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','NONE':'NONE','READ_COMMITTED':'READ_COMMITTED','READ_UNCOMMITTED':'READ_UNCOMMITTED','REPEATABLE_READ':'REPEATABLE_READ','SERIALIZABLE':'SERIALIZABLE'}", 
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_defaultCatalog').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_driverClassName').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_username').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_password').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_maxActive').editable('<portlet:resourceURL id="savePropertyValue"/>', {
			indicator : 'Saving...',
			tooltip   : 'Click to edit...'
	     });
	     $('#connpool_maxIdle').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_minIdle').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_initialSize').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_maxWait').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_testOnBorrow').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_testOnRun').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_testWhileIdle').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_validationQuery').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_validatorClassName').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_timeBetweenEvictionRunsMillis').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_numTestsPerEvictionRun').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_minEvictableIdleTimeMillis').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_accessToUnderlyingConnectionAllowed').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_removeAbandoned').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_removeAbandonedTimeout').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_logAbandoned').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_connectionProperties').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_poolPreparedStatements').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_maxOpenPreparedStatements').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_validationInterval').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_jmxEnabled').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_fairQueue').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_abandonWhenPercentageFull').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_maxAge').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_useEquals').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_suspectTimeout').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	     $('#connpool_alternateUsernameAllowed').editable('<portlet:resourceURL id="savePropertyValue"/>', {
	         data   : "{'':'Default','true':'True','false':'False'}",
	         type   : 'select',
	         submit : 'OK',
	         indicator : 'Saving...',
	         tooltip   : 'Click to edit...'
	     });
	 });
</script>

<div class="demo">

<div id="connection-pool-manager-tabs">
	<ul>
		<li><a href="#connection-pool-manager-tab-1">Common</a></li>
		<li><a href="#connection-pool-manager-tab-2">Enhanced</a></li>
	</ul>
	<div id="connection-pool-manager-tab-1">
		<div class="property-table">
			<div class="property-row">
				<div class="property-key">Default Auto Commit</div>
				<div class="property-value"><div id="connpool_defaultAutoCommit">${settings.defaultAutoCommit}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Default Read-Only</div>
				<div class="property-value"><div id="connpool_defaultReadOnly">${settings.defaultReadOnly}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Default Transaction Isolation</div>
				<div class="property-value"><div id="connpool_defaultTransactionIsolation">${settings.defaultTransactionIsolation}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Default Catalog</div>
				<div class="property-value"><div id="connpool_defaultCatalog">${settings.defaultCatalog}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Driver Class Name</div>
				<div class="property-value"><div id="connpool_driverClassName">${settings.driverClassName}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Username</div>
				<div class="property-value"><div id="connpool_username">${settings.username}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Password</div>
				<div class="property-value"><div id="connpool_password">${settings.password}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Max Active</div>
				<div class="property-value"><div id="connpool_maxActive">${settings.maxActive}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Max Idle</div>
				<div class="property-value"><div id="connpool_maxIdle">${settings.maxIdle}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Min Idle</div>
				<div class="property-value"><div id="connpool_minIdle">${settings.minIdle}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Initial Size</div>
				<div class="property-value"><div id="connpool_initialSize">${settings.initialSize}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Max Wait</div>
				<div class="property-value"><div id="connpool_maxWait">${settings.maxWait}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Test On Borrow</div>
				<div class="property-value"><div id="connpool_testOnBorrow">${settings.testOnBorrow}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Test On Run</div>
				<div class="property-value"><div id="connpool_testOnRun">${settings.testOnRun}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Test While Idle</div>
				<div class="property-value"><div id="connpool_testWhileIdle">${settings.testWhileIdle}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Validation Query</div>
				<div class="property-value"><div id="connpool_validationQuery">${settings.validationQuery}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Validator Class Name</div>
				<div class="property-value"><div id="connpool_validatorClassName">${settings.validatorClassName}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Time Between Eviction Runs (ms)</div>
				<div class="property-value"><div id="connpool_timeBetweenEvictionRunsMillis">${settings.timeBetweenEvictionRunsMillis}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Number of Tests Per Eviction Run</div>
				<div class="property-value"><div id="connpool_numTestsPerEvictionRun">${settings.numTestsPerEvictionRun}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Minimum Evictable Idle Time (ms)</div>
				<div class="property-value"><div id="connpool_minEvictableIdleTimeMillis">${settings.minEvictableIdleTimeMillis}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Access To Underlying Connection Allowed</div>
				<div class="property-value"><div id="connpool_accessToUnderlyingConnectionAllowed">${settings.accessToUnderlyingConnectionAllowed}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Remove Abandoned</div>
				<div class="property-value"><div id="connpool_removeAbandoned">${settings.removeAbandoned}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Remove Abandoned Timeout</div>
				<div class="property-value"><div id="connpool_removeAbandonedTimeout">${settings.removeAbandonedTimeout}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Log Abandoned</div>
				<div class="property-value"><div id="connpool_logAbandoned">${settings.logAbandoned}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Connection Properties</div>
				<div class="property-value"><div id="connpool_connectionProperties">${settings.connectionProperties}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Pool Prepared Statements</div>
				<div class="property-value"><div id="connpool_poolPreparedStatements">${settings.poolPreparedStatements}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Max Open Prepared Statements</div>
				<div class="property-value"><div id="connpool_maxOpenPreparedStatements">${settings.maxOpenPreparedStatements}</div></div>
			</div>
		</div>
	</div>

	<div id="connection-pool-manager-tab-2">
		<div class="property-table">
			<div class="property-row">
				<div class="property-key">Validation Interval</div>
				<div class="property-value"><div id="connpool_validationInterval">${settings.validationInterval}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">JMX Enabled</div>
				<div class="property-value"><div id="connpool_jmxEnabled">${settings.jmxEnabled}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Fair Queue</div>
				<div class="property-value"><div id="connpool_fairQueue">${settings.fairQueue}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Abandon When Percentage Full</div>
				<div class="property-value"><div id="connpool_abandonWhenPercentageFull">${settings.abandonWhenPercentageFull}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Max Age</div>
				<div class="property-value"><div id="connpool_maxAge">${settings.maxAge}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Use Equals</div>
				<div class="property-value"><div id="connpool_useEquals">${settings.useEquals}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Suspect Timeout</div>
				<div class="property-value"><div id="connpool_suspectTimeout">${settings.suspectTimeout}</div></div>
			</div>
			<div class="property-row">
				<div class="property-key">Alternate Username Allowed</div>
				<div class="property-value"><div id="connpool_alternateUsernameAllowed">${settings.alternateUsernameAllowed}</div></div>
			</div>
		</div>
	</div>
</div>
