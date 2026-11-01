<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="${p.id == 0 ? 'New product' : 'Edit product'}" active="products">
<div class="admin-head"><h1>${p.id == 0 ? 'New product' : 'Edit product'}</h1><c:if test="${p.id != 0}"><span><a class="btn btn-outline btn-sm" href="${ctx}/admin/images?productId=${p.id}">Images</a> <a class="btn btn-outline btn-sm" href="${ctx}/admin/variants?productId=${p.id}">Variants &amp; stock</a></span></c:if></div>
<form class="card" method="post" action="${ctx}/admin/products/save" data-once novalidate style="max-width:860px">
    <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${p.id}">
    <c:if test="${not empty errors}"><div class="alert alert-error" role="alert">Please correct the highlighted fields.</div></c:if>
    <div class="form-grid">
        <g:field name="name" label="Name" value="${p.name}" errors="${errors}" required="true" maxlength="160" cssClass="full"/>
        <div class="field ${not empty errors.categoryId ? 'has-error' : ''}"><label for="f-categoryId">Category *</label>
            <select class="select" id="f-categoryId" name="categoryId" required><option value="">Choose a category</option><c:forEach var="c" items="${categories}"><option value="${c.id}" ${c.id == p.categoryId ? 'selected' : ''}><c:out value="${c.name}"/></option></c:forEach></select>
            <c:if test="${not empty errors.categoryId}"><span class="field-error"><c:out value="${errors.categoryId}"/></span></c:if></div>
        <g:field name="price" label="Price (&#8377;)" type="number" value="${priceText}" errors="${errors}" required="true" min="0" step="0.01" hint="The full price before any discount."/>
        <g:field name="discountPercent" label="Discount (%)" type="number" value="${p.discountPercent}" errors="${errors}" min="0" max="90" step="1" hint="0 for no discount. The sale price is calculated from this percentage."/>
        <div class="field"><label>Visibility &amp; labels</label>
            <label class="check"><input type="checkbox" name="active" ${p.active ? 'checked' : ''}> Visible in the store</label>
            <label class="check"><input type="checkbox" name="featured" ${p.featured ? 'checked' : ''}> Featured collection</label>
            <label class="check"><input type="checkbox" name="newArrival" ${p.newArrival ? 'checked' : ''}> New arrival</label></div>
        <g:field name="description" label="Description" type="textarea" value="${p.description}" errors="${errors}" maxlength="5000" cssClass="full"/>
        <g:field name="specifications" label="Specifications" type="textarea" value="${p.specifications}" errors="${errors}" maxlength="3000" rows="5" hint="One per line, as Label: value (for example Material: Linen)." cssClass="full"/>
    </div>
    <div style="display:flex;gap:12px"><button class="btn btn-primary" type="submit">Save product</button><a class="btn btn-outline" href="${ctx}/admin/products">Cancel</a></div>
</form>
</g:adminLayout>

