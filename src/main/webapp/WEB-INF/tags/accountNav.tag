<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="active" required="true" %>
<nav class="side-nav" aria-label="Account">
    <a href="${ctx}/account" <c:if test="${active == 'dashboard'}">aria-current="page"</c:if>>Dashboard</a>
    <a href="${ctx}/orders" <c:if test="${active == 'orders'}">aria-current="page"</c:if>>Orders</a>
    <a href="${ctx}/wishlist" <c:if test="${active == 'wishlist'}">aria-current="page"</c:if>>Wishlist</a>
    <a href="${ctx}/account/addresses" <c:if test="${active == 'addresses'}">aria-current="page"</c:if>>Addresses</a>
    <a href="${ctx}/account/profile" <c:if test="${active == 'profile'}">aria-current="page"</c:if>>Profile</a>
    <form method="post" action="${ctx}/logout"><input type="hidden" name="_csrf" value="${csrfToken}"><button type="submit">Log out</button></form>
</nav>
