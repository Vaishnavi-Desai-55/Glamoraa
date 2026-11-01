<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:layout title="Profile">
<div class="container section-tight"><div class="account-layout">
    <g:accountNav active="profile"/>
    <div>
        <h1>Profile</h1>
        <div class="card">
            <h2>Your details</h2>
            <form method="post" action="${ctx}/account/profile" data-once novalidate>
                <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="action" value="profile">
                <c:set var="pe" value="${formAction == 'profile' ? errors : null}"/>
                <div class="form-grid">
                    <g:field name="fullName" label="Full name" value="${formAction == 'profile' ? fullNameInput : user.fullName}" errors="${pe}" required="true" autocomplete="name" maxlength="120"/>
                    <g:field name="phone" label="Phone" type="tel" value="${formAction == 'profile' ? phoneInput : user.phone}" errors="${pe}" autocomplete="tel" maxlength="20"/>
                    <div class="field full"><label for="f-email">Email</label><input class="input" id="f-email" type="email" value="<c:out value='${user.email}'/>" disabled><span class="hint">Your email is your login and cannot be changed here.</span></div>
                </div>
                <button class="btn btn-primary" type="submit">Save changes</button>
            </form>
        </div>
        <div class="card">
            <h2>Change password</h2>
            <form method="post" action="${ctx}/account/profile" data-once novalidate>
                <input type="hidden" name="_csrf" value="${csrfToken}"><input type="hidden" name="action" value="password">
                <c:set var="we" value="${formAction == 'password' ? errors : null}"/>
                <g:field name="currentPassword" label="Current password" type="password" errors="${we}" required="true" autocomplete="current-password" maxlength="100"/>
                <div class="form-grid">
                    <g:field name="newPassword" label="New password" type="password" errors="${we}" required="true" autocomplete="new-password" hint="At least 8 characters, with a letter and a number." maxlength="100"/>
                    <g:field name="confirmPassword" label="Confirm new password" type="password" errors="${we}" required="true" autocomplete="new-password" maxlength="100"/>
                </div>
                <button class="btn btn-outline" type="submit">Update password</button>
            </form>
        </div>
    </div>
</div></div>
</g:layout>

