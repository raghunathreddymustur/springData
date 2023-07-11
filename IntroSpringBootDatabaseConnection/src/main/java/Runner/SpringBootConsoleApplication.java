package Runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import service.EmployeeReportService;

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
