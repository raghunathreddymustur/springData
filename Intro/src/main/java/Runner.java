import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import service.EmployeeReportService;

@ComponentScan(basePackages = {"config","service","dao"})
public class Runner {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Runner.class);
        context.registerShutdownHook();
        EmployeeReportService employeeReportService = context.getBean(EmployeeReportService.class);
        employeeReportService.printReport();
    }
}
