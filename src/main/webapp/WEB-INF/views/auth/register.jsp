<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Create account" bodyClass="auth-page">
<div class="auth-wrap">
    <div class="auth-art"><img src="${ctx}/assets/images/editorial.jpg" alt=""></div>
    <div class="auth-panel">
        <h1>Create your account</h1>
        <p class="muted">Save your wishlist, addresses and order history.</p>
        <c:if test="${not empty errors}"><div class="alert alert-error" role="alert">Please correct the highlighted fields.</div></c:if>
        <form method="post" action="${ctx}/register" data-once novalidate>
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <g:field name="fullName" label="Full name" value="${fullName}" errors="${errors}" required="true" autocomplete="name" maxlength="120"/>
            <g:field name="email" label="Email" type="email" value="${email}" errors="${errors}" required="true" autocomplete="email" maxlength="190"/>
            <g:field name="phone" label="Phone (optional)" type="tel" value="${phone}" errors="${errors}" autocomplete="tel" maxlength="20" inputmode="tel"/>
            <g:field name="password" label="Password" type="password" errors="${errors}" required="true" autocomplete="new-password" hint="At least 8 characters, with a letter and a number." maxlength="100"/>
            <g:field name="confirmPassword" label="Confirm password" type="password" errors="${errors}" required="true" autocomplete="new-password" maxlength="100"/>
            <button class="btn btn-primary btn-block" type="submit">Create account</button>
        </form>
        <p class="auth-alt">Already have an account? <a href="${ctx}/login">Log in</a></p>
    </div>
</div>
</g:layout>

