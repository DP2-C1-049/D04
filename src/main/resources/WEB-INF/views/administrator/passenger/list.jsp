
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list navigable="false">
    <acme:list-column code="customer.passenger.list.label.fullName" path="fullName" />
	<acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" />
	<acme:list-column code="customer.passenger.list.label.dateOfBirth" path="dateOfBirth" />
	<acme:list-column code="customer.passenger.list.label.draftmode" path="draftMode" />

</acme:list>