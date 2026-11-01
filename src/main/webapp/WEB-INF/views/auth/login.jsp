<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Log in" bodyClass="auth-page">
<div class="auth-wrap">
    <div class="auth-art"><img src="${ctx}/assets/images/hero.jpg" alt=""></div>
    <div class="auth-panel">
        <h1>Welcome back</h1>
        <p class="muted">Log in to see your bag, wishlist and orders.</p>
        <c:if test="${not empty error}"><div class="alert alert-error" role="alert"><c:out value="${error}"/></div></c:if>
        <form method="post" action="${ctx}/login" data-once>
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <input type="hidden" name="next" value="<c:out value='${next}'/>">
            <g:field name="email" label="Email" type="email" value="${email}" required="true" autocomplete="email" maxlength="190"/>
            <g:field name="password" label="Password" type="password" required="true" autocomplete="current-password" maxlength="100"/>
            <button class="btn btn-primary btn-block" type="submit">Log in</button>
        </form>
        <p class="auth-alt"><a href="${ctx}/forgot-password">Forgot your password?</a></p>
        <p class="auth-alt">New to Glamoraa? <a href="${ctx}/register">Create an account</a></p>
    </div>
</div>
</g:layout>

