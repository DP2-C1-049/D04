<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
 	<acme:input-textbox code="authenticated.technician.form.label.licenseNumber" path="licenseNumber" placeholder="authenticated.technician.form.placeholder.licenseNumber"/>
 	<acme:input-textbox code="authenticated.technician.form.label.phoneNumber" path="phoneNumber" placeholder="authenticated.technician.form.placeholder.phoneNumber"/>
 	<acme:input-textbox code="authenticated.technician.form.label.specialization" path="specialisation"/>
 	<acme:input-checkbox code="authenticated.technician.form.label.healthTestPassed" path="passedAnnualHealthTest"/>
 	<acme:input-integer code="authenticated.technician.form.label.yearsOfExperience" path="experienceYears"/>
 	<acme:input-textarea code="authenticated.technician.form.label.certifications" path="certifications"/>
 	
 	<jstl:if test="${_command == 'create'}">
 		<acme:submit code="authenticated.technician.form.button.create" action="/authenticated/technician/create"/>
 		
 	</jstl:if>
 	<jstl:if test="${_command == 'update'}">
 		<acme:submit code="authenticated.technician.form.button.update" action="/authenticated/technician/update"/>
 	</jstl:if>
 
 </acme:form>