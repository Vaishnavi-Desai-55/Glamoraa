<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Orders" active="orders">
<div class="admin-head"><h1>Orders</h1></div>
<form class="toolbar" method="get" action="${ctx}/admin/orders">
    <label class="sr-only" for="oq">Search</label><input class="input" id="oq" name="q" value="<c:out value='${q}'/>" placeholder="Order number, name or email">
    <label class="sr-only" for="os">Status</label>
    <select class="select" id="os" name="status"><option value="">All statuses</option><c:forEach var="s" items="${['PLACED','CONFIRMED','SHIPPED','DELIVERED','CANCELLED']}"><option value="${s}" ${s == status ? 'selected' : ''}>${fn:toLowerCase(s)}</option></c:forEach></select>
    <button class="btn btn-dark btn-sm" type="submit">Filter</button>
</form>
<div class="table-wrap"><table class="data">
    <thead><tr><th>Order</th><th>Customer</th><th>Date</th><th>Status</th><th>Payment</th><th class="num">Total</th></tr></thead>
    <tbody>
    <c:forEach var="o" items="${page.items}">
        <tr><td><a href="${ctx}/admin/orders/view?id=${o.id}"><strong>${o.orderNumber}</strong></a></td><td><c:out value="${o.customerName}"/><div class="muted" style="font-size:.8rem"><c:out value="${o.customerEmail}"/></div></td><td><fmt:formatDate value="${o.placedAt}" pattern="d MMM yyyy"/></td><td><span class="status ${o.status}">${fn:toLowerCase(o.status)}</span></td><td><span class="status ${o.paymentStatus}">${fn:toLowerCase(o.paymentStatus)}</span></td><td class="num">${gf:money(o.total)}</td></tr>
    </c:forEach>
    <c:if test="${empty page.items}"><tr><td colspan="6" class="muted" style="text-align:center;padding:32px">No orders found.</td></tr></c:if>
    </tbody>
</table></div>
<g:pagination pg="${page}" base="${ctx}/admin/orders?q=${gf:encode(q)}&status=${status}&"/>
</g:adminLayout>

