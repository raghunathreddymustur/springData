package com.jdbc.example.service;

import com.jdbc.example.dao.CallBackExample;
import com.jdbc.example.dao.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeReportService {
    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CallBackExample callBackExample;

    public void printReport() {
        System.out.println("EmployeeDao Report Start");

        System.out.println("EmployeeDao Count = " + employeeDao.findEmployeesCount());
        System.out.println("First Hired EmployeeDao Count = " + employeeDao.findFirstHiredEmployee());
        System.out.println("Highest Salary EmployeeDao Count = " + employeeDao.findEmployeeWithHighestSalary());

        System.out.println("Employees List");
        employeeDao.findEmployees()
                .forEach(System.out::println);

        System.out.println("EmployeeDao Report Stop");

        //CALL BACKS
        System.out.println("Callback RowMapper "+callBackExample.getEmployeeList());
        System.out.println("callback CallBackHandler "+callBackExample.findAverageSalaryRowByRow());
        System.out.println("call back ResultSetExtractor "+callBackExample.findAverageSalaryCalculatedOnEntireResultSet());
    }
}
