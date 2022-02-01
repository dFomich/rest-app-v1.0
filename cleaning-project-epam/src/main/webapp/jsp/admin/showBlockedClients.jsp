
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ctg" uri="customtags" %>
<fmt:setLocale value="${language}" scope="session"/>
<fmt:setBundle basename="messages"/>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <title><fmt:message key="clientBlockedList.title"/></title>
</head>
<body>
<%--<c:set var="pagePath" scope="session" value="/jsp/admin/showBlockedClients.jsp"/>--%>
<c:set var="pagePath" scope="session" value="/controller?command=show_blocked_clients"/>

<%@ include file="/jsp/static/header.jsp" %>


<c:set var="client" scope="session" value="${clientBlockedList}"/>
<c:set var="totalCount" scope="session" value="${clientBlockedList.size()}"/>
<c:out value="${totalcount}"> </c:out>
<c:set var="perPage" scope="session" value="${5}"/>
<c:set var="pageStart" value="${param.start}"/>
<c:if test="${empty pageStart or pageStart < 0}">
    <c:set var="pageStart" value="0"/>
</c:if>
<c:if test="${totalCount < pageStart}">
    <c:set var="pageStart" value="${pageStart - perPage}"/>
</c:if>
<c:choose>
    <c:when test="${not empty clientBlockedList}">
        <table class="table table-striped">
            <thead>
            <tr>
                <td><b>Id</b></td>
                <td><b><fmt:message key="registration.login"/></b></td>
                <td><b><fmt:message key="registration.firstName"/></b></td>
                <td><b><fmt:message key="registration.lastName"/></b></td>
                <td><b><fmt:message key="registration.address"/></b></td>
                <td><b><fmt:message key="registration.telephoneNumber"/></b></td>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="client" items="${clientBlockedList}"
                       begin="${pageStart}" end="${pageStart + perPage - 1}">
                <c:set var="classSuccess" value=""/>
                <tr class="${classSuccess}">
                    <td>${fn:escapeXml(client.id)}</td>
                    <td>${fn:escapeXml(client.login)}</td>
                    <td>${fn:escapeXml(client.firstName)}</td>
                    <td>${fn:escapeXml(client.lastName)}</td>
                    <td>${fn:escapeXml(client.address)}</td>
                    <td>${fn:escapeXml(client.telephoneNumber)}</td>
                    <td align="center">
                        <form method="POST" action="${pageContext.request.contextPath}/controller">
                        <input type="hidden" name="command" value="change_user_status"/>
                        <input type="hidden" name="id" value="${client.id}"/>
                        <input type="hidden" name="isActive" value="true"/>
                        <input type="hidden" name="start" value="${param.start}"/>
                        <input type="submit" class="btn btn-dark"
                               value="<fmt:message key="title.unblockUser"/>"/>
                    </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <br>
        <div class="alert alert-info">
            No people found matching your search criteria
        </div>
    </c:otherwise>
</c:choose>


<strong><p class="text-danger"> ${blockUserError} </p></strong>
<strong><p class="text-success"> ${blockUser} </p></strong>

<%--<a href="controller?command=show_blocked_clients&start=${pageStart - perPage}"--%>
<%--   class="btn btn-dark">Previous</a>--%>
<%--<a href="controller?command=show_blocked_clients&start=${pageStart + perPage}"--%>
<%--   class="btn btn-dark">Next</a>--%>

<a href="${pageContext.request.contextPath}/controller?command=show_blocked_clients&start=${pageStart - perPage}"><fmt:message key="title.previous"/></a>
${pageStart + 1} - ${pageStart + perPage}
<a href="${pageContext.request.contextPath}/controller?command=show_blocked_clients&start=${pageStart + perPage}"><fmt:message key="title.next"/></a>
<br>
<div>
    <a href="${pageContext.request.contextPath}/controller?command=go_to_cabinet" class="btn btn-dark"> <fmt:message
            key="back.toCabinet"/></a>
</div>
<br>
<br>
<br>
<br>
<br>
<br>
<%@ include file="/jsp/static/footer.jsp" %>
</body>
</html>
