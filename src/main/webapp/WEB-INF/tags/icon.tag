<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="name" required="true" %>
<svg class="icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false"><c:choose>
<c:when test="${name == 'bag'}"><path d="M6 8h12l1 12H5L6 8z"/><path d="M9 8V6a3 3 0 0 1 6 0v2"/></c:when>
<c:when test="${name == 'heart'}"><path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.5A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"/></c:when>
<c:when test="${name == 'search'}"><circle cx="11" cy="11" r="6.5"/><path d="M16 16l4 4"/></c:when>
<c:when test="${name == 'user'}"><circle cx="12" cy="8" r="4"/><path d="M4 20c1-4 4-6 8-6s7 2 8 6"/></c:when>
<c:when test="${name == 'menu'}"><path d="M4 7h16M4 12h16M4 17h16"/></c:when>
<c:when test="${name == 'close'}"><path d="M6 6l12 12M18 6L6 18"/></c:when>
<c:when test="${name == 'arrow-right'}"><path d="M5 12h14M13 6l6 6-6 6"/></c:when>
<c:when test="${name == 'arrow-left'}"><path d="M19 12H5M11 6l-6 6 6 6"/></c:when>
<c:when test="${name == 'chevron-down'}"><path d="M6 9l6 6 6-6"/></c:when>
<c:when test="${name == 'trash'}"><path d="M5 7h14M10 7V5h4v2M7 7l1 13h8l1-13"/></c:when>
<c:when test="${name == 'plus'}"><path d="M12 5v14M5 12h14"/></c:when>
<c:when test="${name == 'minus'}"><path d="M5 12h14"/></c:when>
<c:when test="${name == 'check'}"><path d="M5 12.5l4.5 4.5L19 7"/></c:when>
<c:when test="${name == 'truck'}"><path d="M3 6h11v10H3zM14 10h4l3 3v3h-7"/><circle cx="7" cy="17.5" r="1.8"/><circle cx="17" cy="17.5" r="1.8"/></c:when>
<c:when test="${name == 'return'}"><path d="M4 12a8 8 0 1 0 3-6.2"/><path d="M4 4v4h4"/></c:when>
<c:when test="${name == 'lock'}"><rect x="5" y="11" width="14" height="9" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/></c:when>
<c:when test="${name == 'star'}"><path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/></c:when>
</c:choose></svg>
