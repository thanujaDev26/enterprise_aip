package com.copperleaf.asset_invesment_planner.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    DataSource dataSource;

    @GetMapping("/test-db")
    public String testDb() throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection.toString();
    }

}
