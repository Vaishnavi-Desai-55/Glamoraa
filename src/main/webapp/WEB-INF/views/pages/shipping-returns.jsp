<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Shipping &amp; returns" description="Shipping charges and returns information.">
<div class="container-narrow page-head"><h1>Shipping &amp; returns</h1><p>Sample policy text for this demonstration store. Replace it with your own before going live.</p></div>
<div class="container-narrow prose section-tight">
    <h2>Shipping</h2>
    <ul>
        <li>Orders above <strong>${gf:money(freeShippingThreshold)}</strong> ship free.</li>
        <li>Orders below that amount are charged a flat <strong>${gf:money(flatShippingFee)}</strong> shipping fee.</li>
        <li>The shipping charge is calculated automatically in your bag and again at checkout.</li>
        <li>We currently accept <strong>Cash on Delivery</strong> only.</li>
    </ul>
    <h2>Cancellations</h2>
    <p>You can cancel an order yourself from <a href="${ctx}/orders">your orders</a> until it has been shipped. Cancelled items return to stock automatically.</p>
    <h2>Returns</h2>
    <p>Unworn items with their tags attached may be returned within 14 days of delivery. Please <a href="${ctx}/contact">contact us</a> with your order number to arrange a return.</p>
</div>
</g:layout>

