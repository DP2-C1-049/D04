<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-select code="technician.involves.form.label.task" path="task" readonly="${_command != 'create'}" choices="${tasks}"/>
	<jstl:if test="${_command == 'show'}">	
		<acme:input-integer code="technician.involves.form.label.priority" path="priority" readonly="true"/>
		<acme:input-textbox code="technician.involves.form.label.technician" path="technician" readonly="true"/>
	</jstl:if>
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|delete') && draftMode}">
			<acme:submit code="technician.involves.form.button.delete" action="/technician/involves/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.involves.form.button.create" action="/technician/involves/create?masterId=${masterId}"/>

		</jstl:when>		
	</jstl:choose>
</acme:form>
