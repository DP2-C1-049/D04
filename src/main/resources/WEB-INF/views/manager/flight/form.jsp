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

<acme:form readonly="false">
	<acme:input-textbox code="manager.flight.form.label.tag" path="tag" />
	<acme:input-checkbox code="manager.flight.form.label.indication" path="indication" />
	<acme:input-textbox code="manager.flight.form.label.cost" path="cost" />
	<acme:input-textbox code="manager.flight.form.label.description" path="description" />
	<acme:input-moment code="manager.flight.form.label.departure" path="departure" />
	<acme:input-moment code="manager.flight.form.label.arrival" path="arrival" />
	<jstl:if test="${draftMode == true}">
		<acme:submit code="manager.flight.form.button.publish" action="/manager/flight/publish" />
	</jstl:if> 
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:input-checkbox code="manager.flight.form.label.confirmation" path="confirmation"/>
			<acme:submit code="manager.flight.form.button.create" action="/manager/flight/create" />
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update') && draftMode}">
			<acme:submit code="manager.flight.form.button.update" action="/manager/flight/update" />
			<acme:submit code="manager.flight.form.button.delete" action="/manager/flight/delete" />
			<acme:input-checkbox code="manager.flight.form.label.confirmation" path="confirmation"/>
		</jstl:when>
	</jstl:choose>
</acme:form>