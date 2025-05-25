<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="acme" uri="http://acme-framework.org/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Leg Details</title>
</head>
<body>
    <acme:form readonly="false">
        <acme:input-textbox code="manager.leg.form.label.flightNumber" path="flightNumber" />
        <acme:input-moment code="manager.leg.form.label.departure" path="departure" />
        <acme:input-moment code="manager.leg.form.label.arrival" path="arrival" />
        <acme:input-textbox code="manager.leg.form.label.duration" path="duration" readonly="true"/>
        <acme:input-select code="manager.leg.form.label.status" path="status"  choices="${legStatuses}" />
        <acme:input-select 
            code="manager.leg.form.label.departureAirport" 
            path="departureAirport"
            choices="${departureAirports}" />
        <acme:input-select 
            code="manager.leg.form.label.arrivalAirport" 
            path="arrivalAirport"
            choices="${arrivalAirports}" />
            
	  <acme:input-select 
	        code="manager.leg.form.label.aircraft" 
	        path="aircraft"
	        choices="${aircraftChoices}" />
        <c:if test="${draftMode and _command != 'create'}">
            <acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish" />
        </c:if>
        
        <c:choose>
            <c:when test="${_command == 'create'}">
                <acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?flightId=${param.flightId}" />
            </c:when>
            <c:otherwise>
                <c:if test="${draftMode}">
                    <acme:submit code="manager.leg.form.button.update" action="/manager/leg/update" />
                    <acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete" />
                </c:if>
            </c:otherwise>
        </c:choose>
    </acme:form>
</body>
</html>