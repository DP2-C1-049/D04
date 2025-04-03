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

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.tag" path="tag" width="20%"/>
	<acme:list-column code="manager.flight.list.label.indication" path="indication" width="10%"/>
	<acme:list-column code="manager.flight.list.label.cost" path="cost" width="10%"/>
	<acme:list-column code="manager.flight.list.label.description" path="description" width="10%"/>
	<acme:list-column code="manager.flight.list.label.departure" path="departure" width="10%"/>
	<acme:list-column code="manager.flight.list.label.arrival" path="arrival" width="10%"/>
	<acme:list-column code="manager.flight.list.label.originCity" path="originCity" width="10%"/>
	<acme:list-column code="manager.flight.list.label.destinationCity" path="destinationCity" width="10%"/>
	<acme:list-column code="manager.flight.list.label.numberOfLayovers" path="numberOfLayovers" width="10%"/>	
	<acme:list-payload path="payload"/>
</acme:list>