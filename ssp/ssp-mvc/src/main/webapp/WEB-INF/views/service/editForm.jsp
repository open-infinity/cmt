<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
    <h1>
        Edit service
    </h1>
    <div class="span-12 last">  
        <form:form modelAttribute="accountModel" action="edit" method="post">
            <fieldset>      
                <p>
                    <form:label for="account.serviceType" path="account.serviceType" cssErrorClass="error">Service type</form:label><br/>
                    <form:input path="account.serviceType" /> <form:errors path="account.serviceType" />
                </p>
                <p> 
                    <form:label for="account.serviceValidFrom" path="account.serviceValidFrom" cssErrorClass="error">Valid from</form:label><br/>
                    <form:input path="account.serviceValidFrom" /> <form:errors path="account.serviceValidFrom" />
                </p>
                    <p> 
                    <form:label for="account.serviceValidTo" path="account.serviceValidTo" cssErrorClass="error">Valid to</form:label><br/>
                    <form:input path="account.serviceValidTo" /> <form:errors path="account.serviceValidTo" />
                </p>
                <p> 
                    <input type="submit"/>
                </p>
            </fieldset>
        </form:form>
    </div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
