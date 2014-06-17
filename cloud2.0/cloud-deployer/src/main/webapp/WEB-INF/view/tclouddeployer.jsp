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
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:defineObjects />

<html>
<head>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>Upload Example</title>
        <!--[if lt IE 9]>
          <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
</head>
<body>
	<portlet:actionURL var="action" />
<!-- 	<script> -->
// 		actionUrl="${action}'";
<!-- 	</script> -->
	<form:form modelAttribute="deploymentModel" action="${action}" method="post"  enctype="multipart/form-data">
		<fieldset>
			<legend>Upload Fields</legend>
<!-- 			<p> -->
<%-- 				<form:label for="organization" path="organization">Organization</form:label> --%>
<!-- 				<br /> -->
<%-- 				<form:input path="organization" /> --%>
<!-- 			</p> -->
			<p>
				<form:label for="name" path="name">Name</form:label>
				<br />
				<form:input path="name" />
			</p>
			<p>
				<form:label for="clusterId" path="clusterId">Cluster ID</form:label>
				<br />
				<form:input path="clusterId" />
			</p>
			<p>
				<form:label for="fileData" path="fileData">File</form:label>
				<br />
				<form:input path="fileData" type="file" />
			</p>
			<p>
				<input type="submit" />
			</p>
		</fieldset>
	</form:form>
	
<!--    <div id="dropbox"> -->
<!--    		<span class="message">Drop images here to upload. <br /><i>(they will only be visible to you)</i></span> -->
<!--    </div> -->
     
</body>
</html>