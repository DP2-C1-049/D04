<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<h2 class="text-center mt-4 mb-4">
    <acme:print code="technician.dashboard.form.title.general-indicators"/>
</h2>

<div class="container">


    <table class="table table-bordered table-striped text-center mb-5">
        <thead class="table-dark">
            <tr><th colspan="2">
            <acme:print code="technician.dashboard.form.label.general"/></th></tr>
        </thead>
        <tbody>
            <tr><th><acme:print code="technician.dashboard.form.label.recordWithNearestInspection"/></th><td><acme:print value="${recordWithNearestInspection}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.top5AircraftsWithMostTasks"/></th>
                <td>
                    <jstl:forEach var="aircraft" items="${top5AircraftsWithMostTasks}">
                        <acme:print value="${aircraft.id}"/><br/>
                    </jstl:forEach>
                </td>
            </tr>
        </tbody>
    </table>


    <table class="table table-bordered table-striped text-center mb-5">
        <thead class="table-dark">
            <tr><th colspan="2"><acme:print code="technician.dashboard.form.label.eur"/></th></tr>
        </thead>
        <tbody>
            <tr><th><acme:print code="technician.dashboard.form.label.averageEstimatedCostLastYear"/></th><td><acme:print value="${averageEstimatedCostLastYearEUR}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.minimumEstimatedCostLastYear"/></th><td><acme:print value="${minimumEstimatedCostLastYearEUR}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.maximumEstimatedCostLastYear"/></th><td><acme:print value="${maximumEstimatedCostLastYearEUR}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.sTDDEVEstimatedCostLastYear"/></th><td><acme:print value="${sTDDEVEstimatedCostLastYearEUR}"/></td></tr>
        </tbody>
    </table>


    <table class="table table-bordered table-striped text-center mb-5">
        <thead class="table-dark">
            <tr><th colspan="2"><acme:print code="technician.dashboard.form.label.usd"/></th></tr>
        </thead>
        <tbody>
            <tr><th><acme:print code="technician.dashboard.form.label.averageEstimatedCostLastYear"/></th><td><acme:print value="${averageEstimatedCostLastYearUSD}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.minimumEstimatedCostLastYear"/></th><td><acme:print value="${minimumEstimatedCostLastYearUSD}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.maximumEstimatedCostLastYear"/></th><td><acme:print value="${maximumEstimatedCostLastYearUSD}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.sTDDEVEstimatedCostLastYear"/></th><td><acme:print value="${sTDDEVEstimatedCostLastYearUSD}"/></td></tr>
        </tbody>
    </table>


    <table class="table table-bordered table-striped text-center mb-5">
        <thead class="table-dark">
            <tr><th colspan="2"><acme:print code="technician.dashboard.form.label.gbp"/></th></tr>
        </thead>
        <tbody>
            <tr><th><acme:print code="technician.dashboard.form.label.averageEstimatedCostLastYear"/></th><td><acme:print value="${averageEstimatedCostLastYearGBP}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.minimumEstimatedCostLastYear"/></th><td><acme:print value="${minimumEstimatedCostLastYearGBP}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.maximumEstimatedCostLastYear"/></th><td><acme:print value="${maximumEstimatedCostLastYearGBP}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.sTDDEVEstimatedCostLastYear"/></th><td><acme:print value="${sTDDEVEstimatedCostLastYearGBP}"/></td></tr>
        </tbody>
    </table>


    <table class="table table-bordered text-center mb-5">
        <thead class="table-dark">
            <tr><th colspan="2"><acme:print code="technician.dashboard.form.label.duration"/></th></tr>
        </thead>
        <tbody>
            <tr><th><acme:print code="technician.dashboard.form.label.averageEstimatedDurationTask"/></th><td><acme:print value="${averageEstimatedDurationTask}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.minimumEstimatedDurationTask"/></th><td><acme:print value="${minimumEstimatedDurationTask}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.maximumEstimatedDurationTask"/></th><td><acme:print value="${maximumEstimatedDurationTask}"/></td></tr>
            <tr><th><acme:print code="technician.dashboard.form.label.sTDDEVEstimatedDurationTask"/></th><td><acme:print value="${sTDDEVEstimatedDurationTask}"/></td></tr>
        </tbody>
    </table>


    <h2 class="text-center mb-3">
        <acme:print code="technician.dashboard.form.label.maintenanceRecord-statuses"/>
    </h2>

    <div class="text-center mb-5">
        <canvas id="canvas" width="600" height="300"></canvas>
    </div>

</div>


<script type="text/javascript">
    $(document).ready(function() {
        const data = {
            labels: ["PENDING", "IN_PROGRESS", "COMPLETED"],
            datasets: [{
                label: "Maintenance Records",
                backgroundColor: ["#ffc107", "#17a2b8", "#28a745"], 
                data: [
                    <acme:print value="${numberMaintenanceRecordPending}"/>,
                    <acme:print value="${numberMaintenanceRecordInProgress}"/>,
                    <acme:print value="${numberMaintenanceRecordCompleted}"/>
                ]
            }]
        };

        const options = {
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 }
                }
            },
            plugins: {
                legend: { display: false }
            }
        };

        const ctx = document.getElementById("canvas").getContext("2d");
        new Chart(ctx, {
            type: "bar",
            data: data,
            options: options
        });
    });
</script>

<acme:return/>