<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>
	<h1>
		Sign in
	</h1>
	<div class="span-12 last">	
		<%--form:form modelAttribute="account" action="account" method="post"--%>
		<form:form modelAttribute="signinForm" action="/ssp-mvc/signin" method="post">
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
		<h6>Don't have an account? <a href="account"> Sign up here</a> </h6>
		<p></p>
	</div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
	
	