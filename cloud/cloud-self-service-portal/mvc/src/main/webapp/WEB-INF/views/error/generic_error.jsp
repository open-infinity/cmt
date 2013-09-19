<div id="statusbox">
	<br/>
	<c:forEach items="${errorStatuses.errorLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}" /></label>
	</c:forEach>
	<br />
	<br />
	<c:forEach items="${errorStatuses.warningLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}" /></label>
	</c:forEach>
	<br />
	<br />
	<c:forEach items="${errorStatuses.informativeLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}" /></label>
	</c:forEach>
</div>
<br />
