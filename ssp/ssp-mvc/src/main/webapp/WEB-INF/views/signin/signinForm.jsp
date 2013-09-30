<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>
	<h1>
		Sign in
	</h1>
	<div class="span-12 last">	
		<form name='signin' action="<c:url value='j_spring_security_check' />" method='POST'>
			<fieldset>
			    <br> 
				<p> username <input type='text' name='j_username' value=''> </p>
				<p> password <input type='password' name='j_password' value=''> </p>
				<p> <input type="checkbox" value="keep-signed" /> Keep me signed in </p>
				<input type="submit" value="Sign in" />
			</fieldset>
			<h6>Don't have an account? <a href="account"> Sign up here</a> </h6>
	        <p></p>	
		</form>
	</div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>

