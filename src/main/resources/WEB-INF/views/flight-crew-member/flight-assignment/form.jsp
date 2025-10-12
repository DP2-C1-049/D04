<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.flight-crew-member" path="flightCrewMember" readonly="true"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.leg" path="leg" choices="${legs}"/>		
	<div class="form-group">
        <label for="dutySelect">
            <acme:print code="flight-crew-member.flight-assignment.form.label.duty"/>
        </label>
		<jstl:if test="${acme:anyOf(_command, 'show|update|publish')}">
        <select id="dutySelect" name="duty" class="form-control" 
                onchange="if(this.value) window.location.href='/Acme-ANS-C3/flight-crew-member/flight-assignment/${_command }?duty=' + this.value + '&id=${id}'">
            
            <jstl:forEach var="choice" items="${duty.iterator()}">		
			<acme:input-option 
				value="${choice.getKey()}" 
				code="${choice.getLabel()}" 
				selected="${choice.isSelected()}"/>
		</jstl:forEach>
           
        </select>
        </jstl:if>
        <jstl:if test="${_command == 'create'}">
        <select id="dutySelect" name="duty" class="form-control" 
                onchange="if(this.value) window.location.href='/Acme-ANS-C3/flight-crew-member/flight-assignment/${_command }?duty=' + this.value">
            
            <jstl:forEach var="choice" items="${duty.iterator()}">		
			<acme:input-option 
				value="${choice.getKey()}" 
				code="${choice.getLabel()}" 
				selected="${choice.isSelected()}"/>
		</jstl:forEach>
           
        </select>
        </jstl:if>
    </div>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.current-status" path="currentStatus" choices="${currentStatus}"/>
	<acme:input-textbox code="flight-crew-member.flight-assignment.form.label.remarks" path="remarks"/>
	<acme:input-moment code="flight-crew-member.flight-assignment.form.label.moment" path="moment" readonly="true"/>
	

	<jstl:choose>	 
		
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true && isCompleted == false}">  <%-- Si está completed la leg ya no se puede publicar--%>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')  && isCompleted==true && draftMode ==true}">
			<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>		
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>	
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')  && isCompleted==true && draftMode ==false}">
			<acme:button code="flight-crew-member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>		
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true && isCompleted==false}">
	
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
		</jstl:when>

		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>