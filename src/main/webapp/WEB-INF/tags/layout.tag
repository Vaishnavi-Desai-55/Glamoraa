<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" %>
<%@ attribute name="description" %>
<%@ attribute name="nav" %>
<%@ attribute name="bodyClass" %>
<%@ attribute name="noDrawer" type="java.lang.Boolean" %>
<c:set var="activeNav" value="${nav}" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="csrf-token" content="${csrfToken}">
    <meta name="description" content="<c:out value='${empty description ? tagline : description}'/>">
    <title><c:if test="${not empty title}"><c:out value="${title}"/> | </c:if><c:out value="${siteName}"/></title>
    <link rel="icon" href="${ctx}/assets/icons/favicon.svg" type="image/svg+xml">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600&amp;family=Poppins:wght@300;400;500&amp;display=swap">
    <link rel="stylesheet" href="${ctx}/assets/css/glamoraa.css">
    <script src="${ctx}/assets/js/init.js"></script>
</head>
<body class="${bodyClass}" data-ctx="${ctx}" data-auth="${empty user ? '0' : '1'}" <c:if test="${noDrawer}">data-no-drawer</c:if>>
<a class="skip-link" href="#main">Skip to content</a>
<jsp:include page="/WEB-INF/fragments/header.jsp"/>
<main id="main" class="page-in"><jsp:doBody/></main>
<jsp:include page="/WEB-INF/fragments/footer.jsp"/>
<jsp:include page="/WEB-INF/fragments/cart-drawer.jsp"/>
<jsp:include page="/WEB-INF/fragments/toast.jsp"/>
<script src="${ctx}/assets/js/glamoraa.js" defer></script>
</body>
</html>
