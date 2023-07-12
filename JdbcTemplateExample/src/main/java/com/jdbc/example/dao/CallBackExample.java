package com.jdbc.example.dao;

import com.jdbc.example.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CallBackExample {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource)
    {
        jdbcTemplate=new JdbcTemplate(dataSource);
    }

    //using RowMapper Interface
    public List<Employee> getEmployeeList()
    {
        return jdbcTemplate.query("select employee_id, first_name, last_name, email, phone_number, hire_date, salary from employee", new RowMapper<Employee>() {
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

    //using RowCallBackHandler
    public float findAverageSalaryRowByRow() {
        AverageSalaryRowCallbackHandler averageSalaryRowCallbackHandler = new AverageSalaryRowCallbackHandler();

        jdbcTemplate.query(
                "select salary from employee",
                averageSalaryRowCallbackHandler
        );

        return averageSalaryRowCallbackHandler.getAverageSalary();
    }

    //using ResultSetExatractor
    public Float findAverageSalaryCalculatedOnEntireResultSet() {
        return jdbcTemplate.query(
                "select salary from employee",
                new AverageSalaryResultSetExtractor()
        );
    }

    private static class AverageSalaryResultSetExtractor implements ResultSetExtractor<Float> {
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



}
