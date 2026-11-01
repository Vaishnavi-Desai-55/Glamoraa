<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Variants &amp; stock" active="products">
<div class="admin-head"><div><h1 style="margin:0">Variants &amp; stock</h1><div class="muted"><c:out value="${p.name}"/></div></div><a class="btn btn-outline btn-sm" href="${ctx}/admin/products">Back to products</a></div>
<div class="table-wrap" style="margin-bottom:28px"><table class="data">
    <thead><tr><th>Size</th><th>Colour</th><th>SKU</th><th>Stock</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="v" items="${variants}">
        <tr>
            <td><c:out value="${v.sizeLabel}"/></td>
            <td><span style="display:inline-block;width:12px;height:12px;border-radius:50%;background:<c:out value='${v.colorHex}'/>;border:1px solid rgba(0,0,0,.15);vertical-align:middle;margin-right:6px"></span><c:out value="${v.colorName}"/></td>
            <td class="muted"><c:out value="${v.sku}"/></td>
            <td>
                <form class="inline-form" method="post" action="${ctx}/admin/variants"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="variantId" value="${v.id}"><input type="hidden" name="action" value="stock">
                    <label class="sr-only" for="st-${v.id}">Stock for ${v.sizeLabel} ${v.colorName}</label><input class="input" id="st-${v.id}" type="number" name="stock" value="${v.stockQuantity}" min="0" max="100000"><button class="btn btn-outline btn-sm" type="submit">Save</button>
                    <c:if test="${v.stockQuantity <= 5}"><span class="status ${v.stockQuantity == 0 ? 'CANCELLED' : 'PLACED'}">${v.stockQuantity == 0 ? 'Out' : 'Low'}</span></c:if>
                </form>
            </td>
            <td><span class="status ${v.active ? 'DELIVERED' : 'off'}">${v.active ? 'Active' : 'Disabled'}</span></td>
            <td class="num"><form class="inline-form" method="post" action="${ctx}/admin/variants"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="variantId" value="${v.id}"><input type="hidden" name="action" value="toggle"><input type="hidden" name="active" value="${v.active ? 0 : 1}"><button class="btn-text" type="submit">${v.active ? 'Disable' : 'Enable'}</button></form></td>
        </tr>
    </c:forEach>
    <c:if test="${empty variants}"><tr><td colspan="6" class="muted" style="text-align:center;padding:28px">No variants yet. Add a size and colour below so customers can buy this product.</td></tr></c:if>
    </tbody>
</table></div>
<div class="two-col">
    <form class="card" method="post" action="${ctx}/admin/variants" data-once>
        <h2>Add a variant</h2>
        <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="action" value="add">
        <div class="field"><label for="v-size">Size</label><select class="select" id="v-size" name="sizeId" required><option value="">Choose</option><c:forEach var="s" items="${sizes}"><option value="${s.id}"><c:out value="${s.label}"/></option></c:forEach></select></div>
        <div class="field"><label for="v-color">Colour</label><select class="select" id="v-color" name="colorId" required><option value="">Choose</option><c:forEach var="c" items="${colors}"><option value="${c.id}"><c:out value="${c.name}"/></option></c:forEach></select></div>
        <g:field name="stock" label="Starting stock" type="number" value="0" required="true" min="0" max="100000"/>
        <button class="btn btn-primary" type="submit">Add variant</button>
    </form>
    <div>
        <form class="card" method="post" action="${ctx}/admin/variants" data-once>
            <h3>New size</h3>
            <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="action" value="addSize">
            <div class="form-grid"><g:field name="label" label="Label" required="true" maxlength="20"/><g:field name="sortOrder" label="Sort order" type="number" value="100"/></div>
            <button class="btn btn-outline btn-sm" type="submit">Add size</button>
        </form>
        <form class="card" method="post" action="${ctx}/admin/variants" data-once>
            <h3>New colour</h3>
            <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="action" value="addColor">
            <div class="form-grid"><g:field name="name" label="Name" required="true" maxlength="40"/><g:field name="hex" label="Colour code" value="#" required="true" maxlength="7" hint="Like #A1B2C3"/></div>
            <button class="btn btn-outline btn-sm" type="submit">Add colour</button>
        </form>
    </div>
</div>
</g:adminLayout>

