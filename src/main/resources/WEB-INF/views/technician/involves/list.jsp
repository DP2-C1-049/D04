<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
<acme:list-column code="technician.involves.list.label.ticker" path="ticker" width="30%"/>
	<acme:list-column code="technician.involved-in.list.label.priority" path="priority" width="30%"/>
	<acme:list-column code="technician.involved-in.list.label.task" path="task" width="30%"/>
	
</acme:list>	
	
<jstl:choose>
<jstl:when test="${_command == 'list'}">
		<jstl:if test="${showCreate}">
		    <acme:button code="technician.involved-in.form.button.create" action="/technician/involves/create?masterId=${masterId}"/>
		
		    <jstl:if test="${hayInvolucrados}">
		        <acme:button code="technician.involved-in.form.button.delete" action="/technician/involves/delete?masterId=${masterId}"/>
		    </jstl:if>
		</jstl:if>
	</jstl:when>

	</jstl:choose>