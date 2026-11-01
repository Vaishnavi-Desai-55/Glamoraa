<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Categories" active="categories">
<div class="admin-head"><h1>Categories</h1></div>
<div class="two-col" style="grid-template-columns:1.3fr 1fr;align-items:start">
    <div class="table-wrap"><table class="data">
        <thead><tr><th>Name</th><th class="num">Products</th><th>Status</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="c" items="${categories}">
            <tr><td><strong><c:out value="${c.name}"/></strong><div class="muted" style="font-size:.8rem">/category/${c.slug}</div></td><td class="num">${c.productCount}</td><td><span class="status ${c.active ? 'DELIVERED' : 'off'}">${c.active ? 'Active' : 'Hidden'}</span></td>
                <td class="num" style="white-space:nowrap"><a class="btn-text" href="${ctx}/admin/categories?edit=${c.id}">Edit</a>
                    <c:if test="${c.productCount == 0}"> &middot; <form class="inline-form" method="post" action="${ctx}/admin/categories" data-confirm="Delete this category?"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${c.id}"><button class="btn-text btn-danger-text" type="submit">Delete</button></form></c:if></td></tr>
        </c:forEach>
        </tbody>
    </table></div>
    <form class="card" method="post" action="${ctx}/admin/categories" data-once novalidate>
        <h2>${editing.id == 0 ? 'Add a category' : 'Edit category'}</h2>
        <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="${editing.id}">
        <g:field name="name" label="Name" value="${editing.name}" errors="${errors}" required="true" maxlength="80"/>
        <g:field name="subtitle" label="Subtitle" value="${editing.subtitle}" errors="${errors}" maxlength="160"/>
        <g:field name="description" label="Description" type="textarea" value="${editing.description}" errors="${errors}" rows="3" maxlength="2000"/>
        <g:field name="imagePath" label="Tile image path" value="${editing.imagePath}" errors="${errors}" maxlength="255" hint="For example /assets/images/categories/women.svg"/>
        <g:field name="sortOrder" label="Sort order" type="number" value="${editing.sortOrder}" errors="${errors}"/>
        <label class="check" style="margin-bottom:20px"><input type="checkbox" name="active" ${editing.active ? 'checked' : ''}> Visible in the store</label>
        <div style="display:flex;gap:12px"><button class="btn btn-primary" type="submit">Save category</button><c:if test="${editing.id != 0}"><a class="btn btn-outline" href="${ctx}/admin/categories">Cancel</a></c:if></div>
    </form>
</div>
</g:adminLayout>

