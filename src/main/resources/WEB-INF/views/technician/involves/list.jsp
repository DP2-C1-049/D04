<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.involves.list.label.priority" path="priority" width="30%"/>
	<acme:list-column code="technician.involves.list.label.task" path="task" width="30%"/>
	
</acme:list>	
	
<jstl:if test="${showCreate}">
	<acme:button code="technician.involves.form.button.create" action="/technician/involves/create?masterId=${masterId}"/>
</jstl:if>


