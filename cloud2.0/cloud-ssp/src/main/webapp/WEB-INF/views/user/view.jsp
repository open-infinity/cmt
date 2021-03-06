<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
	<h1>
		View User
	</h1>
	<div class="span-12 last">	
		<%--form:form modelAttribute="account" action="account" method="post"--%>
		<form:form modelAttribute="userModel" action="/ssp-mvc/user/${exampleaccount.id}" method="post">
		  	<fieldset>		
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
					<input type="submit" value="Submit" />
				</p>
			</fieldset>
		</form:form>
	</div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
