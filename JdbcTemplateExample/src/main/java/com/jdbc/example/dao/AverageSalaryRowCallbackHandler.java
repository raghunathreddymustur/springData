package com.jdbc.example.dao;

import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AverageSalaryRowCallbackHandler implements RowCallbackHandler {

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
