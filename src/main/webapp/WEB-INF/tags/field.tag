<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" %>
<%@ attribute name="value" %>
<%@ attribute name="errors" type="java.util.Map" %>
<%@ attribute name="required" type="java.lang.Boolean" %>
<%@ attribute name="autocomplete" %>
<%@ attribute name="hint" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="rows" %>
<%@ attribute name="inputmode" %>
<%@ attribute name="min" %>
<%@ attribute name="max" %>
<%@ attribute name="step" %>
<%@ attribute name="cssClass" %>
<div class="field ${not empty errors[name] ? 'has-error' : ''} ${cssClass}">
    <label for="f-${name}">${label}<c:if test="${required}"> <span aria-hidden="true">*</span></c:if></label>
    <c:choose>
        <c:when test="${type == 'textarea'}">
            <textarea class="textarea" id="f-${name}" name="${name}" rows="${empty rows ? 5 : rows}" <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if> <c:if test="${required}">required</c:if> <c:if test="${not empty errors[name]}">aria-invalid="true" aria-describedby="e-${name}"</c:if>><c:out value="${value}"/></textarea>
        </c:when>
        <c:otherwise>
            <input class="input" id="f-${name}" name="${name}" type="${empty type ? 'text' : type}" value="<c:out value='${value}'/>"
                   <c:if test="${not empty autocomplete}">autocomplete="${autocomplete}"</c:if> <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if>
                   <c:if test="${not empty inputmode}">inputmode="${inputmode}"</c:if> <c:if test="${not empty min}">min="${min}"</c:if>
                   <c:if test="${not empty max}">max="${max}"</c:if> <c:if test="${not empty step}">step="${step}"</c:if>
                   <c:if test="${required}">required</c:if> <c:if test="${not empty errors[name]}">aria-invalid="true" aria-describedby="e-${name}"</c:if>>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty hint}"><span class="hint">${hint}</span></c:if>
    <c:if test="${not empty errors[name]}"><span class="field-error" id="e-${name}" role="alert"><c:out value="${errors[name]}"/></span></c:if>
</div>
