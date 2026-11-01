<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<c:if test="${not empty user}">
<aside class="drawer drawer-right" id="cart-drawer" role="dialog" aria-modal="true" aria-label="Your bag" aria-hidden="true">
    <div class="drawer-head"><h2>Your bag</h2><button type="button" class="icon-btn" data-close aria-label="Close bag"><g:icon name="close"/></button></div>
    <div class="drawer-body">
        <div id="cart-items"></div>
        <div id="cart-empty" class="empty-state" style="border:0;padding:48px 0" hidden><p>Your bag is empty.</p><a class="btn btn-outline" href="${ctx}/products">Start shopping</a></div>
    </div>
    <div class="drawer-foot" id="cart-foot" hidden>
        <div class="totals">
            <div><span>Subtotal</span><span id="cart-subtotal"></span></div>
            <div><span>Shipping</span><span id="cart-shipping"></span></div>
            <div class="grand"><span>Total</span><span id="cart-total"></span></div>
        </div>
        <div style="display:grid;gap:10px;margin-top:18px">
            <a class="btn btn-primary" href="${ctx}/checkout">Checkout</a>
            <a class="btn btn-outline" href="${ctx}/cart">View bag</a>
        </div>
    </div>
</aside>
</c:if>

