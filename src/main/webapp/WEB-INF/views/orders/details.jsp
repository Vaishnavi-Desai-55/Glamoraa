<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Order ${order.orderNumber}">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="orders"/>
    <div>
        <nav class="crumbs" aria-label="Breadcrumb"><a href="${ctx}/orders">Orders</a><span aria-hidden="true">/</span><span>${order.orderNumber}</span></nav>
        <div class="admin-head"><div><h1 style="margin:0">Order ${order.orderNumber}</h1><div class="muted">Placed <fmt:formatDate value="${order.placedAt}" pattern="d MMM yyyy, h:mm a"/></div></div><span class="status ${order.status}">${fn:toLowerCase(order.status)}</span></div>
        <c:if test="${order.status != 'CANCELLED'}">
            <ol class="timeline" aria-label="Order progress">
                <li class="done">Placed</li>
                <li class="${order.status == 'CONFIRMED' or order.status == 'SHIPPED' or order.status == 'DELIVERED' ? 'done' : ''}">Confirmed</li>
                <li class="${order.status == 'SHIPPED' or order.status == 'DELIVERED' ? 'done' : ''}">Shipped</li>
                <li class="${order.status == 'DELIVERED' ? 'done' : ''}">Delivered</li>
            </ol>
        </c:if>
        <div class="card">
            <h2>Items</h2>
            <c:forEach var="i" items="${order.items}">
                <div class="order-item"><img src="${ctx}${gf:img(i.imagePath)}" alt="" loading="lazy"><div><strong><c:out value="${i.productName}"/></strong><div class="muted" style="font-size:.85rem"><c:out value="${i.sizeLabel}"/> &middot; <c:out value="${i.colorName}"/> &middot; ${gf:money(i.unitPrice)} &times; ${i.quantity}</div><div class="muted" style="font-size:.78rem">SKU <c:out value="${i.sku}"/></div></div><div>${gf:money(i.lineTotal)}</div></div>
            </c:forEach>
            <div class="totals" style="margin-top:16px;max-width:320px;margin-left:auto">
                <div><span>Subtotal</span><span>${gf:money(order.subtotal)}</span></div>
                <div><span>Shipping</span><span>${order.shippingFee.signum() == 0 ? 'Free' : gf:money(order.shippingFee)}</span></div>
                <div class="grand"><span>Total</span><span>${gf:money(order.total)}</span></div>
            </div>
        </div>
        <div class="two-col" style="margin-top:20px">
            <div class="card"><h3>Delivery address</h3><p><strong><c:out value="${order.shipName}"/></strong><br><c:out value="${order.shipLine1}"/><c:if test="${not empty order.shipLine2}">, <c:out value="${order.shipLine2}"/></c:if><br><c:out value="${order.shipCity}"/>, <c:out value="${order.shipState}"/> <c:out value="${order.shipPostal}"/><br><c:out value="${order.shipCountry}"/><br><c:out value="${order.shipPhone}"/></p></div>
            <div class="card"><h3>Payment</h3><p>Cash on delivery<br><span class="status ${order.paymentStatus}">${fn:toLowerCase(order.paymentStatus)}</span></p></div>
        </div>
        <c:if test="${order.cancellable}">
            <form method="post" action="${ctx}/order-cancel" style="margin-top:24px" data-confirm="Cancel this order? This cannot be undone." data-once>
                <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${order.id}">
                <button class="btn btn-outline" type="submit">Cancel order</button>
            </form>
        </c:if>
    </div>
</div></div>
</g:layout>

