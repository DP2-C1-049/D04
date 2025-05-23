<%--
- list.jsp
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
<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Leg List</title>
</head>
<body>
    <acme:list navigable="true" show="show">
        <acme:list-column path="flightNumber" code="manager.leg.list.label.flightNumber" sortable="false"/>
        <acme:list-column path="originCity" code="manager.leg.list.label.departureCity" sortable="false"/>
        <acme:list-column path="destinationCity" code="manager.leg.list.label.arrivalCity" sortable="false"/>
        <acme:list-column path="departure" code="manager.leg.list.label.departure" />
        <acme:list-column path="arrival" code="manager.leg.list.label.arrival" sortable="false"/>
        <acme:list-column path="duration" code="manager.leg.list.label.duration" sortable="false"/>
        <acme:list-column path="status" code="manager.leg.list.label.status" sortable="false"/>
        
    </acme:list>
    
    <c:if test="${flightDraftMode}">
		<acme:button code="manager.leg.list.button.create" action="/manager/leg/create?flightId=${param.flightId}" />
	</c:if>
</body>
</html>