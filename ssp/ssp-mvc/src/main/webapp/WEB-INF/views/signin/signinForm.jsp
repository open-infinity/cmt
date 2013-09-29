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
		<form name='signin' action="<c:url value='j_spring_security_check' />"
		method='POST'>
		<fieldset>
			<p> username <input type='text' name='j_username' value=''> </p>
			<p> password <input type='text' name='j_password' value=''> </p>
			<p> <input name="submit" type="submit" value="submit"/> </p>
			<p> <input name="reset" type="reset"/> </p>
		</fieldset>
		
	</form>
	</div>
	
	<hr>
		
	<ul>
		<li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
	</ul>	
</div>
</body>
</html>
