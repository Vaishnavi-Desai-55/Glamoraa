<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Images" active="products">
<div class="admin-head"><div><h1 style="margin:0">Images</h1><div class="muted"><c:out value="${p.name}"/></div></div><a class="btn btn-outline btn-sm" href="${ctx}/admin/products">Back to products</a></div>
<c:choose>
    <c:when test="${empty images}"><div class="alert alert-info">This product has no images yet. The first one you add becomes the primary image.</div></c:when>
    <c:otherwise>
        <div class="image-grid" style="margin-bottom:28px">
            <c:forEach var="im" items="${images}">
                <div class="image-tile">
                    <img src="${ctx}${gf:img(im.imagePath)}" alt="<c:out value='${im.altText}'/>">
                    <div>
                        <div><c:if test="${im.primary}"><span class="tag">Primary</span></c:if> <span class="muted"><c:out value="${im.altText}"/></span></div>
                        <div style="display:flex;gap:12px">
                            <c:if test="${not im.primary}"><form method="post" action="${ctx}/admin/images"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="imageId" value="${im.id}"><input type="hidden" name="action" value="primary"><button class="btn-text" type="submit">Make primary</button></form></c:if>
                            <form method="post" action="${ctx}/admin/images" data-confirm="Remove this image?"><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="imageId" value="${im.id}"><input type="hidden" name="action" value="delete"><button class="btn-text btn-danger-text" type="submit">Remove</button></form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<div class="two-col">
    <form class="card" method="post" action="${ctx}/admin/images" enctype="multipart/form-data" data-once>
        <h2>Upload an image</h2>
        <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="action" value="upload">
        <div class="field"><label for="f-file">Image file</label><input class="input" id="f-file" type="file" name="file" accept="image/jpeg,image/png,image/webp,image/gif" required><span class="hint">JPG, PNG, WebP or GIF, up to 3 MB. The file type is checked on the server.</span></div>
        <g:field name="alt" label="Alt text" maxlength="200" hint="Describe the image for screen readers."/>
        <button class="btn btn-primary" type="submit">Upload</button>
    </form>
    <form class="card" method="post" action="${ctx}/admin/images" data-once>
        <h2>Use an existing site image</h2>
        <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="productId" value="${p.id}"><input type="hidden" name="action" value="path">
        <g:field name="imagePath" label="Image path" required="true" hint="For example /assets/images/products/example.jpg" maxlength="255"/>
        <g:field name="alt" label="Alt text" maxlength="200"/>
        <button class="btn btn-outline" type="submit">Add image</button>
    </form>
</div>
</g:adminLayout>

