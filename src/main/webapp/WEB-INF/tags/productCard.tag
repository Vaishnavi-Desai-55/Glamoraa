<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<%@ attribute name="p" required="true" type="com.glamoraa.model.Product" %>
<article class="product-card" data-reveal>
    <div class="pc-media">
        <a class="pc-link" href="${ctx}/product?id=${p.id}" aria-label="<c:out value='${p.name}'/>, view details" tabindex="-1">
            <img class="pc-img-1" src="${ctx}${gf:img(p.primaryImage)}" alt="<c:out value='${p.name}'/>" loading="lazy" width="640" height="800">
            <c:if test="${not empty p.secondImage}"><img class="pc-img-2" src="${ctx}${p.secondImage}" alt="" loading="lazy" width="640" height="800"></c:if>
        </a>
        <div class="pc-tags">
            <c:if test="${not p.inStock}"><span class="pc-tag out">Sold out</span></c:if>
            <c:if test="${p.inStock and p.newArrival}"><span class="pc-tag">New</span></c:if>
            <c:if test="${p.hasDiscount}"><span class="pc-tag sale">${p.discountPercent}% off</span></c:if>
        </div>
        <button type="button" class="pc-wish" data-wish="${p.id}" aria-pressed="${wishIds.contains(p.id) ? 'true' : 'false'}" aria-label="Save <c:out value='${p.name}'/> to wishlist"><g:icon name="heart"/></button>
        <a class="pc-quick" href="${ctx}/product?id=${p.id}">View details</a>
    </div>
    <div class="pc-body">
        <div class="pc-cat"><c:out value="${p.categoryName}"/></div>
        <h3 class="pc-name"><a href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a></h3>
        <div class="price">
            <span class="now">${gf:money(p.salePrice)}</span>
            <c:if test="${p.hasDiscount}"><span class="was">${gf:money(p.price)}</span></c:if>
        </div>
        <c:if test="${p.hasReviews}"><div class="rating"><g:stars value="${p.avgRating}"/> <span>${p.avgRating} (${p.reviewCount})</span></div></c:if>
    </div>
</article>
