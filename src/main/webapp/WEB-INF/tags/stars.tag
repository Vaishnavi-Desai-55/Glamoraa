<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<%@ attribute name="value" required="true" type="java.lang.Double" %>
<span class="stars" role="img" aria-label="Rated ${value} out of 5"><c:forEach var="i" begin="1" end="5"><span class="${i <= value + 0.25 ? '' : 'off'}"><g:icon name="star"/></span></c:forEach></span>
