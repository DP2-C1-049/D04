<%--
- menu.jsp
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
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.albpallop" action="https://www.youtube.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.rafberalv" action="https://app.clockify.me/tracker"/>
			<acme:menu-suboption code="master.menu.anonymous.serponlop" action="https://lichess.org/"/>
			<acme:menu-suboption code="master.menu.anonymous.samcocdel" action="https://futbol-11.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.javmanriq" action="https://www.listadomanga.es/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.aircraft.list" action="/administrator/aircraft/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>		
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.assistanceAgent" access="hasRealm('AssistanceAgents')">
			<acme:menu-suboption code="master.menu.assistance-agent.list-resolved" action="/assistance-agents/claim/list-resolved"/>			
			<acme:menu-suboption code="master.menu.assistance-agent.list-pending" action="/assistance-agents/claim/list-pending"/>	

		<acme:menu-option code="master.menu.list" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.list-bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.list-passengers" action="/customer/passenger/list"/>
		</acme:menu-option>
		
 		<acme:menu-option code="master.menu.create" access="hasRealm('Customer')">
 			<acme:menu-suboption code="master.menu.booking.create" action="/customer/booking/create"/>
			<acme:menu-suboption code="master.menu.passenger.create" action="/customer/passenger/create"/>	
 		</acme:menu-option>
 		
 		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
 			<acme:menu-suboption code="master.menu.technician.maintenance-records" action="/technician/maintenance-record/list"/>
 
 		</acme:menu-option>		

		<acme:menu-option code="master.menu.manager" access="hasRealm('Manager')">
			<acme:menu-suboption code="master.menu.manager.list-flights" action="/manager/flight/list"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.maintenanceRecord.list" action="/technician/maintenance-record/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.task.list" action="/technician/task/list"/>

		</acme:menu-option>
	</acme:menu-left>

	<acme:menu-left>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>

		</acme:menu-option>
		<acme:menu-option code="master.menu.flight-crew-member" access="hasRealm('FlightCrewMember')">
 			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.completedlist" action="/flight-crew-member/flight-assignment/completed-list"/>
 			<acme:menu-suboption code="master.menu.flight-crew-member.flight-assignment.plannedlist" action="/flight-crew-member/flight-assignment/planned-list"/>
 		</acme:menu-option>
	</acme:menu-left>
</acme:menu-bar>

