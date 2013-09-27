<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html>
<head>
	<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<title>Create Account</title>
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/screen.css" />" type="text/css" media="screen, projection">
	<link rel="stylesheet" href="<c:url value="/resources/blueprint/print.css" />" type="text/css" media="print">
	<!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection">
	<![endif]-->
</head>	
<body>
<div class="container">
        <p></p>
        <p><h7>TOAS Self service portal </h7></p>
        <hr>
        <br></br>
	<h1>
		Sign in
	</h1>
	<div class="span-12 last">	
		<%--form:form modelAttribute="account" action="account" method="post"--%>
		<form:form modelAttribute="signinForm" action="signin" method="post">
		  	<fieldset>		
				<p>	
					<form:label for="username" path="username" cssErrorClass="error">Username</form:label><br/>
					<form:input path="username" /> <form:errors path="username" />
				</p>			
				<p> 
                    <form:label for="password" path="password" cssErrorClass="error">Password </form:label><br/>
                    <form:input path="password" /> <form:errors path="password" />
                </p>
                
                <p>
                    <input type="checkbox" value="keep-signed" /> Keep me signed in
                </p>
                <br>
                <p>
					<input type="submit" value="Sign in" />
				</p>
			</fieldset>
		</form:form>
		<h7>Don't have an account? <a href="account"> Sign up here</a> </h7>
		<p></p>
	</div>
	
	<hr>
		
	<ul>
		<li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
	</ul>	
</div>
</body>
</html>