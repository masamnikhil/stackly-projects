-- Find the total order amount for each customer.

SELECT c.customer_id, c.customer_name,
       COALESCE(SUM(o.amount), 0) AS total_amount
FROM Customers c
LEFT JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id;

-- Find customers who have placed more than 3 orders.

SELECT c.customer_id, c.customer_name,
       COUNT(o.order_id) AS total_orders
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING total_orders > 3;

-- Find the highest order amount placed by each customer.

SELECT c.customer_name,
       MAX(o.amount) AS highest_order_amount
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id;

-- Display customers sorted by their total purchase amount.

SELECT c.customer_name,
       COALESCE(SUM(o.amount), 0) AS total_purchase
FROM Customers c
LEFT JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
ORDER BY total_purchase DESC;

-- Find customers whose total purchase amount exceeds 10,000.

SELECT c.customer_name,
       SUM(o.amount) AS total_purchase
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING SUM(o.amount) > 10000;

-- Display customer names along with the total number of orders placed.

SELECT c.customer_name,
       COUNT(o.order_id) AS total_orders
FROM Customers c
LEFT JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id;

-- Find the customer who spent the highest amount.

SELECT c.customer_id, c.customer_name,
       SUM(o.amount) AS total_spent
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
ORDER BY total_spent DESC
LIMIT 1;

-- Find the customer who placed the maximum number of orders.

SELECT c.customer_id, c.customer_name,
       COUNT(o.order_id) AS total_orders
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
ORDER BY total_orders DESC
LIMIT 1;

-- Find customers whose average order amount is greater than 2,000.

SELECT c.customer_name,
       AVG(o.amount) AS average_order_amount
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING average_order_amount > 2000;

-- Display the top 5 customers based on total purchase amount.

SELECT c.customer_id, c.customer_name,
       SUM(o.amount) AS total_purchase
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
ORDER BY total_purchase DESC
LIMIT 5;

-- Find the minimum order amount for each customer.

SELECT c.customer_name,
       MIN(o.amount) AS minimum_order_amount
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id;

-- Find customers who have placed orders worth more than 5,000 in total.

SELECT c.customer_id, c.customer_name,
       SUM(o.amount) AS total_purchase
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING total_purchase > 5000;

-- Display customer-wise total orders and total purchase amount.

SELECT c.customer_id, c.customer_name,
       COUNT(o.order_id) AS total_orders,
       COALESCE(SUM(o.amount), 0) AS total_purchase
FROM Customers c
LEFT JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id;

-- Find customers who placed more than 2 orders and spent more than 8,000.

SELECT c.customer_id, c.customer_name,
       COUNT(o.order_id) AS total_orders,
       SUM(o.amount) AS total_purchase
FROM Customers c
INNER JOIN Orders o
ON c.customer_id = o.customer_id
GROUP BY c.customer_id
HAVING total_orders > 2
   AND total_purchase > 8000;