<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="active" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="csrf-token" content="${csrfToken}">
    <meta name="robots" content="noindex, nofollow">
    <title><c:out value="${title}"/> | Admin | <c:out value="${siteName}"/></title>
    <link rel="icon" href="${ctx}/assets/icons/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500&amp;family=Poppins:wght@300;400;500&amp;display=swap">
    <link rel="stylesheet" href="${ctx}/assets/css/glamoraa.css">
    <script src="${ctx}/assets/js/init.js"></script>
</head>
<body class="admin-body" data-ctx="${ctx}" data-auth="1" data-no-drawer>
<a class="skip-link" href="#main">Skip to content</a>
<div class="admin-shell">
    <aside class="admin-side">
        <a class="logo" href="${ctx}/admin"><c:out value="${siteName}"/></a>
        <small>Admin</small>
        <nav aria-label="Admin">
            <a href="${ctx}/admin" <c:if test="${active == 'dashboard'}">aria-current="page"</c:if>>Dashboard</a>
            <a href="${ctx}/admin/products" <c:if test="${active == 'products'}">aria-current="page"</c:if>>Products</a>
            <a href="${ctx}/admin/categories" <c:if test="${active == 'categories'}">aria-current="page"</c:if>>Categories</a>
            <a href="${ctx}/admin/orders" <c:if test="${active == 'orders'}">aria-current="page"</c:if>>Orders</a>
            <a href="${ctx}/admin/customers" <c:if test="${active == 'customers'}">aria-current="page"</c:if>>Customers</a>
            <hr>
            <a href="${ctx}/" target="_blank" rel="noopener">View storefront</a>
            <form method="post" action="${ctx}/logout"><input type="hidden" name="_csrf" value="${csrfToken}"><button type="submit">Log out</button></form>
        </nav>
    </aside>
    <main class="admin-main" id="main"><jsp:doBody/></main>
</div>
<jsp:include page="/WEB-INF/fragments/toast.jsp"/>
<script src="${ctx}/assets/js/glamoraa.js" defer></script>
</body>
</html>
