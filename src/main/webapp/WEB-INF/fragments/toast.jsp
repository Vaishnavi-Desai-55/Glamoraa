<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="toast-region" id="toast-region" aria-live="polite" <c:if test="${not empty flash}">data-type="<c:out value='${flash[0]}'/>" data-message="<c:out value='${flash[1]}'/>"</c:if>></div>

