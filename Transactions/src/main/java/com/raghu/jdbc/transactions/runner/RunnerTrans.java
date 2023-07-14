package com.raghu.jdbc.transactions.runner;

import com.raghu.jdbc.transactions.service.EmployeeService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.raghu.jdbc.transactions")
public class RunnerTrans {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RunnerTrans.class);
        context.registerShutdownHook();

        EmployeeService employeeService = context.getBean(EmployeeService.class);
        try {
            employeeService.saveEmployeesWithoutTransaction();
        } catch (Exception e) {
            System.out.println("Exception during saving employees: " + e.getMessage());
        }
        employeeService.printEmployees();
        employeeService.deleteAllEmployees();

        try {
            employeeService.saveEmployeesInTransaction();
        } catch (Exception e) {
            System.out.println("Exception during saving employees: " + e.getMessage());
        }
        employeeService.printEmployees();
        employeeService.deleteAllEmployees();
    }
    }
