Database Management
-------------------
dependencies
------------


Intro
------
1. Exceptions
   1. Checked
      1. Exception that is extending java.lang.Exception (expect
         java.lang.RuntimeException) class that has to be explicitly declared in
         throws part of method signature of method that is throwing an exception and has
         to be explicitly handled by code that invokes the method. If code that is calling the
         method with checked exception does not handle exception, it has to declare it in
         throws part of method signature.
      2. Pros:
         1. Developer using API always has a list of
            exceptional situations that has to be
            handled
         2. Fast compile-time feedback on check if
            all exceptional situations were handled
      3. Cons:
         1. May result in cluttered code
         2. Coupling between callee and caller
   2. Unchecked
      1. Exception that is extending
         java.lang.RuntimeException class, does not have to be explicitly declared in
         throws part of method signature of method that is throwing an exception and does
         not have to be explicitly handled by code that invokes the method. Developer has
         freedom of choice if error handling should be implemented or not.
      2. Pros:
         1. Reduces cluttered code
         2. Reduces coupling between callee and
            caller
      3. Cons:
         1. May result in missing situations in which
            error handling should be implemented
         2. Lack of compile-time feedback on error
            handling
   3. Spring encourages Runtime exception due to its pros
   4. Spring  data access exception hierarchy
      1. Data Access Exception is a Runtime Exception
      2. Examples of concrete Data Access Exceptions
         1. CannotAcquireLockException
         2. CannotCreateRecordException
         3. DataIntegrityViolationException
      3. Purpose of this hierarchy is to create
         abstraction layer on top of Data Access APIs to
         avoid coupling with concrete implementation
         of Data Access APIs

configure a DataSource
----------------------
1. DataSource
   1. Data Source is represented by generic interface javax.sql.DataSource which represent any
      data source for sql database
   2. To configure data source in Spring you need to create a @Configuration class that will return
      javax.sql.DataSource bean.
   3. You can use for example following types of javax.sql.DataSource:
   4. You can use for example following types of javax.sql.DataSource:
         1. DriverManagerDataSource – basic JDBC driver connection source
         2. BasicDataSource – Apache DBCP for Connection Pooling
         3. ComboPooledDataSource - C3P0 for Connection Pool
2. Configuration of Data Source in Spring is dependent on type of application that is
   executed.
   1. Type of execution:
      1. `Standalone` – Data Source is configured in @Configuration class and is
         created as a bean of one of supported data source types
      2. `Spring Boot `– Data Source is configured through application.properties
3. working with development/test databases, following beans are very useful:
   1. EmbeddedDatabaseBuilder – allows to easily configure H2/HSQLDB embedded
      database with schema/data initialization scripts
   2. DataSourceInitializer / ResourceDatabasePopulator – allows to use
      schema/data initialization scripts without usage of EmbeddedDatabaseBuilder

4. Example for Stand Alone
   1. Using hsqldb in memory database
   2. Example
      ```java
      // configuration - setting up Db config and population of test data
      
      @Configuration
      public class DataSourceConfiguration {
      @Bean
      public DataSource dataSource() {
          BasicDataSource basicDataSource = new BasicDataSource();
          basicDataSource.setDriverClassName("org.hsqldb.jdbcDriver");
          basicDataSource.setUrl("jdbc:hsqldb:mem:localhost");
          return basicDataSource;
      }
      }
      
      @Configuration
      public class DataSourceTestDataConfiguration {

      @Value("classpath:/db-schema.sql")
      private Resource schemaScript;
      @Value("classpath:/db-test-data.sql")
      private Resource dataScript;

      @Bean
      public DataSourceInitializer dataSourceInitializer(@Autowired final DataSource dataSource) {
          final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
          dataSourceInitializer.setDataSource(dataSource);
          dataSourceInitializer.setDatabasePopulator(databasePopulator());
          return dataSourceInitializer;
      }

      private DatabasePopulator databasePopulator() {
          final ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
          databasePopulator.addScript(schemaScript);
          databasePopulator.addScript(dataScript);
          return databasePopulator;
      }
      }  
      
      // Repo - database connected via JdbcTemplate
      @Repository
      public class EmployeeDao {
      private JdbcTemplate jdbcTemplate;

          @Autowired
          public void setDataSource(DataSource dataSource) {
              jdbcTemplate = new JdbcTemplate(dataSource);
          }

          public List<String> findEmployeeEmails() {
              return jdbcTemplate.queryForList(
                "select email from employee",
                String.class
              );
          }
      }
      
      //Service
      
      @Service
      public class EmployeeReportService {

          @Autowired
          private EmployeeDao employeeDao;

          public void printReport() {
              System.out.println("Employee Report Start");

              employeeDao.findEmployeeEmails()
                      .forEach(System.out::println);

              System.out.println("Employee Report Stop");
          }
      }
      
      
         ```
      ```sql
      //db-schema.sql
      create table employee
      (      
      employee_id  int,
      first_name   varchar(32),
      last_name    varchar(32),
      email        varchar(32),
      phone_number varchar(32),
      hire_date    date,
      salary       int
      );
      
      //db-test-data.sql
      insert into employee(employee_id, first_name, last_name, email, phone_number, hire_date, salary)
      values (1, 'John', 'Doe', 'John.Doe@corp.com', '555-55-55', '2019-06-05', 70000),
      (2, 'Willow', 'Zhang', 'Willow.Zhang@corp.com', '555-55-56', '2019-07-12', 80000),
      (3, 'Jayvon', 'Grant', 'Jayvon.Grant@corp.com', '555-55-57', '2019-07-17', 90000),
      (4, 'Shaylee', 'Mcclure', 'Shaylee.Mcclure@corp.com', '555-55-58', '2019-07-19', 120000),
      (5, 'Miley', 'Krueger', 'Miley.Krueger@corp.com', '555-55-59', '2019-07-20', 110000);
      ```
      
5. Example for Spring boot
   1. Data Source is configured through application.properties
   2. database is autoconfigured by springboot
   3. Example
      ```java
      // Configuration is done automatically by SpringBoot by reading application.properties and schema.sql files specified from resources
      
      //application.properties
      spring.datasource.url=jdbc:hsqldb:mem:localhost
      spring.datasource.driver-class-name=org.hsqldb.jdbcDriver
      
      // schema and test data is same as above example
      
      //service
      @Service
      public class EmployeeReportService {

          @Autowired
          private EmployeeDao employeeDao;

          public void printReport() {
              System.out.println("Employee Report Start");

              employeeDao.findEmployeeEmails()
                      .forEach(System.out::println);

              System.out.println("Employee Report Stop");
          }
      }
      
      //REPO
      @Repository
      public class EmployeeDao {
      private JdbcTemplate jdbcTemplate;

          @Autowired
          public void setDataSource(DataSource dataSource) {
              jdbcTemplate = new JdbcTemplate(dataSource);
          }

          public List<String> findEmployeeEmails() {
              return jdbcTemplate.queryForList(
                "select email from employee",
                String.class
              );
          }
      }
      
      //main
      @SpringBootApplication
      @ComponentScan(basePackages = {"dao","service"})
      public class SpringBootConsoleApplication implements CommandLineRunner {

      @Autowired
      private EmployeeReportService employeeReportService;

      public static void main(String[] args) {

          SpringApplication.run(SpringBootConsoleApplication.class, args);
      }
      @Override
      public void run(String... args) throws Exception {
          employeeReportService.printReport();
      }

       }
      
      //dependencies
      implementation group: 'org.springframework.boot', name: 'spring-boot-starter', version: '2.5.3'
      implementation group: 'org.springframework', name: 'spring-jdbc', version: '5.3.9'
      implementation group: 'org.hsqldb', name: 'hsqldb', version: '2.5.0'
      implementation group: 'org.apache.commons', name: 'commons-dbcp2', version: '2.6.0'
      ```
      
   
   


