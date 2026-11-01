<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Wishlist" nav="">
<div class="container section-tight">
    <div class="page-head" style="padding-top:0"><h1>Your wishlist</h1><p>Pieces you have saved. Choose a size and colour to move one into your bag.</p></div>
    <c:choose>
        <c:when test="${empty wish.products}"><div class="empty-state"><h2>Nothing saved yet</h2><p>Tap the heart on any product to keep it here.</p><a class="btn btn-outline" href="${ctx}/products">Discover the collection</a></div></c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="item" items="${wish.products}">
                    <div>
                        <g:productCard p="${item}"/>
                        <c:set var="vs" value="${wish.variantsByProduct[item.id]}"/>
                        <c:choose>
                            <c:when test="${empty vs}"><p class="muted" style="font-size:.85rem;margin-top:10px">Currently sold out.</p></c:when>
                            <c:otherwise>
                                <form method="post" action="${ctx}/wishlist/move" style="display:grid;gap:8px;margin-top:10px" data-once>
                                    <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${item.id}">
                                    <label class="sr-only" for="v-${item.id}">Size and colour</label>
                                    <select class="select" id="v-${item.id}" name="variantId" required>
                                        <option value="">Choose size &amp; colour</option>
                                        <c:forEach var="v" items="${vs}"><option value="${v.id}"><c:out value="${v.sizeLabel}"/> / <c:out value="${v.colorName}"/></option></c:forEach>
                                    </select>
                                    <button class="btn btn-outline btn-sm" type="submit">Move to bag</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                        <form method="post" action="${ctx}/wishlist/remove" style="margin-top:8px"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${item.id}"><button class="btn-text btn-danger-text" type="submit" style="font-size:.84rem">Remove</button></form>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</g:layout>

