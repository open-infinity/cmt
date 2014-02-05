<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html"%>

<portlet:defineObjects />

<fmt:setBundle basename="cloudadmin"/>

<div id="cloudadmin">
	<div id="tabs">
		<ul>
			<li><a href="#tabs-0"><fmt:message key="cloud.mainview.title.instances" /></a></li>
		</ul>
		<div id="tabs-0">
				<table id="instances"></table>
				<div id="instancepager"></div>
				
				<div class="ui-button-bar">
					<button id="manage-instance"><fmt:message key="cloud.mainview.button.manageInstance" /></button>
					<button id="create-instance"><fmt:message key="cloud.mainview.button.newInstance" /></button>
					<button id="delete-instance"><fmt:message key="cloud.mainview.button.deleteInstance" /></button>
				</div>
		</div>
	</div>
</div>

<jsp:include page="properties.jsp" />
<jsp:include page="templates.jsp" />
<jsp:include page="instanceview.jsp" />
<jsp:include page="dialogs.jsp" />
