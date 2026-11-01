<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Your bag" noDrawer="true">
<div class="container section-tight">
    <div class="page-head" style="padding-top:0"><h1>Your bag</h1></div>
    <c:choose>
        <c:when test="${empty cart.items}"><div class="empty-state"><h2>Your bag is empty</h2><p>When you add something you love, it will wait for you here.</p><a class="btn btn-primary" href="${ctx}/products">Continue shopping</a></div></c:when>
        <c:otherwise>
        <div class="cart-layout">
            <div>
                <c:forEach var="i" items="${cart.items}">
                    <div class="cart-line">
                        <a href="${ctx}/product?id=${i.productId}"><img src="${ctx}${gf:img(i.imagePath)}" alt="" loading="lazy"></a>
                        <div>
                            <h3><a href="${ctx}/product?id=${i.productId}"><c:out value="${i.productName}"/></a></h3>
                            <div class="meta"><c:out value="${i.sizeLabel}"/> &middot; <c:out value="${i.colorName}"/> &middot; ${gf:money(i.unitPrice)} <c:if test="${i.unitPrice.compareTo(i.originalPrice) < 0}"><s class="muted">${gf:money(i.originalPrice)}</s></c:if></div>
                            <div class="actions">
                                <form method="post" action="${ctx}/cart/update" data-qty-form>
                                    <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="itemId" value="${i.id}">
                                    <div class="qty">
                                        <button type="button" data-qty-step="-1" aria-label="Decrease quantity of <c:out value='${i.productName}'/>" ${i.quantity <= 1 ? 'disabled' : ''}><g:icon name="minus"/></button>
                                        <input type="number" name="quantity" value="${i.quantity}" min="1" max="${i.stock < maxQty ? (i.stock < 1 ? 1 : i.stock) : maxQty}" aria-label="Quantity" readonly>
                                        <button type="button" data-qty-step="1" aria-label="Increase quantity of <c:out value='${i.productName}'/>"><g:icon name="plus"/></button>
                                    </div>
                                </form>
                                <form method="post" action="${ctx}/cart/remove"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="itemId" value="${i.id}"><button class="btn-text btn-danger-text" type="submit">Remove</button></form>
                            </div>
                            <c:if test="${not i.available}"><div class="line-warn">This item is no longer available. Please remove it to continue.</div></c:if>
                            <c:if test="${i.shortStock}"><div class="line-warn">Only ${i.stock} left in stock. Please reduce the quantity.</div></c:if>
                        </div>
                        <div class="line-total">${gf:money(i.lineTotal)}</div>
                    </div>
                </c:forEach>
                <form method="post" action="${ctx}/cart/clear" style="margin-top:18px" data-confirm="Remove everything from your bag?"><input type="hidden" name="_csrf" value="${csrfToken}"><button class="btn-text" type="submit">Clear bag</button></form>
            </div>
            <aside class="summary" aria-label="Order summary">
                <h2>Summary</h2>
                <c:if test="${not empty toFreeShipping}">
                    <div class="ship-progress">Add ${gf:money(toFreeShipping)} more for free shipping.<div class="bar"><i style="width:${cart.subtotal.multiply(100).divide(freeShippingThreshold, 0, 1)}%"></i></div></div>
                </c:if>
                <div class="totals">
                    <div><span>Subtotal (${cart.itemCount} ${cart.itemCount == 1 ? 'item' : 'items'})</span><span>${gf:money(cart.subtotal)}</span></div>
                    <div><span>Shipping</span><span>${shipping.signum() == 0 ? 'Free' : gf:money(shipping)}</span></div>
                    <div class="grand"><span>Total</span><span>${gf:money(total)}</span></div>
                </div>
                <c:choose>
                    <c:when test="${cart.checkoutReady}"><a class="btn btn-primary btn-block" href="${ctx}/checkout">Proceed to checkout</a></c:when>
                    <c:otherwise><span class="btn btn-primary btn-block" aria-disabled="true">Fix the items above to continue</span></c:otherwise>
                </c:choose>
                <p class="muted" style="font-size:.8rem;margin:14px 0 0;text-align:center">Cash on delivery &middot; Final prices are confirmed at checkout</p>
            </aside>
        </div>
        </c:otherwise>
    </c:choose>
</div>
</g:layout>

