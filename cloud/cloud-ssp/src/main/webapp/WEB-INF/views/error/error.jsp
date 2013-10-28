<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
	<br />
	<h2>Error Page</h2>

	<br />
	<div class="sub_menu">
		<b>Following errors occurred during the execution</b>
	</div>
	<br />

	<div id="statusbox">
		<br/>
		<c:forEach items="${errorLevelExceptions}" var="exception">
			<label for="${exception}"><spring:message code="${exception}" /></label>
		</c:forEach>
		<br />
		<br />
		<c:forEach items="${warningLevelExceptions}" var="exception">
			<label for="${exception}"><spring:message code="${exception}" /></label>
		</c:forEach>
		<br />
		<br />
		<c:forEach items="${informativeLevelExceptions}" var="exception">
			<label for="${exception}"><spring:message code="${exception}" /></label>
		</c:forEach>
		<br />
			${nonstandard.message}
		<br />
	</div>
	<br />
</div>	

<%@ include file="/WEB-INF/views/common/footer.jsp"%>