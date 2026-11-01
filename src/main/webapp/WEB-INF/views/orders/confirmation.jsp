<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Order placed">
<div class="container-narrow section-tight">
    <div class="confirm">
        <div class="tick"><g:icon name="check"/></div>
        <h1>Thank you, your order is placed</h1>
        <p class="muted">Order number <strong>${order.orderNumber}</strong>. Pay <strong>${gf:money(order.total)}</strong> in cash on delivery.</p>
    </div>
    <div class="card">
        <h2>Delivering to</h2>
        <p><strong><c:out value="${order.shipName}"/></strong><br><c:out value="${order.shipLine1}"/><c:if test="${not empty order.shipLine2}">, <c:out value="${order.shipLine2}"/></c:if><br><c:out value="${order.shipCity}"/>, <c:out value="${order.shipState}"/> <c:out value="${order.shipPostal}"/><br><c:out value="${order.shipCountry}"/></p>
        <c:forEach var="i" items="${order.items}">
            <div class="order-item"><img src="${ctx}${gf:img(i.imagePath)}" alt="" loading="lazy"><div><strong><c:out value="${i.productName}"/></strong><div class="muted" style="font-size:.85rem"><c:out value="${i.sizeLabel}"/> &middot; <c:out value="${i.colorName}"/> &middot; Qty ${i.quantity}</div></div><div>${gf:money(i.lineTotal)}</div></div>
        </c:forEach>
    </div>
    <p style="display:flex;gap:12px;justify-content:center;flex-wrap:wrap;margin-top:28px"><a class="btn btn-primary" href="${ctx}/order-details?id=${order.id}">View order</a><a class="btn btn-outline" href="${ctx}/products">Continue shopping</a></p>
</div>
</g:layout>

