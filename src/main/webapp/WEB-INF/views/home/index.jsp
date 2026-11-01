<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="${tagline}" nav="home" description="Thoughtfully curated fashion and lifestyle pieces designed for everyday expression.">

<section class="hero" aria-labelledby="hero-title">
    <div class="hero-media"><img src="${ctx}/assets/images/hero.jpg" alt="A burgundy dress and a camel leather tote in a warm studio setting" fetchpriority="high"></div>
    <div class="container">
        <div class="hero-copy">
            <h1 id="hero-title">Style That Speaks Without Saying a Word.</h1>
            <p>Discover thoughtfully curated fashion designed for your everyday expression.</p>
            <div class="hero-actions">
                <a class="btn btn-primary" href="${ctx}/products">Shop Collection</a>
                <a class="btn btn-outline" href="${ctx}/products?new=1">Explore New Arrivals</a>
            </div>
        </div>
    </div>
</section>

<c:if test="${not empty home.categories}">
<section class="section" aria-labelledby="cat-title">
    <div class="container">
        <div class="section-head" data-reveal><div><h2 id="cat-title">Shop by category</h2><p>Six ways into the edit.</p></div></div>
        <div class="category-grid">
            <c:forEach var="cat" items="${home.categories}">
                <a class="category-tile" href="${ctx}/category/${cat.slug}" data-reveal>
                    <img src="${ctx}${gf:img(cat.imagePath)}" alt="" loading="lazy" width="480" height="640">
                    <span class="label"><strong><c:out value="${cat.name}"/></strong><span><c:out value="${cat.subtitle}"/></span></span>
                </a>
            </c:forEach>
        </div>
    </div>
</section>
</c:if>

<section class="section section-cream" aria-labelledby="new-title">
    <div class="container">
        <div class="section-head" data-reveal><div><h2 id="new-title">New arrivals</h2><p>Just landed, and made to be worn now.</p></div><a class="btn-text" href="${ctx}/products?new=1">View all</a></div>
        <c:choose>
            <c:when test="${empty home.newArrivals}"><div class="empty-state"><h2>New pieces are on their way</h2><p>Check back soon, or browse the full collection.</p><a class="btn btn-outline" href="${ctx}/products">Shop all</a></div></c:when>
            <c:otherwise><div class="product-grid"><c:forEach var="item" items="${home.newArrivals}"><g:productCard p="${item}"/></c:forEach></div></c:otherwise>
        </c:choose>
    </div>
</section>

<c:if test="${not empty home.featured}">
<section class="section" aria-labelledby="feat-title">
    <div class="container">
        <div class="section-head" data-reveal><div><h2 id="feat-title">The featured collection</h2><p>Our editors' favourites for the season.</p></div><a class="btn-text" href="${ctx}/products?featured=1">View collection</a></div>
        <div class="product-grid"><c:forEach var="item" items="${home.featured}"><g:productCard p="${item}"/></c:forEach></div>
    </div>
</section>
</c:if>

<section class="section section-cream" aria-labelledby="story-title">
    <div class="container feature-split">
        <div class="img" data-reveal><img src="${ctx}/assets/images/about.jpg" alt="Warm arches with a folded linen throw" loading="lazy"></div>
        <div class="copy" data-reveal>
            <h2 id="story-title">Fewer, better things.</h2>
            <p class="muted">Glamoraa began with a simple idea: getting dressed should feel effortless. We curate pieces in honest fabrics, with considered proportions, that work together and last well beyond a season.</p>
            <div class="values">
                <div><strong>Considered</strong><span>Small, edited collections</span></div>
                <div><strong>Comfortable</strong><span>Cuts you can live in</span></div>
                <div><strong>Lasting</strong><span>Made to be worn for years</span></div>
            </div>
            <p style="margin-top:28px"><a class="btn btn-outline" href="${ctx}/about">Our story</a></p>
        </div>
    </div>
</section>

<section class="banner" aria-labelledby="banner-title">
    <img src="${ctx}/assets/images/editorial.jpg" alt="" loading="lazy">
    <div class="container"><div class="banner-copy" data-reveal>
        <h2 id="banner-title">Layers worth keeping.</h2>
        <p>Soft knits, tailored jackets and scarves in a palette of rose, sand and charcoal.</p>
        <a class="btn btn-light" href="${ctx}/category/women">Shop the edit <g:icon name="arrow-right"/></a>
    </div></div>
</section>

<c:if test="${not empty home.bestSellers}">
<section class="section" aria-labelledby="best-title">
    <div class="container">
        <div class="section-head" data-reveal><div><h2 id="best-title">Best sellers</h2><p>Most ordered by our customers.</p></div></div>
        <div class="product-grid"><c:forEach var="item" items="${home.bestSellers}"><g:productCard p="${item}"/></c:forEach></div>
    </div>
</section>
</c:if>

<section class="section section-cream" aria-labelledby="news-title">
    <div class="container">
        <div class="newsletter" data-reveal>
            <h2 id="news-title">Join the Glamoraa list</h2>
            <p class="muted">New arrivals and quiet updates, a few times a season. Unsubscribe whenever you like.</p>
            <form method="post" action="${ctx}/newsletter" data-ajax>
                <input type="hidden" name="_csrf" value="${csrfToken}">
                <label class="sr-only" for="nl-email">Email address</label>
                <input class="input" id="nl-email" type="email" name="email" placeholder="Your email address" required maxlength="190" autocomplete="email">
                <button class="btn btn-primary" type="submit">Subscribe</button>
            </form>
        </div>
    </div>
</section>
</g:layout>

