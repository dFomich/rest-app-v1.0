package com.epam.cleaningProject.dao.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epam.cleaningProject.dao.AbstractDao;
import com.epam.cleaningProject.dao.DaoException;
import com.epam.cleaningProject.dao.OrderDao;
import com.epam.cleaningProject.dao.TableColumn;
import com.epam.cleaningProject.entity.Order;
import com.epam.cleaningProject.entity.OrderStatus;
import com.epam.cleaningProject.entity.PaymentType;

/**
 * The {@code OrderDaoImpl} class
 * <p>
 * provides access to the tables 'orders', 'client_orders',
 * 'cleaning_in_order', 'cleaner_orders' in the database
 */
public class OrderDaoImpl extends AbstractDao<Long, Order> implements OrderDao {
    private final static Logger logger = LogManager.getLogger();
    private final static String SQL_SELECT_ALL_ORDERS = "SELECT order_id, order_sum, order_date_incoming," +
            " order_date_execute, order_status, order_payment_type, order_payment_fulfilled, order_comment FROM orders";
    private final static String SQL_SELECT_ORDER_BY_ID = "SELECT order_id, order_sum, order_date_incoming, " +
            "order_date_execute, order_status, order_payment_type, order_payment_fulfilled, order_comment " +
            " FROM orders WHERE order_id= ? ";
    private final static String SQL_CREATE_ORDER = "INSERT INTO orders (order_sum, order_date_incoming, " +
            "order_date_execute, order_status, order_payment_type, order_payment_fulfilled, order_comment)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String SQL_UPDATE_ORDER = "UPDATE orders SET order_sum= ?, order_date_execute= ?, " +
            " order_status= ?, order_payment_type= ?, order_payment_fulfilled= ?, order_comment= ? " +
            " WHERE order_id= ?";
    private final static String SQL_UPDATE_ORDER_STATUS = "UPDATE orders SET order_status= ? " +
            " WHERE order_id= ?";
    private final static String SQL_UPDATE_PAYMENT = "UPDATE orders SET order_payment_fulfilled = ? " +
            " WHERE order_id= ?";
    private final static String SQL_FIND_ALL_ORDERS_BY_CLIENT_ID = "SELECT orders.order_id, orders.order_sum," +
            " orders.order_date_incoming, orders.order_date_execute, orders.order_status, orders.order_payment_type, " +
            "orders.order_payment_fulfilled, orders.order_comment  FROM orders, client_orders WHERE" +
            " client_orders.order_id =  orders.order_id AND client_orders.client_id = ?";
    private final static String SQL_FIND_ALL_ORDERS_BY_CLEANER_ID = "SELECT orders.order_id, orders.order_sum, " +
            "orders.order_date_incoming, orders.order_date_execute, orders.order_status, orders.order_payment_type, " +
            "orders.order_payment_fulfilled, orders.order_comment  FROM orders, cleaner_orders WHERE " +
            "cleaner_orders.cleaner_order_id = orders.order_id AND cleaner_orders.cleaner_id = ?";
    private final static String SQL_LINK_ORDER_TO_CLIENT = "INSERT INTO client_orders (client_id, order_id," +
            " quantity_in_order) VALUES (?, ?, ?);";
    private final static String SQL_LINK_ORDER_TO_CLEANER = "INSERT INTO cleaner_orders (cleaner_id, " +
            "cleaner_order_id)  VALUES (?, ?)";
    private final static String SQL_LINK_ORDER_TO_CLEANING = " INSERT INTO cleaning_in_order " +
            "( cleaning_in_order.orders_id, cleaning_in_order.cleaning_id, cleaning_in_order.items_in_order)" +
            " VALUES (?, ?, ?)";

    @Override
    public List<Order> findAll() throws DaoException {
        List<Order> ordersList = new ArrayList<>();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_ORDERS);
            while (resultSet.next()) {
                Order order = extractOrder(resultSet);
                ordersList.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return ordersList;
    }

    @Override
    public Optional<Order> findById(Long id) throws DaoException {
        Order order = new Order();
        PreparedStatement statement = null;
        Optional<Order> orderOptional;
        try {
            statement = connection.prepareStatement(SQL_SELECT_ORDER_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                order = extractOrder(resultSet);
            }
            orderOptional = Optional.of(order);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return orderOptional;
    }

    @Override
    public boolean create(Order order) throws DaoException {
        boolean created = false;
        PreparedStatement statement = null;
        ResultSet generatedKeys;
        try {
            statement = connection.prepareStatement(SQL_CREATE_ORDER, Statement.RETURN_GENERATED_KEYS);
            statement.setBigDecimal(1, order.getOrderSum());
            statement.setTimestamp(2, Timestamp.valueOf(order.getIncomingDate()));
            statement.setTimestamp(3, Timestamp.valueOf(order.getExecuteDate()));
            statement.setString(4, order.getOrderStatus().toString().toLowerCase());
            statement.setString(5, order.getPaymentType().toString().toLowerCase());
            statement.setBoolean(6, order.isPaymentFulfilled());
            statement.setString(7, order.getComment());
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setId(generatedKeys.getInt(1));
                created = true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return created;
    }

    @Override
    public boolean update(Order order) throws DaoException {
        PreparedStatement statement = null;
        logger.log(Level.DEBUG, order);
        boolean updated;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_ORDER);
            statement.setBigDecimal(1, order.getOrderSum());
            statement.setDate(2, new Date(Timestamp.valueOf(order.getExecuteDate()).getTime()));
            statement.setString(3, order.getOrderStatus().toString().toLowerCase());
            statement.setString(4, order.getPaymentType().toString().toLowerCase());
            statement.setBoolean(5, order.isPaymentFulfilled());
            statement.setString(6, order.getComment());
            statement.setLong(7, order.getId());
            updated = statement.executeUpdate() != 0;
            logger.log(Level.DEBUG, updated);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return updated;
    }

    @Override
    public boolean updatePaymentStatus(long orderId) throws DaoException {
        PreparedStatement statement = null;
        boolean updated;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_PAYMENT);
            statement.setBoolean(1, Boolean.TRUE);
            statement.setLong(2, orderId);
            updated = statement.executeUpdate() != 0;
            logger.log(Level.DEBUG, updated);
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return updated;
    }

    @Override
    public boolean updateOrderStatus(long orderId, OrderStatus status) throws DaoException {
        PreparedStatement statement = null;
        boolean updated;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_ORDER_STATUS);
            statement.setString(1, status.toString().toLowerCase());
            statement.setLong(2, orderId);
            updated = statement.executeUpdate() != 0;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return updated;
    }

    @Override
    public boolean linkOrderClient(long clientId, long orderId, int cleaningCountTotal) throws DaoException {
        boolean created = false;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_LINK_ORDER_TO_CLIENT, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, clientId);
            statement.setLong(2, orderId);
            statement.setInt(3, cleaningCountTotal);
            int updatedRows = statement.executeUpdate();
            if (updatedRows != 0) {
                created = true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return created;
    }

    @Override
    public boolean linkOrderCleaner(long cleanerId, long orderId) throws DaoException {
        boolean created = false;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_LINK_ORDER_TO_CLEANER, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, cleanerId);
            statement.setLong(2, orderId);
            int updatedRows = statement.executeUpdate();
            if (updatedRows != 0) {
                created = true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return created;
    }

    @Override
    public boolean linkOrderCleaning(long cleaningId, long orderId, int cleaningCount) throws DaoException {
        boolean created = false;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL_LINK_ORDER_TO_CLEANING, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, orderId);
            statement.setLong(2, cleaningId);
            statement.setInt(3, cleaningCount);
            int updatedRows = statement.executeUpdate();
            if (updatedRows != 0) {
                created = true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return created;
    }

    @Override
    public List<Order> findAllOrderByClientId(Long clientId) throws DaoException {
        List<Order> ordersList = new ArrayList<>();
        PreparedStatement statement = null;
        StringBuilder builder = new StringBuilder();
        try {
            statement = connection.prepareStatement(SQL_FIND_ALL_ORDERS_BY_CLIENT_ID);
            statement.setLong(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = extractOrder(resultSet);
                ordersList.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return ordersList;
    }

    @Override
    public List<Order> findAllOrderByCleanerId(Long cleanerId) throws DaoException {
        List<Order> ordersList = new ArrayList<>();
        PreparedStatement statement = null;
        StringBuilder builder = new StringBuilder();
        try {
            statement = connection.prepareStatement(SQL_FIND_ALL_ORDERS_BY_CLEANER_ID);
            statement.setLong(1, cleanerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = extractOrder(resultSet);
                ordersList.add(order);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
            throw new DaoException(e);
        } finally {
            close(statement);
        }
        return ordersList;
    }

    /**
     * Creates a new {@code Order} object and
     * sets its values using {@code ResultSet}
     *
     * @param resultSet a {@code ResultSet} to build an object
     * @return a {@code Order}
     */
    private Order extractOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getLong(TableColumn.ORDER_ID));
        order.setOrderSum(resultSet.getBigDecimal(TableColumn.ORDER_SUM));
        order.setIncomingDate(resultSet.getTimestamp(TableColumn.ORDER_DATE_INCOMING).toLocalDateTime());
        order.setExecuteDate(resultSet.getTimestamp(TableColumn.ORDER_DATE_EXECUTE).toLocalDateTime());
        order.setOrderStatus(OrderStatus.valueOf(resultSet.getString(TableColumn.ORDER_STATUS).toUpperCase()));
        order.setPaymentType(PaymentType.valueOf(resultSet.getString(TableColumn.ORDER_PAYMENT_TYPE).toUpperCase()));
        order.setPaymentFulfilled(resultSet.getBoolean(TableColumn.ORDER_PAYMENT_FULFILLED));
        order.setComment(resultSet.getString(TableColumn.ORDER_COMMENT).toString());
        return order;
    }

//    public List<ItemInCart> findCleaningsInOrder(Long cleaningId) throws DaoException {
//        List<ItemInCart> itemList = new ArrayList<>();
//
//        PreparedStatement statement = null;
//        try {
//            statement = connection.prepareStatement(SQL_FIND_CLEANINGS_IN_ORDER_BY_ID);
//            statement.setLong(1, cleaningId);
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                Cleaning cleaning = new Cleaning();
//                cleaning.setId(resultSet.getLong(TableColumn.CLEANING_ID));
//                cleaning.setName(resultSet.getString(TableColumn.CLEANING_NAME));
//                cleaning.setPrice(resultSet.getBigDecimal(TableColumn.CLEANING_PRICE_PER_UNIT));
//                cleaning.setCleaningType(CleaningType.valueOf(resultSet.getString(TableColumn.CLEANING_TYPE).toUpperCase()));
//                cleaning.setDescription(resultSet.getString(TableColumn.CLEANING_DESCRIPTION));
//                cleaning.setQuantity(resultSet.getInt(TableColumn.CLEANING_QUANTITY));
//                cleaning.setAvailable(resultSet.getBoolean(TableColumn.CLEANING_IS_AVAILABLE));
//                cleaning.setCleanerId(resultSet.getLong(TableColumn.CLEANING_CLEANER_ID));
//              itemList.add(new ItemInCart(cleaning, resultSet.getInt(TableColumn.CLEANING_IN_ORDER_ORDER_ITEM)));
//            }
//
//        } catch (SQLException e) {
//            logger.log(Level.ERROR, "SQL exception (request or table failed): ", e);
//            throw new DaoException(e);
//        } finally {
//            close(statement);
//        }
//        return itemList;
//
//    }
}