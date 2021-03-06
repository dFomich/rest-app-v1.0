package com.epam.cleaningProject.command.client;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.cleaningProject.command.Command;
import com.epam.cleaningProject.command.ConstantName;
import com.epam.cleaningProject.command.RequestContent;
import com.epam.cleaningProject.command.RouteType;
import com.epam.cleaningProject.command.Router;
import com.epam.cleaningProject.entity.Client;
import com.epam.cleaningProject.entity.User;
import com.epam.cleaningProject.service.ServiceException;
import com.epam.cleaningProject.service.serviceImpl.ClientServiceImpl;
import com.epam.cleaningProject.util.ConfigurationManager;
import com.epam.cleaningProject.util.MessageManager;
import com.epam.cleaningProject.validator.DataValidator;

public class EditClientProfileCommand implements Command {
    private final static Logger logger = LogManager.getLogger();

    /**
     * Gets first name, last name, address, telephone number.
     * Validates this values, if input data is not valid,
     * returns router to the same page with message about invalid values.
     * Otherwise, edits client and returns router to the same page.
     * Not allowed to change client id.
     *
     * @param content an {@link RequestContent} object that
     *                contains the request the client has made
     *                of the servlet
     * @return a {@code Router} object
     * @see DataValidator#validateUserUpdateDate(Map)
     * @see ClientServiceImpl#updateClient(Client)
     */
    @Override
    public Router execute(RequestContent content) {
        DataValidator validator = new DataValidator();
        Map<String, String> userParameters = new HashMap<>();
        Router router = new Router();
        ClientServiceImpl clientService = new ClientServiceImpl();
        User user = (User) content.getSessionAttribute(ConstantName.ATTRIBUTE_USER);
        Long userId = user.getUserId();
        String firstName = content.getRequestParameter(ConstantName.PARAMETER_FIRST_NAME);
        String lastName = content.getRequestParameter(ConstantName.PARAMETER_LAST_NAME);
        String address = content.getRequestParameter(ConstantName.PARAMETER_ADDRESS);
        String telephoneNumber = content.getRequestParameter(ConstantName.PARAMETER_TELEPHONE_NUMBER);
        userParameters.put(ConstantName.PARAMETER_FIRST_NAME, firstName);
        userParameters.put(ConstantName.PARAMETER_LAST_NAME, lastName);
        userParameters.put(ConstantName.PARAMETER_ADDRESS, address);
        userParameters.put(ConstantName.PARAMETER_TELEPHONE_NUMBER, telephoneNumber);
        Client client = new Client(userId, firstName, lastName, address, telephoneNumber);
        try {
            validator.validateUserUpdateDate(userParameters);
            if (!userParameters.containsValue(ConstantName.ATTRIBUTE_EMPTY_VALUE)) {
                if (clientService.updateClient(client)) {
                    content.addSessionAttribute(ConstantName.ATTRIBUTE_USER_PROFILE, client);
                    router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_CLIENT_PROFILE));
                    router.setType(RouteType.REDIRECT);
                } else {
                    content.addRequestAttribute(ConstantName.ATTRIBUTE_EDIT_PROFILE_ERROR,
                            MessageManager.getProperty(ConstantName.MESSAGE_REGISTRATION_ERROR));
                    router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_CLIENT_PROFILE));
                }
            } else {
                content.addSessionAttribute(ConstantName.ATTRIBUTE_USER_PROFILE, client);
                content.addSessionAttribute(ConstantName.ATTRIBUTE_VALIDATED_MAP, userParameters);
                content.addRequestAttribute(ConstantName.ATTRIBUTE_EDIT_PROFILE_ERROR,
                        MessageManager.getProperty(ConstantName.MESSAGE_INCORRECT_INPUT_DATA));
                router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_CLIENT_PROFILE));
             }
        } catch (ServiceException e) {
            logger.error("Error while editing profile command", e);
            router.setPagePath(ConfigurationManager.getProperty(ConstantName.JSP_ERROR));
        }
        return router;
    }
}

