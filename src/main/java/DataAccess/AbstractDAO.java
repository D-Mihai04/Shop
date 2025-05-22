package DataAccess;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;

import Connection.ConnectionFactory;

/**
 * AbstractDAO implements all the operations to access the database and get the details form it
 *
 * @param <T> Type of object
 */
public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;


    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Builds a SELECT query
     *
     * @param field the field to query by
     * @return the SELECT query string
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        String tableName = type.getSimpleName();
        if (tableName.equals("Order")) {
            tableName = "`Order`";
        }
        sb.append(tableName);
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * FInds all from table
     *
     * @return a list of objects of type T
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String tableName = type.getSimpleName();
        if (tableName.equals("Order")) {
            tableName = "`Order`";
        }
        String query = "SELECT * FROM " + tableName;

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return new ArrayList<>();
    }

    /**
     * Finds object by ID
     *
     * @param id the ID of the object
     * @return the object with that ID
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Deletes an object by ID
     *
     * @param id the ID of the object
     */
    public void delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;

        String tableName = type.getSimpleName();
        if (tableName.equals("Order")) {
            tableName = "`Order`";
        }
        String query = "DELETE FROM " + tableName + " WHERE id = ?";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Converts a ResultSet into a list of objects of type T
     *
     * @param resultSet what to convert
     * @return the list of objects
     */
    List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Inserts an object into the database and sets the generated ID
     *
     * @param t the object to insert
     * @return the inserted object with updated ID
     */
    public T insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String tableName = type.getSimpleName();
        if (tableName.equals("Order")) {
            tableName = "`Order`";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");

        Field[] fields = type.getDeclaredFields();
        List<Object> values = new ArrayList<>();

        try {
            boolean first = true;
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase("id")) continue;
                if (!first) sb.append(", ");
                sb.append(field.getName());
                first = false;

                field.setAccessible(true);
                values.add(field.get(t));
            }

            sb.append(") VALUES (");
            sb.append("?,".repeat(values.size()));
            sb.setLength(sb.length() - 1);
            sb.append(")");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }

            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                int generatedId = resultSet.getInt(1);
                Field idField = type.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(t, generatedId);
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return t;
    }

    /**
     * Updates object
     * @param t the object to be updated
     * @return the updated object
     */
    public T update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;

        String tableName = type.getSimpleName();
        if (tableName.equals("Order")) {
            tableName = "`Order`";
        }

        StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(tableName).append(" SET ");
        List<Object> values = new ArrayList<>();
        Object idValue = null;

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (field.getName().equalsIgnoreCase("id")) {
                    idValue = value;
                } else {
                    query.append(field.getName()).append(" = ?, ");
                    values.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE id = ?");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query.toString());

            int i = 0;
            for (; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            statement.setObject(i + 1, idValue);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }
}
