<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Privacy policy">
<div class="container-narrow page-head"><h1>Privacy policy</h1><p>Sample text for this demonstration store. Replace with your own policy before going live.</p></div>
<div class="container-narrow prose section-tight">
    <h2>What we collect</h2>
    <p>When you create an account we store your name, email address, optional phone number and a salted hash of your password (never the password itself). When you order, we store your delivery address and the items you bought.</p>
    <h2>How we use it</h2>
    <ul><li>To create and manage your account and orders.</li><li>To deliver your purchases and contact you about them.</li><li>To send our newsletter, only if you subscribe.</li></ul>
    <h2>Cookies</h2>
    <p>We use a single essential session cookie to keep you signed in and to protect forms. We do not use advertising or tracking cookies.</p>
    <h2>Your choices</h2>
    <p>You can edit your details in <a href="${ctx}/account/profile">your profile</a> or <a href="${ctx}/contact">contact us</a> to ask for your data to be removed.</p>
</div>
</g:layout>

