<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="${p.name}" nav="${p.categorySlug}" description="${p.name} - ${p.categoryName} at Glamoraa">
<div class="container">
    <nav class="crumbs" style="padding-top:28px" aria-label="Breadcrumb"><a href="${ctx}/">Home</a><span aria-hidden="true">/</span><a href="${ctx}/category/${p.categorySlug}"><c:out value="${p.categoryName}"/></a><span aria-hidden="true">/</span><span><c:out value="${p.name}"/></span></nav>

    <div class="pdp">
        <div class="gallery">
            <div class="gallery-thumbs">
                <c:forEach var="im" items="${detail.images}" varStatus="s">
                    <button type="button" class="thumb" data-src="${ctx}${im.imagePath}" data-alt="<c:out value='${empty im.altText ? p.name : im.altText}'/>" <c:if test="${s.first}">aria-current="true"</c:if> aria-label="Show image ${s.count}"><img src="${ctx}${im.imagePath}" alt="" loading="lazy"></button>
                </c:forEach>
            </div>
            <button type="button" class="gallery-main" id="gallery-main" aria-label="Enlarge image">
                <c:set var="first" value="${empty detail.images ? null : detail.images[0]}"/>
                <img id="gallery-main-img" src="${ctx}${gf:img(first.imagePath)}" alt="<c:out value='${empty first.altText ? p.name : first.altText}'/>" width="800" height="1000" fetchpriority="high">
            </button>
        </div>

        <div class="pdp-info">
            <h1><c:out value="${p.name}"/></h1>
            <c:if test="${p.hasReviews}"><div class="rating"><g:stars value="${p.avgRating}"/> <a href="#reviews">${p.avgRating} (${p.reviewCount} ${p.reviewCount == 1 ? 'review' : 'reviews'})</a></div></c:if>
            <div class="price">
                <span class="now">${gf:money(p.salePrice)}</span>
                <c:if test="${p.hasDiscount}"><span class="was">${gf:money(p.price)}</span><span class="off">${p.discountPercent}% off</span></c:if>
            </div>
            <p class="pdp-desc"><c:out value="${p.description}"/></p>

            <c:choose>
            <c:when test="${empty detail.variants or not p.inStock}">
                <div class="alert alert-info">This piece is currently sold out. Save it to your wishlist and check back soon.</div>
                <div class="buy-row"><button type="button" class="wish-btn" data-wish="${p.id}" aria-pressed="${wishIds.contains(p.id) ? 'true' : 'false'}" aria-label="Save to wishlist" style="min-height:52px;width:auto;padding:0 22px;gap:8px;display:inline-flex;align-items:center"><g:icon name="heart"/> Save to wishlist</button></div>
            </c:when>
            <c:otherwise>
            <form method="post" action="${ctx}/cart/add" data-add-to-cart novalidate>
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <input type="hidden" name="productId" value="${p.id}">
                <input type="hidden" name="variantId" value="">
                <div class="option-group">
                    <div class="option-label"><strong>Colour</strong><span id="chosen-color" class="muted"></span></div>
                    <div class="color-options" role="group" aria-label="Colour">
                        <c:forEach var="co" items="${detail.colors}"><button type="button" class="swatch" data-color="${co.id}" data-name="<c:out value='${co.name}'/>" aria-pressed="false" aria-label="<c:out value='${co.name}'/>"><span style="background:<c:out value='${co.hexCode}'/>"></span></button></c:forEach>
                    </div>
                </div>
                <div class="option-group">
                    <div class="option-label"><strong>Size</strong></div>
                    <div class="size-options" role="group" aria-label="Size">
                        <c:forEach var="sz" items="${detail.sizes}"><button type="button" class="opt" data-size="${sz.id}" aria-pressed="false"><c:out value="${sz.label}"/></button></c:forEach>
                    </div>
                </div>
                <p class="stock-note" id="stock-note" aria-live="polite"></p>
                <div class="buy-row">
                    <div class="qty">
                        <button type="button" data-qty-step="-1" aria-label="Decrease quantity"><g:icon name="minus"/></button>
                        <input type="number" name="quantity" value="1" min="1" max="${maxQty}" aria-label="Quantity" readonly>
                        <button type="button" data-qty-step="1" aria-label="Increase quantity"><g:icon name="plus"/></button>
                    </div>
                    <button class="btn btn-primary" type="submit" disabled>Add to bag</button>
                    <button type="button" class="wish-btn" data-wish="${p.id}" aria-pressed="${wishIds.contains(p.id) ? 'true' : 'false'}" aria-label="Save to wishlist"><g:icon name="heart"/></button>
                </div>
            </form>
            <script type="application/json" id="variant-data">[<c:forEach var="v" items="${detail.variants}" varStatus="s">{"id":${v.id},"size":${v.sizeId},"color":${v.colorId},"stock":${v.stockQuantity}}<c:if test="${not s.last}">,</c:if></c:forEach>]</script>
            </c:otherwise>
            </c:choose>

            <div class="assurances">
                <div><g:icon name="truck"/> <span>Free shipping on orders above ${gf:money(freeShippingThreshold)}; otherwise ${gf:money(flatShippingFee)}.</span></div>
                <div><g:icon name="lock"/> <span>Pay securely with cash on delivery.</span></div>
                <div><g:icon name="return"/> <span>See our <a class="btn-text" href="${ctx}/shipping-returns">shipping &amp; returns</a> information.</span></div>
            </div>
        </div>
    </div>

    <div class="pdp-sections">
        <section aria-labelledby="spec-title">
            <h2 id="spec-title">Details</h2>
            <c:choose>
                <c:when test="${empty p.specList}"><p class="muted">No additional details for this piece.</p></c:when>
                <c:otherwise><table class="specs"><tbody><c:forEach var="row" items="${p.specList}"><tr><th scope="row"><c:out value="${row[0]}"/></th><td><c:out value="${row[1]}"/></td></tr></c:forEach></tbody></table></c:otherwise>
            </c:choose>
        </section>
        <section id="reviews" aria-labelledby="rev-title">
            <h2 id="rev-title">Reviews</h2>
            <c:choose>
                <c:when test="${empty detail.reviews}"><p class="muted">No reviews yet. Reviews appear here after customers receive this product.</p></c:when>
                <c:otherwise>
                    <c:forEach var="r" items="${detail.reviews}">
                        <div class="review"><g:stars value="${r.rating + 0.0}"/><h4><c:out value="${r.title}"/></h4><p><c:out value="${r.body}"/></p><div class="by"><c:out value="${r.userName}"/> &middot; <fmt:formatDate value="${r.createdAt}" pattern="d MMM yyyy"/></div></div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            <c:if test="${detail.canReview}">
                <form method="post" action="${ctx}/product/review" class="card" style="margin-top:24px" data-once>
                    <h3>Write a review</h3>
                    <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}">
                    <fieldset style="border:0;padding:0;margin:0 0 16px"><legend class="sr-only">Rating</legend>
                        <div class="rating-input">
                            <c:forEach var="n" begin="1" end="5"><c:set var="k" value="${6 - n}"/><input type="radio" id="rate${k}" name="rating" value="${k}" required><label for="rate${k}" title="${k} stars"><span class="sr-only">${k} stars</span><g:icon name="star"/></label></c:forEach>
                        </div>
                    </fieldset>
                    <g:field name="title" label="Title" maxlength="120"/>
                    <g:field name="body" label="Your review" type="textarea" maxlength="2000"/>
                    <button class="btn btn-primary" type="submit">Publish review</button>
                </form>
            </c:if>
        </section>
    </div>

    <c:if test="${not empty detail.related}">
        <section class="section" aria-labelledby="rel-title">
            <div class="section-head"><h2 id="rel-title">You may also like</h2></div>
            <div class="product-grid"><c:forEach var="item" items="${detail.related}"><g:productCard p="${item}"/></c:forEach></div>
        </section>
    </c:if>
</div>

<dialog class="lightbox" id="lightbox" aria-label="Product image">
    <button type="button" class="icon-btn close" data-lightbox-close aria-label="Close image"><g:icon name="close"/></button>
    <img id="lightbox-img" src="" alt="">
</dialog>
</g:layout>

