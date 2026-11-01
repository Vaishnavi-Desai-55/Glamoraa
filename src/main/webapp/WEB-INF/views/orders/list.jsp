<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="My orders">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="orders"/>
    <div>
        <h1>Order history</h1>
        <c:choose>
            <c:when test="${empty page.items}"><div class="empty-state"><h2>No orders yet</h2><p>When you place an order it will appear here.</p><a class="btn btn-outline" href="${ctx}/products">Start shopping</a></div></c:when>
            <c:otherwise>
                <div class="table-wrap"><table class="data">
                    <thead><tr><th>Order</th><th>Date</th><th>Items</th><th>Status</th><th class="num">Total</th></tr></thead>
                    <tbody><c:forEach var="o" items="${page.items}">
                        <tr><td><a href="${ctx}/order-details?id=${o.id}"><strong>${o.orderNumber}</strong></a></td><td><fmt:formatDate value="${o.placedAt}" pattern="d MMM yyyy"/></td><td>${o.itemCount}</td><td><span class="status ${o.status}">${fn:toLowerCase(o.status)}</span></td><td class="num">${gf:money(o.total)}</td></tr>
                    </c:forEach></tbody>
                </table></div>
                <g:pagination pg="${page}" base="${ctx}/orders?"/>
            </c:otherwise>
        </c:choose>
    </div>
</div></div>
</g:layout>

