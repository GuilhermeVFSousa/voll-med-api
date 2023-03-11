package med.voll.api.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {
	
	@Value("${spring.datasource.password}")
	private String senha;

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.url}")
	private String url;

	@Bean
	public DataSource getDataSource() {
        return DataSourceBuilder.create()
          .driverClassName("com.mysql.jdbc.Driver")
          .url(url)
          .username(user)
          .password(senha)
          .build();


    }
}

