<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="My account">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="dashboard"/>
    <div>
        <h1>Hello, <c:out value="${fn:substringBefore(user.fullName, ' ')}"/></h1>
        <p class="muted">Manage your orders, addresses and saved pieces.</p>
        <div class="stat-grid">
            <a class="stat" href="${ctx}/orders"><div class="n">${recentOrders.size() > 0 ? 'View' : 'None yet'}</div><div class="l">Your orders</div></a>
            <a class="stat" href="${ctx}/wishlist"><div class="n">${wishlistCount}</div><div class="l">Saved to wishlist</div></a>
            <a class="stat" href="${ctx}/cart"><div class="n">${cartCount}</div><div class="l">Items in your bag</div></a>
        </div>
        <div class="two-col">
            <div class="card">
                <h2>Recent orders</h2>
                <c:choose>
                    <c:when test="${empty recentOrders}"><p class="muted">You have not placed an order yet.</p><a class="btn btn-outline btn-sm" href="${ctx}/products">Start shopping</a></c:when>
                    <c:otherwise>
                        <c:forEach var="o" items="${recentOrders}">
                            <div class="order-item" style="grid-template-columns:1fr auto">
                                <div><a href="${ctx}/order-details?id=${o.id}"><strong>${o.orderNumber}</strong></a><div class="muted" style="font-size:.84rem"><fmt:formatDate value="${o.placedAt}" pattern="d MMM yyyy"/> &middot; ${o.itemCount} ${o.itemCount == 1 ? 'item' : 'items'}</div></div>
                                <div style="text-align:right"><span class="status ${o.status}">${fn:toLowerCase(o.status)}</span><div>${gf:money(o.total)}</div></div>
                            </div>
                        </c:forEach>
                        <p style="margin:14px 0 0"><a class="btn-text" href="${ctx}/orders">All orders</a></p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="card">
                <h2>Default address</h2>
                <c:choose>
                    <c:when test="${empty defaultAddress}"><p class="muted">No saved address yet.</p><a class="btn btn-outline btn-sm" href="${ctx}/account/addresses/new">Add an address</a></c:when>
                    <c:otherwise><p><strong><c:out value="${defaultAddress.recipientName}"/></strong><br><c:out value="${defaultAddress.oneLine}"/><br><c:out value="${defaultAddress.country}"/><br><c:out value="${defaultAddress.phone}"/></p><a class="btn-text" href="${ctx}/account/addresses">Manage addresses</a></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div></div>
</g:layout>

