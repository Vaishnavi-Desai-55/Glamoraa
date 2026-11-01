<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="${address.id == 0 ? 'Add address' : 'Edit address'}">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="addresses"/>
    <div>
        <h1>${address.id == 0 ? 'Add an address' : 'Edit address'}</h1>
        <form class="card" method="post" action="${ctx}/account/addresses/save" data-once novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${address.id}"><input type="hidden" name="next" value="<c:out value='${next}'/>">
            <c:if test="${not empty errors}"><div class="alert alert-error" role="alert">Please correct the highlighted fields.</div></c:if>
            <div class="form-grid">
                <g:field name="label" label="Label" value="${address.label}" errors="${errors}" required="true" hint="For example Home or Office" maxlength="40"/>
                <g:field name="recipientName" label="Recipient name" value="${address.recipientName}" errors="${errors}" required="true" autocomplete="name" maxlength="120"/>
                <g:field name="phone" label="Phone" type="tel" value="${address.phone}" errors="${errors}" required="true" autocomplete="tel" maxlength="20"/>
                <g:field name="line1" label="Address line 1" value="${address.line1}" errors="${errors}" required="true" autocomplete="address-line1" maxlength="190" cssClass="full"/>
                <g:field name="line2" label="Address line 2 (optional)" value="${address.line2}" errors="${errors}" autocomplete="address-line2" maxlength="190" cssClass="full"/>
                <g:field name="city" label="City" value="${address.city}" errors="${errors}" required="true" autocomplete="address-level2" maxlength="80"/>
                <g:field name="state" label="State" value="${address.state}" errors="${errors}" required="true" autocomplete="address-level1" maxlength="80"/>
                <g:field name="postalCode" label="Postal code" value="${address.postalCode}" errors="${errors}" required="true" autocomplete="postal-code" maxlength="10"/>
                <g:field name="country" label="Country" value="${address.country}" errors="${errors}" required="true" autocomplete="country-name" maxlength="60"/>
            </div>
            <label class="check" style="margin-bottom:22px"><input type="checkbox" name="defaultAddress" ${address.defaultAddress ? 'checked' : ''}> Use as my default address</label>
            <div style="display:flex;gap:12px;flex-wrap:wrap"><button class="btn btn-primary" type="submit">Save address</button><a class="btn btn-outline" href="${ctx}/account/addresses">Cancel</a></div>
        </form>
    </div>
</div></div>
</g:layout>

