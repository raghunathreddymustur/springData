package com.raghu.springboot.jpa;

import com.raghu.springboot.jpa.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootRunner implements CommandLineRunner {
    @Autowired
    private EmployeeService employeeService;

    public static void main(String[] args) {
        SpringApplication.run(BootRunner.class, args);
    }
    @Override
    public void run(String... args)  {
        employeeService.saveAllEmployees();

        employeeService.listAllEmployees();
    }
}
