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
	<h1>
		Create Account
	</h1>
	<div class="span-12 last">	
		<%--form:form modelAttribute="account" action="account" method="post"--%>
		<form:form modelAttribute="accountModel" action="account" method="post">
		  	<fieldset>		
				<legend>Account Fields</legend>
				<p>	
					<form:label for="user.username" path="user.username" cssErrorClass="error">Username</form:label><br/>
					<form:input path="user.username" /> <form:errors path="user.username" />
				</p>			
				<p> 
                    <form:label for="user.password" path="user.password" cssErrorClass="error">Password </form:label><br/>
                    <form:input path="user.password" /> <form:errors path="user.password" />
                </p>                
                <p> 
                    <form:label for="user.firstName" path="user.firstName" cssErrorClass="error">First name </form:label><br/>
                    <form:input path="user.firstName" /> <form:errors path="user.firstName" />
                </p>             
                <p> 
                    <form:label for="user.lastName" path="user.lastName" cssErrorClass="error">Last name </form:label><br/>
                    <form:input path="user.lastName" /> <form:errors path="user.lastName" />
                </p>                
                <p> 
                    <form:label for="user.phone" path="user.phone" cssErrorClass="error">Phone</form:label><br/>
                    <form:input path="user.phone" /> <form:errors path="user.phone" />
                </p>  
                <p> 
                    <form:label for="user.email" path="user.email" cssErrorClass="error">Email </form:label><br/>
                    <form:input path="user.email" /> <form:errors path="user.email" />
                </p>               
                <p> 
                    <form:label for="user.addressLine1" path="user.addressLine1" cssErrorClass="error">Address line 1</form:label><br/>
                    <form:input path="user.addressLine1" /> <form:errors path="user.addressLine1" />
                </p>                
                <p> 
                    <form:label for="user.addressLine2" path="user.addressLine2" cssErrorClass="error">Address line 2</form:label><br/>
                    <form:input path="user.addressLine2" /> <form:errors path="user.addressLine2" />
                </p>                
                <p> 
                    <form:label for="user.city" path="user.city" cssErrorClass="error">City</form:label><br/>
                    <form:input path="user.city" /> <form:errors path="user.city" />
                </p>                
                <p> 
                    <form:label for="user.stateProvinceRegion" path="user.stateProvinceRegion" cssErrorClass="error">State/Province/Region</form:label><br/>
                    <form:input path="user.stateProvinceRegion" /> <form:errors path="user.stateProvinceRegion" />
                </p>            
                <p> 
                    <form:label for="user.postalCode" path="user.postalCode" cssErrorClass="error">Postal code</form:label><br/>
                    <form:input path="user.postalCode" /> <form:errors path="user.postalCode" />
                </p>                     
				<p>
					<form:label for="account.serviceType" path="account.serviceType" cssErrorClass="error">Service type</form:label><br/>
					<form:input path="account.serviceType" /> <form:errors path="account.serviceType" />
				</p>
				<p>	
					<form:label for="account.serviceValidFrom" path="account.serviceValidFrom" cssErrorClass="error">Service valid from</form:label><br/>
					<form:input path="account.serviceValidFrom" /> <form:errors path="account.serviceValidFrom" />
				</p>
				    <p> 
                    <form:label for="account.serviceValidTo" path="account.serviceValidTo" cssErrorClass="error">Service valid to</form:label><br/>
                    <form:input path="account.serviceValidTo" /> <form:errors path="account.serviceValidTo" />
                </p>
                
				<p>	
					<input type="submit" />
				</p>
			</fieldset>
		</form:form>
	</div>
	<hr>	
	<ul>
		<li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
	</ul>	
</div>
</body>
</html>