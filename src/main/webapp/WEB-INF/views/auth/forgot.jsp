<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Forgot password">
<div class="container-narrow section">
    <h1>Forgot your password?</h1>
    <div class="alert alert-info">Password reset by email is <strong>not configured</strong> on this store, so we cannot send a reset link.</div>
    <p class="muted">If you are signed in you can change your password from your <a class="btn-text" href="${ctx}/account/profile">profile</a>. Otherwise please <a class="btn-text" href="${ctx}/contact">contact us</a>.</p>
    <p><a class="btn btn-outline" href="${ctx}/login">Back to log in</a></p>
</div>
</g:layout>

