<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<%@ attribute name="pg" required="true" type="com.glamoraa.util.Page" %>
<%@ attribute name="base" required="true" %>
<c:if test="${pg.totalPages > 1}">
<nav class="pagination" aria-label="Pagination">
    <c:choose>
        <c:when test="${pg.page > 1}"><a href="${base}page=${pg.page - 1}" rel="prev" aria-label="Previous page"><g:icon name="arrow-left"/></a></c:when>
        <c:otherwise><span class="disabled" aria-hidden="true"><g:icon name="arrow-left"/></span></c:otherwise>
    </c:choose>
    <c:forEach var="i" begin="1" end="${pg.totalPages}">
        <c:choose>
            <c:when test="${i == pg.page}"><span class="current" aria-current="page">${i}</span></c:when>
            <c:when test="${i == 1 or i == pg.totalPages or (i >= pg.page - 2 and i <= pg.page + 2)}"><a href="${base}page=${i}" aria-label="Page ${i}">${i}</a></c:when>
            <c:when test="${i == pg.page - 3 or i == pg.page + 3}"><span class="gap">&hellip;</span></c:when>
        </c:choose>
    </c:forEach>
    <c:choose>
        <c:when test="${pg.page < pg.totalPages}"><a href="${base}page=${pg.page + 1}" rel="next" aria-label="Next page"><g:icon name="arrow-right"/></a></c:when>
        <c:otherwise><span class="disabled" aria-hidden="true"><g:icon name="arrow-right"/></span></c:otherwise>
    </c:choose>
</nav>
</c:if>
