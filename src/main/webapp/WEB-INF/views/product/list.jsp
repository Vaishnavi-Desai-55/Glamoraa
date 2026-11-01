<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<c:set var="navKey" value="${newOnly ? 'new' : (not empty category ? category.slug : (isSearch ? '' : 'shop'))}"/>
<g:layout title="${isSearch and not empty q ? ('Search: ' += q) : heading}" nav="${navKey}">
<div class="container">
    <div class="page-head">
        <nav class="crumbs" aria-label="Breadcrumb"><a href="${ctx}/">Home</a><span aria-hidden="true">/</span><a href="${ctx}/products">Shop</a><c:if test="${not empty category}"><span aria-hidden="true">/</span><span><c:out value="${category.name}"/></span></c:if></nav>
        <c:choose>
            <c:when test="${isSearch}">
                <h1><c:choose><c:when test="${empty q}">Search</c:when><c:otherwise>Results for &ldquo;<c:out value="${q}"/>&rdquo;</c:otherwise></c:choose></h1>
            </c:when>
            <c:otherwise>
                <h1><c:out value="${heading}"/></h1>
                <c:if test="${not empty category}"><p><c:out value="${category.description}"/></p></c:if>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="cat-chips">
        <a class="chip ${empty category and not newOnly and not featuredOnly ? 'is-active' : ''}" href="${ctx}/products">All</a>
        <c:forEach var="cat" items="${navCategories}"><a class="chip ${category.id == cat.id ? 'is-active' : ''}" href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/></a></c:forEach>
        <a class="chip ${newOnly ? 'is-active' : ''}" href="${ctx}/products?new=1">New arrivals</a>
    </div>

    <div class="toolbar">
        <form method="get" action="${formAction}" aria-label="Filter and sort">
            <c:if test="${isSearch}"><label class="sr-only" for="tq">Search</label><input class="input" id="tq" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search products" maxlength="100"></c:if>
            <c:if test="${not isSearch and not empty q}"><input type="hidden" name="q" value="<c:out value='${q}'/>"></c:if>
            <c:if test="${not empty category and fn:endsWith(formAction, '/products')}"><input type="hidden" name="category" value="${category.slug}"></c:if>
            <c:if test="${newOnly}"><input type="hidden" name="new" value="1"></c:if>
            <c:if test="${featuredOnly}"><input type="hidden" name="featured" value="1"></c:if>
            <label class="sr-only" for="tmin">Minimum price</label><input class="input price-in" id="tmin" type="number" name="min" value="${min}" min="0" step="1" placeholder="Min &#8377;" inputmode="numeric">
            <label class="sr-only" for="tmax">Maximum price</label><input class="input price-in" id="tmax" type="number" name="max" value="${max}" min="0" step="1" placeholder="Max &#8377;" inputmode="numeric">
            <label class="sr-only" for="tsort">Sort by</label>
            <select class="select" id="tsort" name="sort">
                <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest first</option>
                <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Price: low to high</option>
                <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Price: high to low</option>
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name: A to Z</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name: Z to A</option>
            </select>
            <button class="btn btn-dark btn-sm" type="submit">Apply</button>
            <c:if test="${filtered or sort != 'newest'}"><a class="btn-text" href="${formAction}<c:if test='${isSearch and not empty q}'>?q=${gf:encode(q)}</c:if>">Clear filters</a></c:if>
        </form>
        <span class="results-count" aria-live="polite">${page.total} ${page.total == 1 ? 'product' : 'products'}</span>
    </div>

    <c:choose>
        <c:when test="${empty page.items}">
            <div class="empty-state">
                <h2>Nothing matched that search</h2>
                <p><c:choose><c:when test="${filtered or not empty q}">Try a different word, widen the price range or clear your filters.</c:when><c:otherwise>There are no products in this collection yet.</c:otherwise></c:choose></p>
                <a class="btn btn-outline" href="${ctx}/products">Browse everything</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="product-grid"><c:forEach var="item" items="${page.items}"><g:productCard p="${item}"/></c:forEach></div>
            <g:pagination pg="${page}" base="${pageBase}"/>
        </c:otherwise>
    </c:choose>
</div>
<div class="section-tight"></div>
</g:layout>

