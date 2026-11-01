<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<c:if test="${not empty announcement}"><div class="announcement" role="region" aria-label="Announcement"><c:out value="${announcement}"/></div></c:if>
<header class="site-header">
    <div class="container header-inner">
        <button type="button" class="icon-btn menu-toggle" data-open="mobile-nav" aria-expanded="false" aria-controls="mobile-nav" aria-label="Open menu"><g:icon name="menu"/></button>
        <a href="${ctx}/" class="logo" aria-label="${siteName} home"><c:out value="${siteName}"/></a>
        <nav class="main-nav" aria-label="Main">
            <a class="nav-link" href="${ctx}/" <c:if test="${activeNav == 'home'}">aria-current="page"</c:if>>Home</a>
            <div class="has-mega">
                <a class="nav-link" href="${ctx}/products" <c:if test="${activeNav == 'shop'}">aria-current="page"</c:if>>Shop <g:icon name="chevron-down"/></a>
                <div class="mega">
                    <div class="mega-inner">
                        <div class="mega-col">
                            <h3>Shop by category</h3>
                            <c:forEach var="cat" items="${navCategories}" end="2"><a href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/><small><c:out value="${cat.subtitle}"/></small></a></c:forEach>
                        </div>
                        <div class="mega-col">
                            <h3>&nbsp;</h3>
                            <c:forEach var="cat" items="${navCategories}" begin="3"><a href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/><small><c:out value="${cat.subtitle}"/></small></a></c:forEach>
                        </div>
                        <div class="mega-col">
                            <h3>Collections</h3>
                            <a href="${ctx}/products?new=1">New arrivals</a>
                            <a href="${ctx}/products?featured=1">The featured collection</a>
                            <a href="${ctx}/products?sort=price_asc">Price: low to high</a>
                            <a href="${ctx}/products">Shop everything</a>
                        </div>
                        <div class="mega-feature">
                            <img src="${ctx}/assets/images/editorial.jpg" alt="" loading="lazy">
                            <div>Fresh this week<br><a class="btn-text" href="${ctx}/products?new=1">Explore new arrivals</a></div>
                        </div>
                    </div>
                </div>
            </div>
            <a class="nav-link" href="${ctx}/category/women" <c:if test="${activeNav == 'women'}">aria-current="page"</c:if>>Women</a>
            <a class="nav-link" href="${ctx}/category/men" <c:if test="${activeNav == 'men'}">aria-current="page"</c:if>>Men</a>
            <a class="nav-link" href="${ctx}/category/accessories" <c:if test="${activeNav == 'accessories'}">aria-current="page"</c:if>>Accessories</a>
            <a class="nav-link" href="${ctx}/products?new=1" <c:if test="${activeNav == 'new'}">aria-current="page"</c:if>>New Arrivals</a>
        </nav>
        <div class="header-actions">
            <button type="button" class="icon-btn" data-open="search-overlay" aria-expanded="false" aria-controls="search-overlay" aria-label="Search"><g:icon name="search"/></button>
            <c:choose>
                <c:when test="${not empty user}">
                    <div class="account-menu">
                        <a class="icon-btn" href="${ctx}/account" aria-label="My account"><g:icon name="user"/></a>
                        <div class="account-panel">
                            <div class="who"><c:out value="${user.fullName}"/></div>
                            <a href="${ctx}/account">Dashboard</a>
                            <a href="${ctx}/orders">My orders</a>
                            <a href="${ctx}/account/addresses">Addresses</a>
                            <a href="${ctx}/account/profile">Profile</a>
                            <c:if test="${user.admin}"><a href="${ctx}/admin">Admin dashboard</a></c:if>
                            <form method="post" action="${ctx}/logout"><input type="hidden" name="_csrf" value="${csrfToken}"><button type="submit">Log out</button></form>
                        </div>
                    </div>
                </c:when>
                <c:otherwise><a class="icon-btn" href="${ctx}/login" aria-label="Log in or register"><g:icon name="user"/></a></c:otherwise>
            </c:choose>
            <a class="icon-btn" href="${ctx}/wishlist" aria-label="Wishlist"><g:icon name="heart"/><span class="dot" id="wish-dot" <c:if test="${empty wishIds}">hidden</c:if>></span></a>
            <a class="icon-btn" href="${ctx}/cart" data-open="cart-drawer" aria-label="Shopping bag" aria-controls="cart-drawer"><g:icon name="bag"/><span class="badge" data-cart-count <c:if test="${cartCount == 0}">hidden</c:if>>${cartCount}</span></a>
        </div>
    </div>
</header>

<div class="scrim" id="scrim"></div>

<aside class="drawer drawer-left" id="mobile-nav" role="dialog" aria-modal="true" aria-label="Menu" aria-hidden="true">
    <div class="drawer-head"><span class="logo"><c:out value="${siteName}"/></span><button type="button" class="icon-btn" data-close aria-label="Close menu"><g:icon name="close"/></button></div>
    <nav class="drawer-body mobile-nav" aria-label="Mobile">
        <a href="${ctx}/">Home</a>
        <a href="${ctx}/products">Shop</a>
        <div class="sub"><c:forEach var="cat" items="${navCategories}"><a href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/></a></c:forEach></div>
        <a href="${ctx}/products?new=1">New Arrivals</a>
        <a href="${ctx}/wishlist">Wishlist</a>
        <c:choose>
            <c:when test="${not empty user}">
                <a href="${ctx}/account">My account</a>
                <a href="${ctx}/orders">My orders</a>
                <c:if test="${user.admin}"><a href="${ctx}/admin">Admin dashboard</a></c:if>
            </c:when>
            <c:otherwise><a href="${ctx}/login">Log in</a><a href="${ctx}/register">Create an account</a></c:otherwise>
        </c:choose>
    </nav>
    <c:if test="${not empty user}">
        <div class="drawer-foot"><form method="post" action="${ctx}/logout"><input type="hidden" name="_csrf" value="${csrfToken}"><button class="btn btn-outline btn-block" type="submit">Log out</button></form></div>
    </c:if>
</aside>

<div class="search-overlay" id="search-overlay" role="dialog" aria-modal="true" aria-label="Search" aria-hidden="true">
    <div class="container">
        <form class="search-form" action="${ctx}/search" method="get" role="search">
            <label class="sr-only" for="search-q">Search products</label>
            <input id="search-q" name="q" type="search" placeholder="Search dresses, loafers, totes..." autocomplete="off" maxlength="100" data-autofocus>
            <button class="icon-btn" type="submit" aria-label="Search"><g:icon name="search"/></button>
            <button class="icon-btn" type="button" data-close aria-label="Close search"><g:icon name="close"/></button>
        </form>
        <div class="search-suggest"><c:forEach var="cat" items="${navCategories}"><a class="chip" href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/></a></c:forEach><a class="chip" href="${ctx}/products?new=1">New arrivals</a></div>
    </div>
</div>

