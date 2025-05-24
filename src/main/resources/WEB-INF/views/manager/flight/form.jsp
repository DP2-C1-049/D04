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
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Flight Details</title>
</head>
<body>
    <acme:form readonly="false">
        <acme:input-textbox
            code="manager.flight.form.label.tag"
            path="tag" />

        <acme:input-checkbox
            code="manager.flight.form.label.indication"
            path="indication" />

        <acme:input-textbox
            code="manager.flight.form.label.cost"
            path="cost" />

        <acme:input-textbox
            code="manager.flight.form.label.description"
            path="description" />

        <acme:input-moment
            code="manager.flight.form.label.departure"
            path="departure" 
            readonly="true"/>

        <acme:input-moment
            code="manager.flight.form.label.arrival"
            path="arrival"
            readonly="true" />
        <acme:input-textbox
            code="manager.flight.form.label.originCity"
            path="originCity"
            readonly="true" />

        <acme:input-textbox
            code="manager.flight.form.label.destinationCity"
            path="destinationCity"
            readonly="true" />

        <acme:input-integer
            code="manager.flight.form.label.numberOfLayovers"
            path="numberOfLayovers"
            readonly="true" />
        <c:if test="${draftMode}">
            <acme:submit
                code="manager.flight.form.button.publish"
                action="/manager/flight/publish" />
        </c:if>

        <c:choose>
            <c:when test="${_command == 'create'}">
                <acme:submit
                    code="manager.flight.form.button.create"
                    action="/manager/flight/create" />
            </c:when>
            <c:when test="${(_command == 'show' || _command == 'update') && draftMode}">
                <acme:submit
                    code="manager.flight.form.button.update"
                    action="/manager/flight/update" />
                <acme:submit
                    code="manager.flight.form.button.delete"
                    action="/manager/flight/delete" />
            </c:when>
        </c:choose>

        <c:if test="${_command != 'create'}">
            <acme:button
                code="manager.flight.form.button.legs"
                action="/manager/leg/list?flightId=${id}" />
        </c:if>
    </acme:form>
</body>
</html>