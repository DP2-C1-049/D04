<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
<acme:list-column code="administrator.involved-in.list.label.ticker" path="ticker" width="30%"/>
	<acme:list-column code="administrator.involved-in.list.label.priority" path="priority" width="30%"/>
	<acme:list-column code="administrator.involved-in.list.label.task" path="task" width="30%"/>
	
</acme:list>	