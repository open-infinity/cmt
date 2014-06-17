<%--

/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 */

@author Ilkka Leinonen

@version 1.0.0
@since 1.0.0

 --%>

<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<portlet:defineObjects />
<portlet:actionURL var="action" />

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>Cloud Deployer</title>
<style>
label {
	background: white;
	width: 300px;
}
</style>
</head>
<body>
	<c:forEach items="${errorLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}"/></label>
	</c:forEach>
	<br /><br />
	<c:forEach items="${warningLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}"/></label>
	</c:forEach>
	<br /><br />
	<c:forEach items="${informativeLevelExceptions}" var="exception">
		<label for="${exception}"><spring:message code="${exception}"/></label>
	</c:forEach>
	<br /><br />
</body>
</html>