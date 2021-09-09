package services;

import com.mysql.cj.jdbc.Driver;
import entities.BaseEntity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class DbService {

    private static final String CONNECTION = "jdbc:mysql://localhost:3306/names";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final StringBuilder builder = new StringBuilder(250);

    static {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new Error("problemche", e);
        }
    }

    public synchronized static <T> void delete(Class<T> clazz, Long personId) {

        try (Connection con = DriverManager.getConnection(
                CONNECTION, USER, PASSWORD);
             PreparedStatement preparedStatement = con.prepareStatement(String.format("DELETE FROM %s WHERE id = %d "
                     , clazz.getSimpleName(), personId))) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Error("problemche", e);
        }
    }

    public static <T> T load(Class<T> clazz, Long id) {

        T entity;

        try (Connection con = DriverManager.getConnection(
                CONNECTION, USER, PASSWORD);
             PreparedStatement preparedStatement = con.prepareStatement(String.format("SELECT * FROM %s WHERE id = %d ",
                     clazz.getSimpleName().toLowerCase(Locale.ROOT), id));
             ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            entity = clazz.getConstructor().newInstance();
            while (resultSet.next()) {
                for (int i = 1; i <= preparedStatement.getMetaData().getColumnCount(); i++) {
                    builder.setLength(0);
                    builder.append("set");
                    for (String t : preparedStatement.getMetaData().getColumnName(i).split("_")) {
                        builder.append(t.substring(0, 1).toUpperCase(Locale.ROOT));
                        builder.append(t.substring(1));
                    }
                    clazz.getMethod(builder.toString(), Object.class).invoke(entity, resultSet.getObject(i));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("problem", e);
        }
        return entity;
    }


    public synchronized static <T> void createTable(Class<T> entity) {
        builder.setLength(0);
        builder.append(String.format("CREATE TABLE %s ( ", entity.getSimpleName().toLowerCase(Locale.ROOT)));
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(entity.getDeclaredFields()));

        fields.forEach(field -> {
            field.setAccessible(true);
            String name = translateToDbColName(field.getName());
            String type = field.getType().getSimpleName();
            if (name.equalsIgnoreCase("id")) {
                builder.append(" id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ");
            } else {
                builder.append(String.format(" %s %s ,", name, type.equalsIgnoreCase("String") ? "varchar(50)" : "int"));
            }
        });

        builder.delete(builder.length() - 2, builder.length());
        builder.append(")");

        try (Connection con = DriverManager.getConnection(
                CONNECTION, USER, PASSWORD);
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(builder.toString());
        } catch (SQLException e) {
            throw new Error("greshkaa", e);
        }

        System.out.println("finisheddddd");
    }

    private static String translateToDbColName(String name) {
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                name = name.replace(String.valueOf(c), "_" + Character.toLowerCase(c));
            }
        }
        return name;
    }

    public static synchronized <T extends BaseEntity> T save(T entity) {
        T copy = entity;
        if (copy.getId() == null) {
            createNewEntity(copy);
            copy.setId(getNewEntityId(entity.getClass()));
        }
        else {
            updateEntity(copy);
        }

        return entity;
    }

    private static <T extends BaseEntity> void updateEntity(T entity) {
        Map<String,String> fieldAndValues = new HashMap<>();
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getClass().getDeclaredFields()));
        fields.addAll(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fields.forEach(field -> {
            fieldAndValues.put(field.getName(),null);
        });
        //todo


    }

    private static <T extends BaseEntity> int getNewEntityId(Class<T> clazz) {
        try(Connection con = DriverManager.getConnection(
                CONNECTION, USER, PASSWORD);
            PreparedStatement preparedStatement = con.prepareStatement(String.format("SELECT id FROM %s order by id desc limit 1",clazz.getSimpleName()));
            ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if(!resultSet.next()){
                throw new Error("problem s id");
            }

            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new Error("problem s id",e);
        }

    }

    private static synchronized  <T extends BaseEntity> void createNewEntity(T entity) {
        Map<String, Method> map = new LinkedHashMap<>();
        builder.setLength(0);
        builder.append(String.format("INSERT INTO %s ( ", entity.getClass().getSimpleName()));
        List<Field> fields = new ArrayList<>(Arrays.asList(entity.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));
        fields.forEach(field -> {
            if (!field.getName().equals("id")) {
                field.setAccessible(true);
                String name = translateToDbColName(field.getName());
                builder.append(String.format(" %s,", name));
                Method getter;
                try {
                    getter = entity.getClass().getDeclaredMethod("get" +
                            field.getName().replace(name.charAt(0), Character.toUpperCase(field.getName().charAt(0))));
                } catch (NoSuchMethodException e) {
                    throw new Error("nema metod ", e);
                }
                map.put(name, getter);
            }
        });
        builder.delete(builder.length() - 1, builder.length());
        builder.append(") VALUES (");
        map.forEach((name, method) -> {
            try {
                Object result = method.invoke(entity);
                builder.append(String.format(" %s,", result ==null ? null: "'"+result+"'" ));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new Error("metoda ne stava", e);
            }
        });
        builder.delete(builder.length() - 1, builder.length());
        builder.append(")");
        try (Connection con = DriverManager.getConnection(CONNECTION, USER, PASSWORD);
             PreparedStatement preparedStatement = con.prepareStatement(builder.toString())) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Error("problem sus seifaneto", e);
        }
    }

}
