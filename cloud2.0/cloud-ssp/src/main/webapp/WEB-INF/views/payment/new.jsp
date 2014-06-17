<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
    <h1>
        View payment information
    </h1>
    <%--
    private String payPalEmail;
    
    private String payPalPassword;
    
    private String creditCardCountry;
    
    private String creditCardType;
    
    private String creditCardNumber;
    
    private String creditCardCSC;
    
    private String creditCardExpirationMonth;
    
    private String creditCardExpirationYear;
    
             --%>
             
    <div class="span-12 last">  
        <form:form modelAttribute="paymentModel" action="/ssp-mvc/payment" method="post">
            <fieldset>      
                
                <%--
                <p>
                    <form:label for="paymentType" path="paymentType" cssErrorClass="error">Payment type</form:label><br/>
                    <form:input path="paymentType" /> <form:errors path="serviceType" />
                </p>
                --%>
                
                <p> 
                    <form:label for="payPalEmail" path="payPalEmail" cssErrorClass="error">PayPal email</form:label><br/>
                    <form:input path="payPalEmail" /> <form:errors path="payPalEmail" />
                </p>
                <p> 
                    <form:label for="payPalPassword" path="payPalPassword" cssErrorClass="error">PayPal password</form:label><br/>
                    <form:input path="payPalPassword" /> <form:errors path="payPalPassword" />
                </p>
                <p> 
                    <form:label for="creditCardCountry" path="creditCardCountry" cssErrorClass="error">Credit card type</form:label><br/>
                    <form:input path="creditCardCountry" /> <form:errors path="creditCardCountry" />
                </p>
                <p> 
                    <form:label for="creditCardType" path="creditCardType" cssErrorClass="error">Credit card type</form:label><br/>
                    <form:input path="creditCardType" /> <form:errors path="creditCardType" />
                </p>
                <p> 
                    <form:label for="creditCardNumber" path="creditCardNumber" cssErrorClass="error">Credit card number</form:label><br/>
                    <form:input path="creditCardNumber" /> <form:errors path="creditCardNumber" />
                </p>
                <p> 
                    <form:label for="creditCardCSC" path="creditCardCSC" cssErrorClass="error">Credit card CSC</form:label><br/>
                    <form:input path="creditCardCSC" /> <form:errors path="creditCardCSC" />
                </p>
                <p> 
                    <form:label for="creditCardExpirationMonth" path="creditCardExpirationMonth" cssErrorClass="error">Credit card expiration month</form:label><br/>
                    <form:input path="creditCardExpirationMonth" /> <form:errors path="creditCardExpirationMonth" />
                </p>
                <p> 
                    <form:label for="creditCardExpirationYear" path="creditCardExpirationYear" cssErrorClass="error">Credit card expiration year</form:label><br/>
                    <form:input path="creditCardExpirationYear" /> <form:errors path="creditCardNumber" />
                </p>
            </fieldset>
        </form:form>
        
        <form method="get" action="/ssp-mvc/service/edit">
            <input type="submit" value="Edit service">
        </form>
        <br>
    </div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
