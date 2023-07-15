package com.raghu.springboot.jpa.dao;

import com.raghu.springboot.jpa.pojo.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDao extends CrudRepository<Employee, Integer> {
}
