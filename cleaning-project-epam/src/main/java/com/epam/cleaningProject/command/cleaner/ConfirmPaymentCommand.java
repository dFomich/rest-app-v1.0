package com.epam.cleaningProject.command.cleaner;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.cleaningProject.command.Command;
import com.epam.cleaningProject.command.ConstantName;
import com.epam.cleaningProject.command.RequestContent;
import com.epam.cleaningProject.command.Router;
import com.epam.cleaningProject.entity.Order;
import com.epam.cleaningProject.entity.User;
import com.epam.cleaningProject.service.ServiceException;
import com.epam.cleaningProject.service.serviceImpl.OrderServiceImpl;
import com.epam.cleaningProject.util.ConfigurationManager;
import com.epam.cleaningProject.util.MessageManager;

public class ConfirmPaymentCommand implements Command {
    private final static Logger logger = LogManager.getLogger();
    /**
     * Gets payment status which cleaner wants to change from the request.
     * Edits order value (updates database) and returns router to the same page.
     *
     * @param content an {@link RequestContent} object that
     *                contains the request the client has made
     *                of the servlet
     * @return a {@code Router} object
     * @see OrderServiceImpl#changePaymentStatus(long)
     */
    @Override
    public Router execute(RequestContent content) {
        String start = content.getRequestParameter(ConstantName.PARAMETER_PAGE_START);
        OrderServiceImpl orderService = new OrderServiceImpl();
        Router router = new Router();
        User user = (User) content.getSessionAttribute(ConstantName.ATTRIBUTE_USER);
        Long oderId = Long.valueOf(content.getRequestParameter(ConstantName.PARAMETER_ORDER_ID));
        try {
            if (orderService.changePaymentStatus(oderId)) {
                List<Order> orderList = orderService.findAllCleanerOrders(user.getUserId());
                content.addSessionAttribute(ConstantName.ATTRIBUTE_START, start);
                content.addSessionAttribute(ConstantName.ATTRIBUTE_ORDERS_LIST, orderList);
                router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_SHOW_CLEANER_ORDERS));
            } else {
                content.addSessionAttribute(ConstantName.ATTRIBUTE_START, start);
                content.addRequestAttribute(ConstantName.ATTRIBUTE_ORDER_CANCEL_ERROR,
                        MessageManager.getProperty(ConstantName.MESSAGE_CLEANING_STATUS_ERROR));
                router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_SHOW_CLEANER_ORDERS));
            }
        } catch (ServiceException e) {
            logger.log(Level.ERROR, "Error while changing payment status", e);
            router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_ERROR));
        }
        return router;
    }
}
