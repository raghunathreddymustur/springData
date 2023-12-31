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
      
   

JDBC Template
-------------
1. Template design pattern 
   1. Template design pattern is a behavioral design pattern that can be used to
      encapsulate algorithm/main flow with it steps in a way to achieve steps
      customization and shared code reusability. 
   2. It is achieved by creating abstract
      class that contains algorithm definition/main flow with shared code, and child
      classes extending abstract class which are customizing step or steps of the
      algorithm.
   3. Note
      1. Template design pattern can be used to achieve greater code reusability,
         however since it is using inheritance, which is very strong relationship between
         classes it can limit future flexibility of the system. You should use this pattern
         with caution and you should analyze if strategy design pattern will not give you
         similar results. Strategy uses composition instead of inheritance and in some
         cases instead of using template method, strategy can be used to achieve code
         reusability and also code flexibility.
   4. Example
      ![img.png](img.png)
2. JDBC Template
   1. Jdbc Template is a class located in org.springframework.jdbc.core package.
      Goal of this class is to simplify use of JDBC by providing implementation of JDBC
      workflow, leaving application to provide SQL statements and results extractions.
   2. it cares of open, establishing and closing the connection. you have just provide the quiries to execute
   3. Jdbc Template executes SQL queries or updates, initiates iteration over ResultSet,
      ResultSet mapping, also it catches exceptions and translates them into generic
      exceptions.
   4. Code that interacts with Jdbc Template needs to provide implementation of callback
      interfaces which allows specific steps of JDBC workflow customization:
      1. PreparedStatementCreator
      2. ResultSetExtractor
      3. PreparedStatementSetter
      4. RowMapper
   5. Example
      ```java
      public class EmployeeDao {
      private JdbcTemplate jdbcTemplate;

      @Autowired
      public void setDataSource(DataSource dataSource) {
      jdbcTemplate = new JdbcTemplate(dataSource);
      }

      public List<Employee> findEmployees() {
      return jdbcTemplate.query(
      "select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee",
      this::mapEmployee
      );
      }

      public Employee findFirstHiredEmployee() {
      return jdbcTemplate.queryForObject(
      "select * from employee order by hire_date limit 1",
      this::mapEmployee
      );
      }

      public Employee findEmployeeWithHighestSalary() {
      return jdbcTemplate.queryForObject(
      "select * from employee order by salary desc limit 1",
      this::mapEmployee
      );
      }

      public int findEmployeesCount() {
      return jdbcTemplate.queryForObject(
      "select count(*) from employee",
      Integer.class
      );
      }   

      @SneakyThrows
      private Employee mapEmployee(ResultSet resultSet, int i) {
      return new Employee(
      resultSet.getInt("employee_id"),
      resultSet.getString("first_name"),
      resultSet.getString("last_name"),
      resultSet.getString("email"),
      resultSet.getString("phone_number"),
      resultSet.getDate("hire_date"),
      resultSet.getFloat("salary")
      );
      }
      }
      ```
Jbbc Call backs
   -------------
1. Callback
   1. A callback is a code or reference to the code that can be passed as an argument
      to the method. This method will execute passed callback during execution.
      1. On Java level callback can be:
         1. Class that implements interface
            Example
            ![img_1.png](img_1.png)
            ```java
            // function that accepts callback
            numbersEvaluator.evaluate(4, expressionEvaluator, valuePrinter);
            
            ExpressionEvaluator expressionEvaluator = new AddExpressionEvaluator();

             ```
         2. Anonymous class
            ```java
            ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator() {
            @Override
            public int evaluate(int a, int b) {
                return a * b;
            }
            };
             ```
         3. Lambda expression – JDK 8
               ````java
            ExpressionEvaluator expressionEvaluator = (a, b) -> a + b;

              ````
         4. Reference Method – JDK 8
            ```java
            ExpressionEvaluator expressionEvaluator = this::powEvaluator;

            ```
2. Jdbc Template Callbacks that can be used with queries:
   1. RowMapper
      1. interface for processing ResultSet data on per-row basis,
         implementation should call ResultSet.get*(..) methods, **but should not call
         ResultSet.next()**, it should only extract values from current row and based on
         those values should create object, which will be returned from mapRow method,
         implementation is `usually stateless`.
      2. implementing the `mapRow(ResultSet resultSet, int rowNum)` method
      3. Example
         ```java
            //using RowMapper Interface
            public List<Employee> getEmployeeList()
            {
            return jdbcTemplate.query("select employee_id, first_name, last_name, email, phone_number, hire_date,          salary from employee", new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Employee(
            resultSet.getInt("employee_id"),
            resultSet.getString("first_name"),
            resultSet.getString("last_name"),
            resultSet.getString("email"),
            resultSet.getString("phone_number"),
            resultSet.getDate("hire_date"),
            resultSet.getFloat("salary")
            );
            };
            });
            }


         ```
   2. RowCallbackHandler
      1. interface for processing ResultSet data on a per-row
         basis, implementation should call ResultSet.get*(..) methods, but should
         **not call ResultSet.next()**, it should only extract values from current row,
         implementation is usually `stateful`, it keeps accumulated data in some object,
         processRow method from this class does not return any value, instead method
         saves results into for example object field that will keep state
      2. by implementing the `processRow(ResultSet)` method
      3. Example
         ```java
         public float findAverageSalaryRowByRow() {
         AverageSalaryRowCallbackHandler averageSalaryRowCallbackHandler = new AverageSalaryRowCallbackHandler();

         jdbcTemplate.query(
         "select salary from employee",
         averageSalaryRowCallbackHandler
         );

         return averageSalaryRowCallbackHandler.getAverageSalary();
         }
         
         //CallBack
         class AverageSalaryRowCallbackHandler implements RowCallbackHandler {
         private float salarySum = 0;
         private int salariesCount = 0;

         @Override
         public void processRow(ResultSet rs) throws SQLException {
         salarySum += rs.getFloat("salary");
         ++salariesCount;
         }

         public float getAverageSalary() {
         return salarySum / (float) salariesCount;
         }
         }

         ```
   3. ResultSetExtractor
      1. – interface for processing entire ResultSet data, all
         rows needs to be processed and implementation `should call ResultSet.next()`
         method to move between rows, implementation is usually stateless,
         implementation should not close ResultSet, it will be closed by Jdbc Template
      2. should implement `extractData(ResultSet rs)` method
      3. Example
         ```java
         //callback
         class AverageSalaryResultSetExtractor implements ResultSetExtractor<Float> {
         @Override
         public Float extractData(ResultSet rs) throws SQLException, DataAccessException {
         float salarySum = 0;
         int salariesCount = 0;

               while (rs.next()) {
                   salarySum += rs.getFloat("salary");
                   ++salariesCount;
               }

               return salarySum / (float) salariesCount;
         }
         }

         ```
         
   4. Jdbc Template other Callbacks:
      1. PreparedStatementCreator
         1. should create PreparedStatement based on
            Connection provided by JdbcTemplate, implementation should provide SQL
            and parameters
      2. PreparedStatementSetter
         1. should set values on PreparedStatement
            provided by JdbcTemplate, implementation should only set parameters, SQL will
            be set by JdbcTemplate
      3. Example of 
         ```java
         public int findEmployeeIdFromEmail(String email) {
            return jdbcTemplate.query(
            new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement("select employee_id from employee where email = ?");
            }
            },
            new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, email);
            }
            },
            new ResultSetExtractor<Integer>() {
            @Override
         public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
         if (rs.next())
         return rs.getInt("employee_id");
         else
         throw new SQLException("Unable to find id based on email");
         }
         }
         );
         }
         ```
      4. CallableStatementCreator
         1. should create CallableStatement based on
            Connection provided by JdbcTemplate, implementation should provide SQL
            and parameters
      5. PreparedStatementCallback
         1. used internally by JdbcTemplate –
            generic interface allowing number of operations on single PreparedStatement
      6. CallableStatementCallback 
         1. used internally by JdbcTemplate –
            generic interface allowing number of operations on single CallableStatement


Execution of plain SQL statements - JDBC Template
----------------------------------------------
1. JDBC Template allows execution of plain SQL statements with following
   methods
   1. query
   2. queryForList
      1. returns list of objects of declared type, `expects query to return
         results with only one column`, otherwise
         IncorrectResultSetColumnCountException will be thrown
   3. queryForObject
      1. returns single object, expects query to return only one record, if
         this requirement is not matched
         IncorrectResultSizeDataAccessException will be thrown
   4. queryForMap
      1. returns map for single row with keys representing column names
         and values representing database record value, expects query to return only one
         record, if this requirement is not matched
         IncorrectResultSizeDataAccessException will be thrown
   5. queryForRowSet
      1. returns SqlRowSet object that contains metadata information
         (like column names) and allows to read results data and iterate through records
   6. execute
   7. update
   8. batchUpdate
   9. Most of the methods above have many versions, allowing you to specify not only query
      itself, but also parameters to the query and customer row mapper if required.
   10. Jdbc Template returns objects, lists/map by using following:
       1. objects – queryForObject – SingleColumnRowMapper for generic types and
          RowMapper for custom types
       2. lists – queryForList – SingleColumnRowMapper for generic types
       3. maps – queryForMap – ColumnMapRowMapper for any query
   11. Examples
       ```java
       //query
       public List<Employee> findEmployees() {
         return jdbcTemplate.query(
                 "select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee",
                 this::mapEmployee
            );
         }
      
       //queryForList
       public List<String> findEmployeesEmails() {
         return jdbcTemplate.queryForList("select email from employee", String.class);
          }
       
        //OUT PUT - John,Jayvon, ...
       
       //queryForObject
       public Employee findEmployeeWithHighestSalary() {
         return jdbcTemplate.queryForObject(
                 "select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee order by salary desc limit 1",
                 this::mapEmployee
         );
       }
        
       //queryForObject - for Prepared statement
       public Employee findEmployeeById(Integer id) {
         return jdbcTemplate.queryForObject(
                 "select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee where employee_id = ?",
                 new Object[]{id},
                 this::mapEmployee
         );
         }
        //Output : findEmployeeEmail(1)
                  //John.Doe@corp.com
      
       //queryForMap
       public Map<String, Object> findEmployeeThatWasHiredLast() {
         return jdbcTemplate.queryForMap(
                 "select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee order by hire_date limit 1"
         );
         }
       
       //Output - {EMPLOYEE_ID=2, FIRST_NAME=Willow, LAST_NAME=Zhang, EMAIL=Willow.Zhang@corp.com, //PHONE_NUMBER=555-55-56, HIRE_DATE=2019-07-12, SALARY=80000}
       
       //queryForRowSet
       public SqlRowSet findEmployeesEmailsAndPhones() {
         return jdbcTemplate.queryForRowSet(
                 "select email, phone_number from employee"
         );
        }
      
       //execute
        public void insertNewDummyRecord() {
         jdbcTemplate.execute(
                 "insert into employee values(999, 'Dummy', 'Dummy', 'Dummy.Dummy@dummy.com', '111-11-11', '2019-06-05', 1)"
         );
         }
      
       //updata & batch Update
       public int updateDummyRecord(Integer id, String firstName) {
         return jdbcTemplate.update(
                 "update employee set first_name = ? where employee_id = ?",
                 new Object[]{firstName, id}
         );
         }
      
       public int[] updateRecordsWithDummyData() {
         return jdbcTemplate.batchUpdate(
                 "update employee set first_name = 'AAA' where employee_id = 1",
                 "update employee set first_name = 'BBB' where employee_id = 2",
                 "update employee set first_name = 'CCC' where employee_id = 3"
         );
        }
    
         ```

JDBC template - acquire and release a connection
---
1. Connection lifecycle in JDBC Template depends on transactions being involved or not.
2. If JDBC Template is used without transaction, then connection is acquired and
   released for every method call. Reason for this strategy, is to minimize amount of
   time when resource (connection) has to be held.
3. If JDBC Template is used together with transaction, then DataSourceUtils which is
   using TransactionSynchronizationManager will reuse connection between
   method calls as long as transaction is not committed or rolled back. Reason for this
   strategy is that connection cannot be closed when transaction is in progress, since
   closing connection would also rollback any changes made.
4. JDBC Template uses getConnection() method from DataSource class through
   DataSourceUtils class. If DataSource is plain JDBC Connection source, then
   connection is actually opened/closed, however if Connection Pool, like DBCP or C3P0
   is used, then connection is not being opened/closed, however it is acquired or
   released from/to the pool.

Transactions
----------------
1. Transaction is an operation that consist of series of tasks, in which all of those tasks
   should be performed, or none of the tasks should be performed. Those tasks are being
   treated as one unit of work. If all tasks in transaction are successful, changes made by
   those tasks are preserved, if at least one of the tasks is unsuccessful, changes made
   by tasks that were already completed will be reverted and any tasks awaiting
   execution will no be executed.
2. Transaction should follow ACID principle:
   1. Atomicity – All changes are applied or none changes are applied
   2. Consistency – system should go from one valid state to other valid state, any
      constraints on data should never be left in invalid state
   3. Isolation – one transaction cannot affect other one, when multiple transactions are executing parallley, the isolation level determines how many changes of one transaction are visible to other. 
   4. Durability – guarantees that if transaction has been committed, data will be
      preserved, even in case of system/power failure
3. a local vs global transaction
   1. Global transaction is a kind of transaction that spans multiple transactional
      resources. Those resources can be anything, but usually include databases (can
      be more then one) and queues. In Java, popular standard for managing global
      transaction is JTA, which is an API for using transaction system provided by
      Application Server.
   2. Local transaction are resource specific transaction, they do not span across
      multiple transactional resources. Local transactions are much simpler than global
      transaction however main disadvantages is lack of ability to treat series of tasks
      dealing with multiple transactional resources such as databases or databases and
      queues as single unit of work.
   3. Global transaction will roll back the changes on all distributed databases that are involved in a transaction but in local it fails.

transactions vs Spring
---------------------
1. Transaction is a cross cutting concern and in Spring it is implemented with usage
   of @Transactional annotation.
2. If @Transactional annotation is present on top of the method or entire class,
   then each call to the method in the class will be proxied by
   TransactionInterceptor and TransactionAspectSupport classes. Those
   classes will interact with PlatformTransactionManager to commit
   transaction upon successful method execution or rollback upon exception. Exact
   behavior will be dependent on transaction propagation and isolation level
   settings, which can be set in @Transactional annotation.
3. Example
   ```java
   @Transactional
    public void saveEmployeeInTransaction() {
        employeeDao.saveEmployee(new Employee(1, "John", "Doe", "John.Doe@corp.com", "555-55-55", Date.valueOf("2019-06-05"), 70000));
    }

      ```

Defining a transaction in Spring
-------------------------------
1. To use transactions in Spring Framework, you need to:
   1. Enable transaction management by using
      `@EnableTransactionManagement` annotation on top of your Configuration
      class
   2. When @EnableTransactionManagement is used, TransactionInterceptor
      and TransactionAspectSupport will be used to proxy each call to
      @Transactional class or method, which will use
      PlatformTransactionManager to manage transaction.
   3. @EnableTransactionManagement allows you to specify following values:
      1. Mode – sets advice mode for @Transactional annotation, indicates how calls to
         methods should be intercepted, PROXY is default mode, you can switch it to more
         advanced ASPECTJ weaving advice, which supports local calls
      2. Order – indicates order of advice execution when more then one advice applies to
         @Transactional join point
      3. proxyTargetClass – indicates whether CGLIB Proxy classes should be created or if
         JDK Proxies should be created (default), this field is used only when Mode is
         set to PROXY
   4. Create bean method in configuration class that will return bean implementing
      interface `PlatformTransactionManager`, examples of transactions
      managers:
      1. DataSourceTransactionManager, JtaTransactionManager, JpaTransactionManager...etc
   5. Dependencies
      ![img_2.png](img_2.png)
   6. Example
      ```java
      // Config
      
      @Configuration
      @EnableTransactionManagement
      public class DataSourceConfiguration {

      @Bean
      public DataSource dataSource()
      {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setScriptEncoding("UTF-8")
                .addScript("db-schema.sql")
                .build();
      }

      @Bean
      @Autowired
      public PlatformTransactionManager platformTransactionManager(DataSource dataSource)
      {
        return new DataSourceTransactionManager(dataSource);
      }

       }
      
      // Repo
      @Repository
      public class EmployeeDao {
      private JdbcTemplate jdbcTemplate;

      @Autowired
      public void setDataSource(DataSource dataSource) {
          jdbcTemplate = new JdbcTemplate(dataSource);
      }
      
      public void saveEmployee(Employee employee) {
        if (employee.getId() < 0)
            throw new IllegalArgumentException("Employee Id has to be greater than zero");

        int numberOfRecordsInserted = jdbcTemplate.update(
                "insert into employee(employee_id, first_name, last_name, email, phone_number, hire_date, salary) " +
                        "values (?, ?, ?, ?, ?, ?, ?)",
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getHireDate(),
                employee.getSalary()
        );

        if (numberOfRecordsInserted == 1)
            System.out.println(String.format("Saved employee [%d]", employee.getId()));
        else
            throw new IllegalStateException(String.format("Expected 1 record to be inserted, instead retrieved [%d] number of records inserted", numberOfRecordsInserted));
      }
      
      @SneakyThrows
      private Employee mapEmployee(ResultSet resultSet, int i) throws SQLException {
          return new Employee(
         resultSet.getInt("employee_id"),
         resultSet.getString("first_name"),
         resultSet.getString("last_name"),
         resultSet.getString("email"),
         resultSet.getString("phone_number"),
         resultSet.getDate("hire_date"),
         resultSet.getFloat("salary")
         );
         }
         }
      
      // Service
      @Service
      public class EmployeeService {
        @Autowired
        private EmployeeDao employeeDao;
        public void saveEmployeesWithoutTransaction() {
        System.out.println("Saving employees without transaction...");
        saveEmployees();
           }

         @Transactional
      public void saveEmployeesInTransaction() {
      System.out.println("Saving employees with transaction...");
      saveEmployees();
      }

      private void saveEmployees() {
      employeeDao.saveEmployee(new Employee(1, "John", "Doe", "John.Doe@corp.com", "555-55-55", Date.   valueOf("2019-06-05"), 70000));
      employeeDao.saveEmployee(new Employee(2, "Willow", "Zhang", "Willow.Zhang@corp.com", "555-55-56",    Date.valueOf("2019-07-12"), 80000));
      employeeDao.saveEmployee(new Employee(3, "Jayvon", "Grant", "Jayvon.Grant@corp.com", "555-55-57",    Date.valueOf("2019-07-17"), 90000));
      employeeDao.saveEmployee(new Employee(-1, "Shaylee", "Mcclure", "Shaylee.Mcclure@corp.com",    "555-55-58", Date.valueOf("2019-07-19"), 120000));
      employeeDao.saveEmployee(new Employee(5, "Miley", "Krueger", "Miley.Krueger@corp.com", "555-55-59",    Date.valueOf("2019-07-20"), 110000));
      }
        
         }
        ```
   7. Use `@Transactional` annotation on top of classes or methods that should
      involve transaction management
      1. @Transactional annotation can be used on top of classes or methods to
         enable transaction management for entire class or specified methods. When
         method with @Transactional annotation is called, invocation is proxied by
         TransactionInterceptor and TransactionAspectSupport which are
         using `PlatformTransactionManager` to manage transaction.
         1. PlatformTransactionManager is an interface that is used by declarative
            Spring’s AOP Transaction Management to create, commit and rollback
            transactions.
         2. PlatformTransactionManager contains following methods
            1. getTransaction – returns currently active transaction or creates new one
            2. commit – commits transaction, or rolls back transaction if it was marked for
               rollback
            3. rollback – performs rollback of transaction
      2. Transaction is being started at the beginning of the method (if none transaction
         exists), and it is being committed at the end of successful execution. Transaction
         can be rolled back upon exception being thrown. This behavior is dependent on
         transaction propagation type.
      3. @Transactional annotation allows you to configure following attributes:
         1. Transaction Manager
         2. Propagation Type
         3. Isolation Level
         4. Timeout for Transaction
         5. Read Only Flag
         6. Define which exception types will cause transaction rollback
         7. Define which exception types will not cause transaction rollback

Isolation Levels
---------------
1. Transaction Isolation determines how changes made under one transaction are
   visible in other transactions and to other users of the system. Higher isolation
   level means that changes from one transaction are not visible and lower isolation
   level means that changes from one transactions may “slip” into selects executed
   under other transaction.
2. Higher transaction isolation level make data being visible in more consistent way,
   lower transaction isolation level makes data less consistent but increases overall
   throughput and concurrency of the system.
3. Most Relational Databases support 4 transaction levels:
   1. Repeatable Read - Often used
      1. Read is independent of all other parallel transaction
         1. You will get same first read values even other parallel transactions have committed changes on that particular value
         2. Multiple reads within same transaction are consistent
         Example
         ![img_3.png](img_3.png)
   2. Read Committed
      1. Reads within the same transaction always reads the fresh value committed by other transactions
         1. Multiple reads within same transaction are inconsistent
      2. Example
         ![img_4.png](img_4.png)
   3. Read Uncommitted
      1. Reads even uncommitted values from other transaction
         1. Dirty read problem
         2. Example
            ![img_5.png](img_5.png)
   4. Serializable - more strict
      1. Every read is a locking read and while one transaction read, other will have to wait untill transaction is committed or roll backed
      2. Example
         ![img_6.png](img_6.png)
         After Commit
         ![img_7.png](img_7.png)
   5. In Spring Framework, you can use @Transactional annotation to set isolation
      level.
      ```java
      @Transactional(isolation = Isolation.SERIALIZABLE)
      @Transactional(isolation = Isolation.REPEATABLE_READ)
      @Transactional(isolation = Isolation.READ_COMMITTED)
      @Transactional(isolation = Isolation.READ_UNCOMMITTED)
         ```

Transaction propagation
----------------------
1. In Spring transactions, transaction propagation refers to the behavior of a transaction when it encounters an existing transaction context. It determines how the current transaction interacts with the existing transaction, if any.
2. spring provides different transaction propagation options, which can be specified using the propagation attribute on the @Transactional annotation or in the transaction configuration. Here are some commonly used propagation options:
3. Suppose we have two methods, methodA() and methodB(), both annotated with @Transactional in a Spring application. methodA() is already executing within a transaction context, and methodB() is called from within methodA().
   ```java
   @Transactional(propagation = Propagation.REQUIRED)
   public void methodA() {
   // Transactional logic
   methodB();
   }

   @Transactional(propagation = Propagation.REQUIRED)
   public void methodB() {
   // Transactional logic
   }
   ```
4. Transaction propagation can be defined in @Transactional annotation in
propagation field as one of following options:
   1. REQUIRED - support a current transaction, create a new one if none exists
   2. SUPPORTS - support a current transaction, execute non-transactionally if none
      exists
   3. MANDATORY - support a current transaction, throw an exception if none exists
   4. REQUIRES_NEW - create a new transaction, and suspend the current transaction if
      one exists
   5. NOT_SUPPORTED - execute non-transactionally, suspend the current transaction if
      one exists
   6. NEVER - execute non-transactionally, throw an exception if a transaction exists
   7. NESTED - execute within a nested transaction if a current transaction exists,
      behave like REQUIRED else
   8. Example
      ```java
      @Repository
      public class EmployeeDao {
      @Transactional(propagation = REQUIRED)
      public void requiredTransactionMethod() {
      }

        @Transactional(propagation = SUPPORTS)
      public void supportsTransactionMethod() {
      }

      @Transactional(propagation = MANDATORY)
      public void mandatoryTransactionMethod() {
      }

      @Transactional(propagation = REQUIRES_NEW)
      public void requiresNewTransactionMethod() {
      }

      @Transactional(propagation = NOT_SUPPORTED)
      public void notSupportedTransactionMethod() {
      }

      @Transactional(propagation = NEVER)
      public void neverTransactionMethod() {
      }

      @Transactional(propagation = NESTED)
      public void nestedTransactionMethod() {
      }
      }
       ```
@Transactional annotated method is calling another @Transactional annotated method on the same object instance
   -------
1. JDK Proxy and CGLIB Proxy in Spring Beans AOP do not support self invocation, so
   when one method with @Transactional annotation calls different method
   with @Transactional annotation from the same class, nothing happens,
   transaction interceptor will not be called.
2. To enable self invocation support, you need to configure Spring Aspects with
   AspectJ, to do that you need to:
   1. Have dependency to spring-aspects
   2. Include aspectj-maven-plugin
   3. Configure Transaction Support with
      1. @EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
3. Example
   ```java
           <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>
   
        <plugin>
                <groupId>com.github.m50d</groupId>
                <artifactId>aspectj-maven-plugin</artifactId>
                <version>1.11.1</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <source>${java.source.target.version}</source>
                    <target>${java.source.target.version}</target>
                    <complianceLevel>${java.source.target.version}</complianceLevel>
                    <forceAjcCompile>true</forceAjcCompile>
                    <aspectLibraries>
                        <aspectLibrary>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-aspects</artifactId>
                        </aspectLibrary>
                    </aspectLibraries>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.aspectj</groupId>
                        <artifactId>aspectjtools</artifactId>
                        <version>${aspectj.version}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.aspectj</groupId>
                        <artifactId>aspectjweaver</artifactId>
                        <version>${aspectj.version}</version>
                    </dependency>
                </dependencies>
            </plugin>
   
            //config   
            @Configuration
            @EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
            public class DataSourceConfiguration { }

   ```
@Transactional Usage
-------------
1. @Transactional can be used on top of class or method, in classes or
   interfaces. 
2. If used on top of class, it applies to all public methods in this class.
3. **If used on top of method, it needs to have public access modifier, if used on top
   of protected / package-visible / private method, transaction management will
   not be applied.**

Declarative transaction management 
-------------------
1. Declarative transaction management means that instead of handling transactions
   manually through the code, methods which should be executed in transactions
   are declared with @Transactional annotation.
2. Example
   ```java
   @Service
   public class EmployeeService {

    @Autowired
    private DataSource dataSource;

    @Transactional
    public void declarativeTransaction() {
        // use dao to update data
    }
    
    //manually writing the code 
    public void manualTransaction() throws SQLException {
        Connection connection = dataSource.getConnection();

        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        try {
            // use connection to update data on transaction

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.close();
        }
    }
   }
    ```

Default rollback policy
--------------
1. Default rollback policy in Spring Framework is set to automatic rollback, but only
   when unchecked exception is being thrown from the method annotated with
   @Transactional annotation.
2. When checked exception is being thrown from
   the method, transaction is not being rolled back.
3. You can override this policy by setting rollbackFor /
   rollbackForClassName or noRollbackFor / noRollbackForClassName
   field in @Transactional annotation.
4. Example
   ```java
   @Service
   public class EmployeeService {

    @Transactional
    public void methodWithUncheckedExceptionCausingRollback() {
        throw new IllegalArgumentException("test unchecked exception that will cause rollback");
    }

    @Transactional
    public void methodWithCheckedExceptionNotCausingRollback() throws CustomException {
        throw new CustomException("test checked exception that will not cause rollback");
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void methodWithUncheckedExceptionNotCausingRollback() {
        throw new IllegalArgumentException("test unchecked exception that will not cause rollback because of noRollbackFor field");
    }

    @Transactional(rollbackFor = CustomException.class)
    public void methodWithCheckedExceptionCausingRollback() throws CustomException {
        throw new CustomException("test checked exception that will cause rollback because of rollbackFor field");
    }
    }

     ```
JUnit test - @Transaction
-----------------------
1. Default rollback policy in @Test methods annotated with @Transactional is
   always rollback. This means that after test execution transaction will always be
   rolled back. The reason for this is that each test method should be able to
   change state of database or call other classes that will change state of the
   database, however for the tests to be repeatable, changes should be reverted
   after @Test method execution.

2. You can change this behavior by using @Rollback annotation set to false.
3. Example
   ````java
   @RunWith(SpringJUnit4ClassRunner.class)
   @ContextConfiguration(classes = {Runner.class})
   public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    @Transactional
    public void shouldRollbackTransaction() {
        employeeService.methodWithTransaction();

        // ...
    }

    @Test
    @Transactional
    @Rollback(false)
    public void shouldNotRollbackTransaction() {
        employeeService.methodWithTransaction();

        // ...
    }
    }
   ````

Why is the term "unit of work" so important and why does JDBC AutoCommit violate this pattern 
------------------------
1. Unit of work is a generic term to describe, set of tasks that are performing some
   changes on the data, with assumption that all changes needs to be performed, or
   no changes should be performed at all.
2. In Relational Databases, Unit of Work can be represented by Database
   Transaction, which Atomic nature describes “all-or-nothing” behavior described
   above
3. In context of JPA/Hibernate, Unit of Work tracks all changes made to the Data
   Objects representing entries in the database, and once done, ORM figures out all
   changes that needs to be applied to the database. This way amount of calls to
   the database can be minimized by aggregating all changes into one call.
4. JDBC AutoCommit violates Unit of Work, because it makes every SQL statement being
   invoked in a separate transaction that is committed after SQL is executed, this makes
   impossible to implement Unit of Work consisting of multiple SQL operations.
5. Here Autocommit means not havings @Transaction annotation on database calls
   1. Example
      ![img_8.png](img_8.png)



Spring - Working with JPA
1. Following steps are required to work with JPA in Spring Framework:
   1. Declare maven dependencies:
      1. JPA API - javax.persistence:javax.persistence-api
      2. Spring ORM - org.springframework:spring-orm
      3. ORM of your choice, for example - org.hibernate:hibernate-core
      4. Database Driver, for example - org.hsqldb: hsqldb
      5. Optionally, but recommended, Spring Data JPA - org.springframework.data:spring-data-jpa
   2. Define DataSource Bean
   3. Define PlatformTransactionManager, in case of JPA JpaTransactionManager
   4. Define EntityManagerFactoryBean
      1. LocalContainerEntityManagerFactoryBean for standalone application
      2. EntityManagerFactory from JNDI
      3. LocalEntityManagerFactoryBean for Test purposes
   5. Define @Entity classes with at least on @Id field
   6. Define DAO classes, or use Spring Data JPA Repositories
   7. Dependicies
      ![img_9.png](img_9.png)
   8. Example
      ```java
      //config
      @Configuration
      @EnableJpaRepositories(basePackages = {"com.spring.professional.exam.tutorial.module03.question21.dao"})
      public class JpaConfiguration {
      @Bean
      @Autowired
      public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(dataSource);
      em.setPackagesToScan("com.spring.professional.exam.tutorial.module03.question21.ds");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
       }

       @Bean
       public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
           JpaTransactionManager transactionManager = new JpaTransactionManager();
           transactionManager.setEntityManagerFactory(emf);

           return transactionManager;
         }
        }
      @Configuration
      @EnableTransactionManagement
      public class DataSourceConfiguration {

          @Bean
          public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setScriptEncoding("UTF-8")
                .addScript("schema.sql")
                .build();
          }
       }
      //dao
      public interface EmployeeDao extends CrudRepository<Employee, Integer> {
          }
      //pojo
      public class Employee {
      @Id
      private int id;
      private String firstName;
      private String lastName;
      private String email;
      private String phoneNumber;
      private Date hireDate;
      private float salary;

      @SuppressWarnings("unused")
      public Employee() {
      }
      }
     ```
   
JPA and JBCD Under same transaction
------
1. JPA in Spring uses JpaTransactionManager, which supports cases when
DataSource is used directly, so it allows mixing JPA and JDBC code under one
transaction.

PlatformTransactionManager
--------------------------
1. JPA can work with following transaction managers:
   1. JpaTransactionManager – recommended when working with one database and one Entity
      Manager
   2. JtaTransactionManager – recommended when working with multiple databases and Entity
      Managers, or when working with multiple databases and other transactional resources, for example
      one transaction needs to span Database and JMS Topi
   3. Example
      ![img_10.png](img_10.png)
   4. Usage of JpaTransactionManager in case of multiple Databases / Transactional Resources / Entity
      Managers will cause each transaction, span only one resource, this is why JtaTransactionManager is
      required in this case.
   5. Multiple Databases/Entity Managers Scenario with incorrectly used JpaTransactionManager
      for this case use JtaTransactionManager
   6. Example
      ![img_11.png](img_11.png)

Spring Boot Auto Config - JPA
-----------------------------
1. Spring Boot simplifies JPA setup by:
   1. Providing spring-boot-starter-data-jpa dependency which includes all required
      dependencies
   2. Providing auto-configuration for JPA
   3. Automatically defines PlatformTransactionManager, EntityManager and other
      required beans
   4. Allows Data Source to be configured via properties
   5. Provides out-of-the-box support for Hikari Connection Pool
   6. Provides default properties to JPA
   7. Automatically creates DAO beans for Repositories
   8. Example [SpringBootJPA/src/main/java/com/raghu/springboot/jpa](SpringBootJPA/src/main/java/com/raghu/springboot/jpa)
   

Repository interface - Spring
---------------------------------
1. Repository interface is a Java interface that describes Dao with expected behaviors, based on which
   Spring Data will automatically generate Dao logic. Repository interface takes Domain Class and ID of
   type to manage.
2. Custom Repository interface needs to extend one of following interface:
   1. Repository – basic marker repository
   2. CrudRepository – adds generic methods for CRUD operations
   3. PagingAndSortingRepository – adds findAll methods for paging/sorting
   4. JpaRepository – JPA specific extension of Repository
   5. To define Repository interface, you need to follow those steps:
      1. Create Java Interface that extends one of: Repository, CrudRepository,
         PagingAndSortingRepository, JpaRepository
      2. Create class with @Entity annotation
      3. Inside @Entity class, create a simple primary key annotated with @Id annotation or create class
         that will represent complex key annotated with @EmbeddedId annotation at field level and
         @Embeddable at key class definition level
      4. Use @EnableJpaRepositories to point out package to scan for Repositories
      5. Repository interface is an interface, not a class for Spring Data to be able to use JDK Dynamic Proxy to intercept all calls to repository and also to allow creation of custom base repositories for every Dao based on SimpleJpaRepository configured at @EnableJpaRepositories level.
   6. Example
      ```java
      public interface EmployeeDao extends CrudRepository<Employee, Integer> {
         Employee findByEmail(String email);

         List<Employee> findByLastName(String lastName);

         List<Employee> findBySalaryBetween(float min, float max);
         }
      
      @Entity
      @ToString
      public class Employee {
      @Id
      private int id;
      }
      
      //Framework generates Daologic automatically based interfacesfrom package pointed in //@EnableJpaRepositories
      @Configuration
      @EnableJpaRepositories(basePackages = {"com.spring.professional.exam.tutorial.module03.question25.dao"})
      public class JpaConfiguration { 
      }

        ```

naming convention for finder methods in a Repository interface
----------------------------------------------------------------
1. Syntax
   1. `find[limit]By[property/properties expression][comparison][ordering operator]`
   2. `limit` – result of the query can be limited by usage of first/top keyword
      1. Examples
         1. findFirst10ByLastname
         2. findFirstByOrderByLastnameAsc
         3. findTop3ByLastname
         4. findTopByOrderByAgeDesc
   3. `property/properties expression` - result will be filtered based on property of entity, multiple
      properties can be used with usage of `And, Or keyword`
      1. Examples
         1. findByLastnameAndFirstname
         2. findByLastnameOrFirstname
         3. findByFirstname
   4. `comparison` – comparison mode can be specified after specifying property used for filtering
      1. Examples
         1. findByFirstnameEquals
         2. findByStartDateBetween
         3. findByAgeLessThan, findByAgeLessThanEqual
         4. findByAgeGreaterThan, findByAgeGreaterThanEqual
         5. findByStartDateBefore, findByStartDateAfter
         6. findByStartDateBefore, findByStartDateAfter
         7. findByAgeIsNull, findByAgeIsNotNull
         8. findByFirstnameLike, findByFirstnameNotLike
         9. findByFirstnameStartingWith, findByFirstnameEndingWith
         10. findByFirstnameContaining
         11. findByLastnameNot
         12. findByAgeIn(Collection<Age> ages), findByAgeNotIn(Collection<Age> ages)
         13. findByActiveTrue, findByActiveFalse
         14. findByFirstnameIgnoreCase
   5. `ordering operator` – optionally you can specify ordering operator at the end of method name
      1. Example
         1. findByLastnameOrderByFirstnameAsc
         2. findByLastnameOrderByFirstnameDesc
   6. Example
      ```java
      //dao 
      public interface EmployeeDao extends CrudRepository<Employee, EmployeeKey> {
      Employee findByFirstNameAndLastName(String firstName, String lastName);

      List<Employee> findTop3ByOrderBySalaryDesc();

      List<Employee> findByHireDateBetween(Date min, Date max);

      List<Employee> findByOrderByHireDateDesc();
      }
      
      //pojo
      @Entity
      @ToString
      public class Employee {
      @EmbeddedId
      private EmployeeKey employeeKey;
      private String email;
      private String phoneNumber;
      private Date hireDate;
      private float salary;

      @SuppressWarnings("unused")
      public Employee() {
          }
      }
      
      @Embeddable
      public class EmployeeKey implements Serializable {
      private String firstName;
      private String lastName;

          @SuppressWarnings("unused")
          public EmployeeKey() {
          }
      }
       ```

How are Spring Data repositories implemented by Spring at runtime
-------------------------------------
1. Spring Repositories are implemented at runtime by SimpleJpaRepository by default
2. When application context is starting up, Spring will scan for all classes annotated with
   @Configuration. When @Configuration class with @EnableJpaRepositories will be
   detected, JpaRepositoriesRegistrar with JpaRepositoryConfigExtension will be used
   to create beans for repositories in packages pointed out by basePackages field in
   @EnableJpaRepositories. JpaRepositoryFactoryBean will use
   JpaRepositoryFactory to create beans based on bean definitions and by default will create
   instance of SimpleJpaRepository class for each Repository interface.
3. Class used for implementation of Repository interface can be customized on:
   1. Global level, by using repositoryBaseClass field from @EnableJpaRepositories
      annotation - 
      1. Example
         ```java
         @Configuration
         @EnableJpaRepositories(
         repositoryBaseClass = CustomBaseJpaRepository.class,
         basePackages = {"com.spring.professional.exam.tutorial.module03.question28.dao"}
         )
         public class JpaConfiguration { 
         ...
         }
         
         //Custom class
         public class CustomBaseJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

         private final EntityManager entityManager;

         CustomBaseJpaRepository(JpaEntityInformation entityInformation, EntityManager entityManager) {
         super(entityInformation, entityManager);
         this.entityManager = entityManager;
         }

         public Employee findByFirstNameAndLastName(String firstName, String lastName) {
         System.out.println("Starting custom implementation of findByFirstNameAndLastName from       CustomBaseJpaRepository...");

              CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

              CriteriaQuery<Employee> criteria = criteriaBuilder.createQuery(Employee.class);
              Root<Employee> employeeRoot = criteria.from(Employee.class);
              criteria.where(
                      criteriaBuilder.and(
                        criteriaBuilder.equal(employeeRoot.get("lastName"), lastName),
                        criteriaBuilder.equal(employeeRoot.get("firstName"), firstName)
                      )
              );

              return entityManager.createQuery(criteria).getSingleResult();
         }
         }


            ```
   2. Single Dao/Repository by creating separate interface and Impl class for behavior that you
      want to customize.
      1. For Custom dao respective custom dao methods will be called, rest of the regular dao methods implemented by Spring SimpleJpaRepository methods will be called.
         1. Example
            ```java
            //custom dao
            public interface CustomEmployeeDao extends CrudRepository<Employee, Integer>, CustomEmployeeFindRepository {
                }
            public interface CustomEmployeeFindRepository {
            Employee findByFirstNameAndLastName(String firstName, String lastName);
            }
         
            public class CustomEmployeeFindRepositoryImpl implements CustomEmployeeFindRepository {
            @Autowired
            private EntityManager entityManager;

            @Override
            public Employee findByFirstNameAndLastName(String firstName, String lastName) {
            System.out.println("Starting custom implementation of findByFirstNameAndLastName from CustomEmployeeFindRepositoryImpl...");

                 CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

                 CriteriaQuery<Employee> criteria = criteriaBuilder.createQuery(Employee.class);
                 Root<Employee> employeeRoot = criteria.from(Employee.class);
                 criteria.where(
                   criteriaBuilder.and(
                           criteriaBuilder.equal(employeeRoot.get("firstName"), firstName),
                           criteriaBuilder.equal(employeeRoot.get("lastName"), lastName)
                   )
                 );

                 return entityManager.createQuery(criteria).getSingleResult();
            }
            }
            //regular dao
            public interface EmployeeDao extends CrudRepository<Employee, Integer> {
             Employee findByFirstNameAndLastName(String firstName, String lastName);
                 }
             ```


@Query
-------
1. @Query annotation can be used on top of Repository method, and with it you can specify
   query that should be used by JPA. When declaring one on top of finder method, specified
   query will be used, instead of generating one automatically based on finder method name.
2. Using @Query annotation allows you to achieve more control and flexibility of the JPA
   query that will be executed.
   1. Example
      ```java
      @Query("select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
      Employee findByFirstNameAndLastName(String firstName, String lastName);
      
       ```


      