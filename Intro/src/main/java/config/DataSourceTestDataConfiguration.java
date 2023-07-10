package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
public class DataSourceTestDataConfiguration {
    @Value("classpath:/db-schema.sql")
    private Resource schemaScript;

    @Value("classpath:/db-test-data.sql")
    private Resource dataScript;

    @Bean
    public DataSourceInitializer dataSourceInitializer(@Autowired final DataSource dataSource )
    {
        final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(databasePopulator());
        return dataSourceInitializer;
    }

    private DatabasePopulator databasePopulator()
    {
        final ResourceDatabasePopulator resourceDatabasePopulator=new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(schemaScript);
        resourceDatabasePopulator.addScript(dataScript);
        return resourceDatabasePopulator;
    }

}
