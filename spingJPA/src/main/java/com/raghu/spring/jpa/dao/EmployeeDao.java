package com.raghu.spring.jpa.dao;

import com.raghu.spring.jpa.poljo.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeDao extends CrudRepository<Employee, Integer> {
}
