<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Products" active="products">
<div class="admin-head"><h1>Products</h1><a class="btn btn-primary btn-sm" href="${ctx}/admin/products/new">Add product</a></div>
<form class="toolbar" method="get" action="${ctx}/admin/products">
    <label class="sr-only" for="pq">Search</label><input class="input" id="pq" name="q" value="<c:out value='${q}'/>" placeholder="Search products">
    <label class="sr-only" for="pc">Category</label>
    <select class="select" id="pc" name="category"><option value="0">All categories</option><c:forEach var="c" items="${categories}"><option value="${c.id}" ${c.id == categoryFilter ? 'selected' : ''}><c:out value="${c.name}"/></option></c:forEach></select>
    <button class="btn btn-dark btn-sm" type="submit">Filter</button>
</form>
<div class="table-wrap"><table class="data">
    <thead><tr><th></th><th>Product</th><th>Category</th><th class="num">Price</th><th class="num">Stock</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${page.items}">
        <tr>
            <td><img class="thumb-sm" src="${ctx}${gf:img(p.primaryImage)}" alt=""></td>
            <td><strong><c:out value="${p.name}"/></strong><div class="muted" style="font-size:.8rem"><c:if test="${p.featured}">Featured </c:if><c:if test="${p.newArrival}">New </c:if><c:if test="${p.hasDiscount}">${p.discountPercent}% off</c:if></div></td>
            <td><c:out value="${p.categoryName}"/></td>
            <td class="num">${gf:money(p.salePrice)}<c:if test="${p.hasDiscount}"><div class="muted" style="font-size:.8rem"><s>${gf:money(p.price)}</s></div></c:if></td>
            <td class="num"><span class="status ${p.totalStock == 0 ? 'CANCELLED' : ''}">${p.totalStock}</span></td>
            <td><span class="status ${p.active ? 'DELIVERED' : 'off'}">${p.active ? 'Active' : 'Hidden'}</span></td>
            <td class="num" style="white-space:nowrap">
                <a class="btn-text" href="${ctx}/admin/products/edit?id=${p.id}">Edit</a> &middot;
                <a class="btn-text" href="${ctx}/admin/images?productId=${p.id}">Images</a> &middot;
                <a class="btn-text" href="${ctx}/admin/variants?productId=${p.id}">Stock</a> &middot;
                <form class="inline-form" method="post" action="${ctx}/admin/products/toggle"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${p.id}"><input type="hidden" name="active" value="${p.active ? 0 : 1}"><button class="btn-text" type="submit">${p.active ? 'Hide' : 'Show'}</button></form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty page.items}"><tr><td colspan="7" class="muted" style="text-align:center;padding:32px">No products match.</td></tr></c:if>
    </tbody>
</table></div>
<g:pagination pg="${page}" base="${ctx}/admin/products?q=${gf:encode(q)}&category=${categoryFilter}&"/>
</g:adminLayout>

