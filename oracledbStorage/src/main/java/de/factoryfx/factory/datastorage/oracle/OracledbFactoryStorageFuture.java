package de.factoryfx.factory.datastorage.oracle;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.DataSerialisationManager;
import de.factoryfx.data.storage.ScheduledDataMetadata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class OracledbFactoryStorageFuture<R extends Data,S> {

    private final DataSerialisationManager<R,S> dataSerialisationManager;
    private final Supplier<Connection> connectionSupplier;

    public OracledbFactoryStorageFuture(Supplier<Connection> connectionSupplier, DataSerialisationManager<R,S> dataSerialisationManager){
        this.connectionSupplier = connectionSupplier;
        this.dataSerialisationManager = dataSerialisationManager;

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE FACTORY_FUTURE " +
                        "(id VARCHAR(255) not NULL, " +
                        " factory BLOB, " +
                        " factoryMetadata BLOB, " +
                        " PRIMARY KEY ( id ))";

                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            //oracle don't know IF NOT EXISTS
            //workaround ignore exception
//            throw new RuntimeException(e);
        }
    }

    public R getFutureFactory(String id) {

        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_FUTURE WHERE id='"+id+"'";

                try (ResultSet resultSet =statement.executeQuery(sql)) {
                    if (resultSet.next()) {
                        ScheduledDataMetadata factoryMetadata = dataSerialisationManager.readScheduledFactoryMetadata(JdbcUtil.readStringToBlob(resultSet, "factoryMetadata"));
                        return dataSerialisationManager.read(JdbcUtil.readStringToBlob(resultSet, "factory"), factoryMetadata.dataModelVersion);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    public Collection<ScheduledDataMetadata<S>> getFutureFactoryList() {
        ArrayList<ScheduledDataMetadata<S>> result = new ArrayList<>();
        try (Connection connection= connectionSupplier.get()){
            try (Statement statement = connection.createStatement()){
                String sql = "SELECT * FROM FACTORY_FUTURE";

                try (ResultSet resultSet =statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        result.add(dataSerialisationManager.readScheduledFactoryMetadata(JdbcUtil.readStringToBlob(resultSet, "factoryMetadata")));
                    }
                }


            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public void addFuture(ScheduledDataMetadata<S> metadata, R factoryRoot) {
        String id=metadata.id;

        try (Connection connection= connectionSupplier.get()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FACTORY_FUTURE(id,factory,factoryMetadata) VALUES (?,?,? )")){
                preparedStatement.setString(1, id);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.write(factoryRoot),preparedStatement,2);
                JdbcUtil.writeStringToBlob(dataSerialisationManager.writeStorageMetadata(metadata),preparedStatement,3);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteFutureFactory(String id) {
        try (Connection connection= connectionSupplier.get();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM FACTORY_FUTURE WHERE id = ?")){
                preparedStatement.setString(1, id);
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
