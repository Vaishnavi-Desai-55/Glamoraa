<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Contact" description="Get in touch with Glamoraa.">
<div class="container page-head"><h1>Contact us</h1><p>Questions about an order, sizing or anything else? Send us a message.</p></div>
<div class="container section-tight contact-layout">
    <div class="prose">
        <h2>How we can help</h2>
        <p>Include your order number if your question is about an order and we will get back to you as soon as we can.</p>
        <c:if test="${not empty contactEmail}"><p>You can also email <a href="mailto:<c:out value='${contactEmail}'/>"><c:out value="${contactEmail}"/></a>.</p></c:if>
        <p class="muted">Messages are saved securely in our database. This demonstration store does not send email.</p>
    </div>
    <form class="card" method="post" action="${ctx}/contact" data-once novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <c:if test="${not empty errors}"><div class="alert alert-error" role="alert">Please correct the highlighted fields.</div></c:if>
        <div class="form-grid">
            <g:field name="name" label="Your name" value="${form.name[0]}" errors="${errors}" required="true" autocomplete="name" maxlength="120"/>
            <g:field name="email" label="Email" type="email" value="${form.email[0]}" errors="${errors}" required="true" autocomplete="email" maxlength="190"/>
            <g:field name="subject" label="Subject" value="${form.subject[0]}" errors="${errors}" required="true" maxlength="160" cssClass="full"/>
            <g:field name="message" label="Message" type="textarea" value="${form.message[0]}" errors="${errors}" required="true" maxlength="3000" cssClass="full"/>
        </div>
        <button class="btn btn-primary" type="submit">Send message</button>
    </form>
</div>
</g:layout>

