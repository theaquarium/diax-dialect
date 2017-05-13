package me.diax.dialect;

import com.knockturnmc.api.util.NamedProperties;
import com.knockturnmc.api.util.Property;

import java.io.Serializable;

final class DatabaseProperties extends NamedProperties implements Serializable {

    private static final long serialVersionUID = -2580963340292493348L;

    @Property(
            value = "hikari.dataSource.url",
            defaultvalue = "jdbc:mysql://localhost:3306/diaxdialect?autoReconnect=true&useSSL=false"
    )
    private String datasourceUrl;

    @Property(value = "hikari.dataSource.user", defaultvalue = "diaxdialect")
    private String datasourceUser;

    @Property(value = "hikari.dataSource.password", defaultvalue = "diaxDialect")
    private String datasourcePassword;

    @Property(value = "hikari.connectionInitSql", defaultvalue = "SET TIME_ZONE = '+00:00';")
    private String connectionInitSql;

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public String getDatasourceUser() {
        return datasourceUser;
    }

    public String getDatasourcePassword() {
        return datasourcePassword;
    }

    public String getConnectionInitSql() {
        return connectionInitSql;
    }
}
