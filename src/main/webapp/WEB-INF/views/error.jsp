<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="${errorTitle}">
<div class="container error-page">
    <div class="code">${errorStatus}</div>
    <h1><c:out value="${errorTitle}"/></h1>
    <p class="muted"><c:out value="${errorMessage}"/></p>
    <p style="margin-top:28px;display:flex;gap:12px;justify-content:center;flex-wrap:wrap"><a class="btn btn-primary" href="${ctx}/">Back to home</a><a class="btn btn-outline" href="${ctx}/products">Browse the shop</a></p>
</div>
</g:layout>

