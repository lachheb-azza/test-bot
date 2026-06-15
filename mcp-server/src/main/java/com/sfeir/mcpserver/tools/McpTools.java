package com.sfeir.mcpserver.tools;

import com.sfeir.mcpserver.model.Employee;
import com.sfeir.mcpserver.repository.EmployeeRepository;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class McpTools {

    private final EmployeeRepository employeeRepository;

    public McpTools(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @McpTool(name = "getEmployee", description = "Get information about a given employee")
    public Employee getEmployee(@McpArg(description = "The name of the employee") String name) {
        return employeeRepository.findByName(name);
    }

    @McpTool(description = "Get all employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}