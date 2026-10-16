-- Find the average marks scored by students in each department.

SELECT department, AVG(marks) AS average_marks
FROM Students
GROUP BY department;

-- Find departments whose average marks are above 75.

SELECT department, AVG(marks) AS average_marks
FROM Students
GROUP BY department
HAVING AVG(marks) > 75;

-- Find the highest mark scored in each department.

SELECT department, MAX(marks) AS highest_marks
FROM Students
GROUP BY department;

-- Find the total number of students in each department.

SELECT department, COUNT(*) AS total_students
FROM Students
GROUP BY department;

-- Find departments having more than 5 students.

SELECT department, COUNT(*) AS total_students
FROM Students
GROUP BY department
HAVING total_students > 5;

-- Display departments sorted by average marks in descending order.

SELECT department, AVG(marks) AS average_marks
FROM Students
GROUP BY department
ORDER BY average_marks DESC;

-- Find the top 3 departments based on average marks.

SELECT department, AVG(marks) AS average_marks
FROM Students
GROUP BY department
ORDER BY average_marks DESC
LIMIT 3;

-- Find departments whose average marks are between 70 and 90.

SELECT department, AVG(marks) AS average_marks
FROM Students
GROUP BY department
HAVING average_marks BETWEEN 70 AND 90;

-- Find the total marks scored by students in each department.

SELECT department, SUM(marks) AS total_marks
FROM Students
GROUP BY department;

-- Display departments sorted by the total number of students.

SELECT department, COUNT(*) AS total_students
FROM Students
GROUP BY department
ORDER BY total_students DESC;

-- Find the lowest mark scored in each department.

SELECT department, MIN(marks) AS lowest_marks
FROM Students
GROUP BY department;

-- Find departments where the highest mark is greater than 90.

SELECT department, MAX(marks) AS highest_marks
FROM Students
GROUP BY department
HAVING highest_marks > 90;

-- Find the number of students scoring above 80 in each department.

SELECT department, COUNT(*) AS students_above_80
FROM Students
WHERE marks > 80
GROUP BY department;

-- Find departments where more than 3 students scored above 75.

SELECT department, COUNT(*) AS students_above_75
FROM Students
WHERE marks > 75
GROUP BY department
HAVING students_above_75 > 3;

-- Display departments ordered by highest mark in descending order.

SELECT department, MAX(marks) AS highest_marks
FROM Students
GROUP BY department
ORDER BY highest_marks DESC;