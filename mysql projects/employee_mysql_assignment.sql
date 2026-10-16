-- Find the total number of employees in each department.

SELECT department, COUNT(*) AS total_employees
FROM Employee
GROUP BY department;

-- Find the average salary of employees in each department.

SELECT department, AVG(salary) AS average_salary
FROM Employee
GROUP BY department;

-- Display departments having more than one employee.

SELECT department, COUNT(*) AS total_employees
FROM Employee
GROUP BY department
HAVING COUNT(*) > 1;

-- Find the highest salary in each department.

SELECT department, MAX(salary) AS highest_salary
FROM Employee
GROUP BY department;

-- Find the lowest salary in each department.

SELECT department, MIN(salary) AS lowest_salary
FROM Employee
GROUP BY department;

-- Find departments whose average salary is greater than 50,000.

SELECT department, AVG(salary) AS average_salary
FROM Employee
GROUP BY department
HAVING AVG(salary) > 50000;

-- Calculate the total salary expenditure for each department.

SELECT department, SUM(salary) AS total_salary
FROM Employee
GROUP BY department;

-- Display all employees sorted by salary in descending order.

SELECT *
FROM Employee
ORDER BY salary DESC;

-- Display employees sorted first by department and then by salary in descending order.

SELECT *
FROM Employee
ORDER BY department ASC, salary DESC;

-- Find cities that have more than one employee.

SELECT city, COUNT(*) AS total_employees
FROM Employee
GROUP BY city
HAVING COUNT(*) > 1;

-- Find the total salary paid in each city.

SELECT city, SUM(salary) AS total_salary
FROM Employee
GROUP BY city;

-- Display departments ordered by total salary expenditure from highest to lowest.

SELECT department, SUM(salary) AS total_salary
FROM Employee
GROUP BY department
ORDER BY total_salary DESC;

-- Find the number of employees in each department whose salary is greater than 50,000.

SELECT department, COUNT(*) AS employee_count
FROM Employee
WHERE salary > 50000
GROUP BY department;

-- Find the difference between the highest and lowest salary in each department.

SELECT department,
       MAX(salary) AS highest_salary,
       MIN(salary) AS lowest_salary,
       MAX(salary) - MIN(salary) AS salary_difference
FROM Employee
GROUP BY department;

-- Display the top 3 highest-paid employees.

SELECT *
FROM Employee
ORDER BY salary DESC
LIMIT 3;