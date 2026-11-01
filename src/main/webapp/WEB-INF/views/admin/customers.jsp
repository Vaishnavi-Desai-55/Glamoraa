<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Customers" active="customers">
<div class="admin-head"><h1>Customers</h1></div>
<form class="toolbar" method="get" action="${ctx}/admin/customers"><label class="sr-only" for="cq">Search</label><input class="input" id="cq" name="q" value="<c:out value='${q}'/>" placeholder="Name or email"><button class="btn btn-dark btn-sm" type="submit">Search</button></form>
<div class="table-wrap"><table class="data">
    <thead><tr><th>Name</th><th>Email</th><th>Phone</th><th>Joined</th><th>Status</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="u" items="${page.items}">
        <tr><td><c:out value="${u.fullName}"/></td><td><c:out value="${u.email}"/></td><td><c:out value="${u.phone}"/></td><td><fmt:formatDate value="${u.createdAt}" pattern="d MMM yyyy"/></td><td><span class="status ${u.active ? 'DELIVERED' : 'off'}">${u.active ? 'Active' : 'Disabled'}</span></td>
            <td class="num"><form class="inline-form" method="post" action="${ctx}/admin/customers" ${u.active ? 'data-confirm="Disable this account? They will not be able to log in."' : ''}><input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="id" value="${u.id}"><input type="hidden" name="active" value="${u.active ? 0 : 1}"><button class="btn-text ${u.active ? 'btn-danger-text' : ''}" type="submit">${u.active ? 'Disable' : 'Enable'}</button></form></td></tr>
    </c:forEach>
    <c:if test="${empty page.items}"><tr><td colspan="6" class="muted" style="text-align:center;padding:32px">No customers found.</td></tr></c:if>
    </tbody>
</table></div>
<g:pagination pg="${page}" base="${ctx}/admin/customers?q=${gf:encode(q)}&"/>
</g:adminLayout>

