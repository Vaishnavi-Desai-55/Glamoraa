<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Order ${order.orderNumber}" active="orders">
<nav class="crumbs"><a href="${ctx}/admin/orders">Orders</a><span aria-hidden="true">/</span><span>${order.orderNumber}</span></nav>
<div class="admin-head"><div><h1 style="margin:0">Order ${order.orderNumber}</h1><div class="muted">Placed <fmt:formatDate value="${order.placedAt}" pattern="d MMM yyyy, h:mm a"/></div></div><span class="status ${order.status}">${fn:toLowerCase(order.status)}</span></div>
<div class="two-col" style="grid-template-columns:1.6fr 1fr;align-items:start">
    <div class="card">
        <h2>Items</h2>
        <c:forEach var="i" items="${order.items}">
            <div class="order-item"><img src="${ctx}${gf:img(i.imagePath)}" alt=""><div><strong><c:out value="${i.productName}"/></strong><div class="muted" style="font-size:.85rem"><c:out value="${i.sizeLabel}"/> &middot; <c:out value="${i.colorName}"/> &middot; ${gf:money(i.unitPrice)} &times; ${i.quantity}</div><div class="muted" style="font-size:.78rem">SKU <c:out value="${i.sku}"/></div></div><div>${gf:money(i.lineTotal)}</div></div>
        </c:forEach>
        <div class="totals" style="margin-top:16px;max-width:320px;margin-left:auto"><div><span>Subtotal</span><span>${gf:money(order.subtotal)}</span></div><div><span>Shipping</span><span>${gf:money(order.shippingFee)}</span></div><div class="grand"><span>Total</span><span>${gf:money(order.total)}</span></div></div>
    </div>
    <div>
        <div class="card"><h3>Update status</h3>
            <c:choose>
                <c:when test="${empty order.nextStatuses}"><p class="muted">This order is ${fn:toLowerCase(order.status)}; no further changes are possible.</p></c:when>
                <c:otherwise>
                    <p class="muted" style="font-size:.86rem">Cancelling returns the items to stock (once).</p>
                    <c:forEach var="s" items="${order.nextStatuses}">
                        <form method="post" action="${ctx}/admin/orders/status" style="margin-bottom:10px" data-once ${s == 'CANCELLED' ? 'data-confirm="Cancel this order and return its items to stock?"' : ''}>
                            <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${order.id}"><input type="hidden" name="status" value="${s}">
                            <button class="btn ${s == 'CANCELLED' ? 'btn-outline' : 'btn-primary'} btn-block" type="submit">Mark as ${fn:toLowerCase(s)}</button>
                        </form>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card"><h3>Customer</h3><p><c:out value="${order.customerName}"/><br><span class="muted"><c:out value="${order.customerEmail}"/></span></p></div>
        <div class="card"><h3>Ship to</h3><p><strong><c:out value="${order.shipName}"/></strong><br><c:out value="${order.shipLine1}"/><c:if test="${not empty order.shipLine2}">, <c:out value="${order.shipLine2}"/></c:if><br><c:out value="${order.shipCity}"/>, <c:out value="${order.shipState}"/> <c:out value="${order.shipPostal}"/><br><c:out value="${order.shipCountry}"/><br><c:out value="${order.shipPhone}"/></p></div>
        <div class="card"><h3>Payment</h3><p>Cash on delivery <span class="status ${order.paymentStatus}">${fn:toLowerCase(order.paymentStatus)}</span></p></div>
    </div>
</div>
</g:adminLayout>

