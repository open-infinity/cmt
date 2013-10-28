<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
	<h1>
		Create User
	</h1>
	<div class="span-12 last">	
		<%--form:form modelAttribute="account" action="account" method="post"--%>
		<form:form modelAttribute="userModel" action="/ssp-mvc/user" method="post">
            <fieldset>      
                <legend>User Account Fields</legend>
                
                <p> 
                    <form:label for="username" path="username" cssErrorClass="error">Username</form:label><br/>
                    <form:input path="username" /> <form:errors path="username" />
                </p>            
                <p> 
                    <form:label for="password" path="password" cssErrorClass="error">Password </form:label><br/>
                    <form:input path="password" /> <form:errors path="password" />
                </p>                
                <p> 
                    <form:label for="firstName" path="firstName" cssErrorClass="error">First name </form:label><br/>
                    <form:input path="firstName" /> <form:errors path="firstName" />
                </p>             
                <p> 
                    <form:label for="lastName" path="lastName" cssErrorClass="error">Last name </form:label><br/>
                    <form:input path="lastName" /> <form:errors path="lastName" />
                </p>
                <p>
                    <form:label for="phone" path="phone" cssErrorClass="error">Phone</form:label><br/>
                    <form:input path="phone" /> <form:errors path="phone" />
                </p>  
                <p> 
                    <form:label for="email" path="email" cssErrorClass="error">Email </form:label><br/>
                    <form:input path="email" /> <form:errors path="email" />
                </p>               
                <p> 
                    <form:label for="addressLine1" path="addressLine1" cssErrorClass="error">Address line 1</form:label><br/>
                    <form:input path="addressLine1" /> <form:errors path="addressLine1" />
                </p>                
                <p> 
                    <form:label for="addressLine2" path="addressLine2" cssErrorClass="error">Address line 2</form:label><br/>
                    <form:input path="addressLine2" /> <form:errors path="addressLine2" />
                </p>                
                <p> 
                    <form:label for="city" path="city" cssErrorClass="error">City</form:label><br/>
                    <form:input path="city" /> <form:errors path="city" />
                </p>                
                <p> 
                    <form:label for="stateProvinceRegion" path="stateProvinceRegion" cssErrorClass="error">State/Province/Region</form:label><br/>
                    <form:input path="stateProvinceRegion" /> <form:errors path="stateProvinceRegion" />
                </p>            
                <p> 
                    <form:label for="postalCode" path="postalCode" cssErrorClass="error">Postal code</form:label><br/>
                    <form:input path="postalCode" /> <form:errors path="postalCode" />
                </p>                     
                 
                <p> 
                    <input type="submit" value="Submit" />
                </p>
            </fieldset>
        </form:form>
	</div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
