<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Checkout" noDrawer="true">
<div class="container section-tight">
    <div class="page-head" style="padding-top:0"><h1>Checkout</h1></div>
    <form method="post" action="${ctx}/place-order" data-once>
        <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="checkoutToken" value="${checkoutToken}">
        <div class="cart-layout">
            <div>
                <section class="checkout-block" aria-labelledby="co-addr">
                    <h2 id="co-addr">1. Delivery address</h2>
                    <c:choose>
                        <c:when test="${empty summary.addresses}">
                            <div class="alert alert-info">You have no saved address yet.</div>
                            <a class="btn btn-outline" href="${ctx}/account/addresses/new?next=${gf:encode('/checkout')}">Add a delivery address</a>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${summary.addresses}" varStatus="s">
                                <label class="choice"><input type="radio" name="addressId" value="${a.id}" required ${s.first ? 'checked' : ''}>
                                    <span><strong><c:out value="${a.label}"/> &middot; <c:out value="${a.recipientName}"/></strong><p><c:out value="${a.oneLine}"/>, <c:out value="${a.country}"/><br><c:out value="${a.phone}"/></p></span>
                                </label>
                            </c:forEach>
                            <a class="btn-text" href="${ctx}/account/addresses/new?next=${gf:encode('/checkout')}">Add another address</a>
                        </c:otherwise>
                    </c:choose>
                </section>
                <section class="checkout-block" aria-labelledby="co-pay">
                    <h2 id="co-pay">2. Payment</h2>
                    <label class="choice"><input type="radio" name="paymentMethod" value="COD" checked><span><strong>Cash on delivery</strong><p>Pay when your order arrives. Online payment is not enabled on this store.</p></span></label>
                </section>
                <section class="checkout-block" aria-labelledby="co-review">
                    <h2 id="co-review">3. Review items</h2>
                    <c:forEach var="i" items="${summary.cart.items}">
                        <div class="order-item"><img src="${ctx}${gf:img(i.imagePath)}" alt="" loading="lazy"><div><strong><c:out value="${i.productName}"/></strong><div class="muted" style="font-size:.85rem"><c:out value="${i.sizeLabel}"/> &middot; <c:out value="${i.colorName}"/> &middot; Qty ${i.quantity}</div></div><div>${gf:money(i.lineTotal)}</div></div>
                    </c:forEach>
                    <a class="btn-text" href="${ctx}/cart">Edit bag</a>
                </section>
            </div>
            <aside class="summary" aria-label="Order total">
                <h2>Order total</h2>
                <div class="totals">
                    <div><span>Subtotal</span><span>${gf:money(summary.subtotal)}</span></div>
                    <div><span>Shipping</span><span>${summary.shipping.signum() == 0 ? 'Free' : gf:money(summary.shipping)}</span></div>
                    <div class="grand"><span>Total</span><span>${gf:money(summary.total)}</span></div>
                </div>
                <button class="btn btn-primary btn-block" type="submit" ${empty summary.addresses ? 'disabled' : ''}>Place order</button>
                <p class="muted" style="font-size:.8rem;margin:14px 0 0;text-align:center">Prices and stock are confirmed again when you place your order.</p>
            </aside>
        </div>
    </form>
</div>
</g:layout>

