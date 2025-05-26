<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 

		<acme:input-textbox code="administrator.involved-in.form.label.ticker" path="ticker" readonly="true"/>
		<acme:input-select path="type" code="administrator.involved-in.form.label.type" choices="${types}" readonly="true"/>
		<acme:input-integer code="administrator.involved-in.form.label.priority" path="priority" readonly="true"/>
		<acme:input-integer code="administrator.involved-in.form.label.estimatedDuration" path="estimatedDuration" readonly="true"/>
		<acme:input-textbox code="administrator.involved-in.form.label.technician" path="technician" readonly="true"/>
		<acme:input-textbox code="administrator.involved-in.form.label.description" path="description" readonly="true"/>

</acme:form>