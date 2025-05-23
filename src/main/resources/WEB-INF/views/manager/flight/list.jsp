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
<html>
<head>
    <title>Flight List</title>
</head>
<body>
    <h1>Flight List</h1>
    <acme:list navigable="true" show="show">
        <acme:list-column path="tag" code="manager.flight.list.label.tag" />
        <acme:list-column path="originCity" code="manager.flight.list.label.originCity" />
        <acme:list-column path="destinationCity" code="manager.flight.list.label.destinationCity" />
        <acme:list-column path="indication" code="manager.flight.list.label.indication" />
        <acme:list-column path="cost" code="manager.flight.list.label.cost" />
        <acme:list-column path="description" code="manager.flight.list.label.description" />
        <acme:list-column path="departure" code="manager.flight.list.label.departure" />
        <acme:list-column path="arrival" code="manager.flight.list.label.arrival" />
        <acme:list-column path="numberOfLayovers" code="manager.flight.list.label.numberOfLayovers" />
    </acme:list>
    <acme:button code="manager.flight.list.button.create" action="/manager/flight/create" />
</body>
</html>