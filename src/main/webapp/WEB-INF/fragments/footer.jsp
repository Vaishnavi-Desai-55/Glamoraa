<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<footer class="site-footer">
    <div class="container">
        <div class="footer-grid">
            <div class="footer-brand">
                <a href="${ctx}/" class="logo"><c:out value="${siteName}"/></a>
                <p><c:out value="${tagline}"/> Thoughtfully curated fashion and lifestyle pieces, designed for everyday expression.</p>
            </div>
            <div><h3>Shop</h3>
                <c:forEach var="cat" items="${navCategories}"><a href="${ctx}/category/${cat.slug}"><c:out value="${cat.name}"/></a></c:forEach>
                <a href="${ctx}/products?new=1">New arrivals</a>
            </div>
            <div><h3>Customer service</h3>
                <a href="${ctx}/contact">Contact us</a><a href="${ctx}/shipping-returns">Shipping &amp; returns</a><a href="${ctx}/orders">Track an order</a><a href="${ctx}/account">My account</a>
            </div>
            <div><h3>About</h3>
                <a href="${ctx}/about">Our story</a><a href="${ctx}/privacy">Privacy policy</a><a href="${ctx}/terms">Terms &amp; conditions</a>
            </div>
            <div><h3>Contact</h3>
                <c:choose>
                    <c:when test="${not empty contactEmail}"><a href="mailto:<c:out value='${contactEmail}'/>"><c:out value="${contactEmail}"/></a></c:when>
                    <c:otherwise><a href="${ctx}/contact">Send us a message</a></c:otherwise>
                </c:choose>
                <c:if test="${not empty instagram}"><a href="<c:out value='${instagram}'/>" rel="noopener" target="_blank">Instagram</a></c:if>
                <c:if test="${not empty facebook}"><a href="<c:out value='${facebook}'/>" rel="noopener" target="_blank">Facebook</a></c:if>
                <c:if test="${not empty pinterest}"><a href="<c:out value='${pinterest}'/>" rel="noopener" target="_blank">Pinterest</a></c:if>
            </div>
        </div>
        <div class="footer-bottom"><span>&copy; <c:out value="${siteName}"/>. A portfolio project - products shown are fictional.</span><span>Prices in Indian rupees (&#8377;)</span></div>
    </div>
</footer>

