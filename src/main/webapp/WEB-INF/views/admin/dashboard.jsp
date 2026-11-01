<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="gf" uri="urn:glamoraa:fn" %>
<%@ taglib prefix="g" tagdir="/WEB-INF/tags" %>
<g:adminLayout title="Dashboard" active="dashboard">
<div class="admin-head"><h1>Dashboard</h1><a class="btn btn-primary btn-sm" href="${ctx}/admin/products/new">Add product</a></div>
<div class="stat-grid">
    <div class="stat"><div class="n">${gf:money(dash.revenue)}</div><div class="l">Order value (excluding cancelled)</div></div>
    <div class="stat"><div class="n">${dash.totalOrders}</div><div class="l">Orders</div></div>
    <div class="stat"><div class="n">${dash.customers}</div><div class="l">Customers</div></div>
    <div class="stat"><div class="n">${dash.products}</div><div class="l">Products</div></div>
</div>
<div class="two-col">
    <div class="card">
        <h2>Orders by status</h2>
        <c:forEach var="e" items="${dash.ordersByStatus}">
            <div class="bar-row"><span><span class="status ${e.key}">${fn:toLowerCase(e.key)}</span></span><div class="bar"><i style="width:${dash.totalOrders == 0 ? 0 : e.value * 100 / dash.totalOrders}%"></i></div><span>${e.value}</span></div>
        </c:forEach>
        <c:if test="${dash.totalOrders == 0}"><p class="muted">No orders yet. Place a test order from the storefront.</p></c:if>
    </div>
    <div class="card">
        <h2>Top products</h2>
        <c:choose>
            <c:when test="${empty dash.topProducts}"><p class="muted">Sales appear here once orders are placed.</p></c:when>
            <c:otherwise><table class="data" style="min-width:0"><thead><tr><th>Product</th><th class="num">Units</th><th class="num">Value</th></tr></thead><tbody>
                <c:forEach var="t" items="${dash.topProducts}"><tr><td><c:out value="${t[0]}"/></td><td class="num">${t[1]}</td><td class="num">${gf:money(t[2])}</td></tr></c:forEach>
            </tbody></table></c:otherwise>
        </c:choose>
    </div>
</div>
<div class="two-col" style="margin-top:20px">
    <div class="card">
        <h2>Low stock</h2>
        <c:choose>
            <c:when test="${empty dash.lowStock}"><p class="muted">Every active variant is comfortably stocked.</p></c:when>
            <c:otherwise><table class="data" style="min-width:0"><thead><tr><th>Variant</th><th class="num">Stock</th><th></th></tr></thead><tbody>
                <c:forEach var="v" items="${dash.lowStock}"><tr><td><c:out value="${v.productName}"/><div class="muted" style="font-size:.8rem"><c:out value="${v.sizeLabel}"/> / <c:out value="${v.colorName}"/></div></td><td class="num"><span class="status ${v.stockQuantity == 0 ? 'CANCELLED' : 'PLACED'}">${v.stockQuantity}</span></td><td class="num"><a class="btn-text" href="${ctx}/admin/variants?productId=${v.productId}">Restock</a></td></tr></c:forEach>
            </tbody></table></c:otherwise>
        </c:choose>
    </div>
    <div class="card">
        <h2>Recent orders</h2>
        <c:choose>
            <c:when test="${empty dash.recentOrders}"><p class="muted">No orders yet.</p></c:when>
            <c:otherwise><table class="data" style="min-width:0"><tbody>
                <c:forEach var="o" items="${dash.recentOrders}"><tr><td><a href="${ctx}/admin/orders/view?id=${o.id}"><strong>${o.orderNumber}</strong></a><div class="muted" style="font-size:.8rem"><c:out value="${o.customerName}"/></div></td><td><span class="status ${o.status}">${fn:toLowerCase(o.status)}</span></td><td class="num">${gf:money(o.total)}</td></tr></c:forEach>
            </tbody></table><p style="margin:12px 0 0"><a class="btn-text" href="${ctx}/admin/orders">All orders</a></p></c:otherwise>
        </c:choose>
    </div>
</div>
</g:adminLayout>

