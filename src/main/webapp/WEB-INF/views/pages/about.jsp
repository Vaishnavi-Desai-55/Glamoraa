<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Our story" description="The story behind Glamoraa.">
<div class="container-narrow page-head"><h1>Our story</h1><p>Fashion that feels like you, without the noise.</p></div>
<section class="section-tight"><div class="container feature-split">
    <div class="img"><img src="${ctx}/assets/images/about.jpg" alt="Warm arches with a folded linen throw" loading="lazy"></div>
    <div class="copy prose">
        <h2>Discover your signature style.</h2>
        <p>Glamoraa is a premium fashion and lifestyle label built around one belief: the clothes you reach for most should also be the ones you love most.</p>
        <p>We keep our collections small and edited. Every piece is chosen for its fabric, its proportion and how well it pairs with everything else in your wardrobe.</p>
        <p>This storefront is a portfolio project: the products, brand and policies are fictional and exist to demonstrate a complete Java Servlets, JSP, JDBC and MySQL application.</p>
        <p><a class="btn btn-primary" href="${ctx}/products">Shop the collection</a></p>
    </div>
</div></section>
</g:layout>

