<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Addresses">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="addresses"/>
    <div>
        <div class="section-head"><h1 style="margin:0">Saved addresses</h1><a class="btn btn-primary btn-sm" href="${ctx}/account/addresses/new">Add address</a></div>
        <c:choose>
            <c:when test="${empty addresses}"><div class="empty-state"><h2>No addresses yet</h2><p>Add a delivery address to check out faster.</p><a class="btn btn-outline" href="${ctx}/account/addresses/new">Add an address</a></div></c:when>
            <c:otherwise>
                <div class="address-grid">
                    <c:forEach var="a" items="${addresses}">
                        <div class="address-card">
                            <div><strong><c:out value="${a.label}"/></strong> <c:if test="${a.defaultAddress}"><span class="tag">Default</span></c:if></div>
                            <div><c:out value="${a.recipientName}"/><br><c:out value="${a.oneLine}"/><br><c:out value="${a.country}"/><br><span class="muted"><c:out value="${a.phone}"/></span></div>
                            <div class="acts">
                                <a class="btn-text" href="${ctx}/account/addresses/edit?id=${a.id}">Edit</a>
                                <c:if test="${not a.defaultAddress}"><form method="post" action="${ctx}/account/addresses/default"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${a.id}"><button class="btn-text" type="submit">Make default</button></form></c:if>
                                <form method="post" action="${ctx}/account/addresses/delete" data-confirm="Delete this address?"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${a.id}"><button class="btn-text btn-danger-text" type="submit">Delete</button></form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div></div>
</g:layout>

