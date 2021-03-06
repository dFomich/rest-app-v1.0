
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ctg" uri="customtags" %>
<%--<c:set var="language"--%>
<%--       value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}"--%>
<%--       scope="session"/>--%>
<fmt:setLocale value="${language}" scope="session"/>
<fmt:setBundle basename="messages"/>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <title>Title</title>
</head>
<body>
<c:set var="pagePath" value="/jsp/contact.jsp" scope="session"/>
<%@ include file="/jsp/static/header.jsp"%>
SUPPORT
<%--<div class="container">--%>
<%--    <div class="row">--%>
<%--        <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 text-center">--%>
<%--            <h2>Contact</h2>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--    <div class="row">--%>
<%--        <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 col-xs-offset-3">--%>
<%--            <form id="contact-form" class="form" action="#" method="POST" role="form">--%>
<%--                <div class="form-group">--%>
<%--                    <label class="form-label" for="name">Your Name</label>--%>
<%--                    <input type="text" class="form-control" id="name" name="name" placeholder="Your name" tabindex="1"--%>
<%--                           required>--%>
<%--                </div>--%>
<%--                <div class="form-group">--%>
<%--                    <label class="form-label" for="email">Your Email</label>--%>
<%--                    <input type="email" class="form-control" id="email" name="email" placeholder="Your Email"--%>
<%--                           tabindex="2" required>--%>
<%--                </div>--%>
<%--                <div class="form-group">--%>
<%--                    <label class="form-label" for="subject">Subject</label>--%>
<%--                    <input type="text" class="form-control" id="subject" name="subject" placeholder="Subject"--%>
<%--                           tabindex="3">--%>
<%--                </div>--%>
<%--                <div class="form-group">--%>
<%--                    <label class="form-label" for="message">Message</label>--%>
<%--                    <textarea rows="5" cols="50" name="message" class="form-control" id="message"--%>
<%--                              placeholder="Message..." tabindex="4" required></textarea>--%>
<%--                </div>--%>
<%--                <div class="text-center">--%>
<%--                    <button type="submit" class="btn btn-start-order">Send Message</button>--%>
<%--                </div>--%>
<%--            </form>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</div>--%>

    <%@ include file="/jsp/static/footer.jsp" %>

</body>
</html>
