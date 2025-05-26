<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 

     <acme:hidden-data path="maintenanceRecordId"/>
     	<acme:input-textbox code="administrator.maintenance-record.form.label.ticker" path="ticker" readonly="true"/>
		<acme:input-moment code="administrator.maintenance-record.form.label.maintenanceMoment" path="moment" readonly="true"/>
		<acme:input-select path="status" code="administrator.maintenance-record.form.label.status" choices="${statuses}" readonly="true"/>
		<acme:input-moment code="administrator.maintenance-record.form.label.next-inspection" path="nextInspectionDueTime" readonly="true"/>
		<acme:input-select code="administrator.maintenance-record.form.label.aircraft" path="aircraft" choices="${aircrafts}" readonly="true"/>
		<acme:input-money code="administrator.maintenance-record.form.label.estimated-cost" path="estimatedCost" readonly="true"/>
		<acme:input-textarea code="administrator.maintenance-record.form.label.notes" path="notes" readonly="true"/>
 
		<jstl:if test="${acme:anyOf(_command, 'show')}">
		<acme:button code="administrator.maintenance-record.form.button.involved-in" action="/administrator/involves/list?masterId=${id}"/>
		</jstl:if>

</acme:form>