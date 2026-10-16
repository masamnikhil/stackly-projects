-- Retrieve all accounts with balance greater than 20,000.

SELECT *
FROM accounts
WHERE balance > 20000;

-- Find customers who live in Chennai.

SELECT *
FROM customers
WHERE city = 'Chennai';

-- Display accounts with balance between 20,000 and 50,000.

SELECT *
FROM accounts
WHERE balance BETWEEN 20000 AND 50000;

-- Find customers whose names start with 'J'.

SELECT *
FROM customers
WHERE name LIKE 'J%';

-- Retrieve accounts of type 'Savings' or 'Current'.

SELECT *
FROM accounts
WHERE account_type IN ('Savings', 'Current');

-- Display accounts that are not 'Savings'.

SELECT *
FROM accounts
WHERE account_type != 'Savings';

-- Find customers whose names contain the letter 'a'.

SELECT *
FROM customers
WHERE name LIKE '%a%';

-- Retrieve accounts with balance less than or equal to 30,000.

SELECT *
FROM accounts
WHERE balance <= 30000;

-- Find customers who are not from Madurai.

SELECT *
FROM customers
WHERE city != 'Madurai';

-- Display accounts where balance is not between 10,000 and 40,000.

SELECT *
FROM accounts
WHERE balance NOT BETWEEN 10000 AND 40000;

-- Retrieve customers whose names end with 'i'.

SELECT *
FROM customers
WHERE name LIKE '%i';

-- Find accounts with balance equal to 50,000.

SELECT *
FROM accounts
WHERE balance = 50000;

-- Display customers whose city is either Chennai or Salem.

SELECT *
FROM customers
WHERE city IN ('Chennai', 'Salem');

-- Find accounts with balance greater than 10,000 and less than 40,000.

SELECT *
FROM accounts
WHERE balance > 10000
  AND balance < 40000;
  
-- Retrieve accounts where account type is not 'Current'.  
  
SELECT *
FROM accounts
WHERE account_type NOT IN ('Current'); 

-- Display all accounts sorted by balance in descending order. 

SELECT *
FROM accounts
ORDER BY balance DESC;

-- List customers sorted alphabetically by name.

SELECT *
FROM customers
ORDER BY name ASC;

-- Display accounts sorted by account type and then by balance (descending).

SELECT *
FROM accounts
ORDER BY account_type ASC, balance DESC;

-- Aggregate functions  

-- Find the total balance of all accounts.

SELECT SUM(balance) AS total_balance
FROM accounts;

-- Calculate the average balance of accounts.

SELECT AVG(balance) AS average_balance
FROM accounts;

-- Find the maximum account balance.

SELECT MAX(balance) AS maximum_balance
FROM accounts;

-- Find the minimum account balance.

SELECT MIN(balance) AS minimum_balance
FROM accounts;

-- Count the total number of customers.

SELECT COUNT(*) AS total_customers
FROM customers;

-- Group By & Having

-- Find total balance grouped by account type.

SELECT account_type, SUM(balance) AS total_balance
FROM accounts
GROUP BY account_type; 

-- Find average balance for each account type.

SELECT account_type, AVG(balance) AS average_balance
FROM accounts
GROUP BY account_type;

-- Display account types having average balance greater than 20,000.

SELECT account_type, AVG(balance) AS average_balance
FROM accounts
GROUP BY account_type
HAVING AVG(balance) > 20000;

-- Count the number of accounts for each customer.

SELECT c.customer_id, c.name,
       COUNT(a.account_id) AS total_accounts
FROM customers c
LEFT JOIN accounts a
    ON c.customer_id = a.customer_id
GROUP BY c.customer_id;

-- Display customers having more than one account.

SELECT c.customer_id, c.name,
       COUNT(a.account_id) AS total_accounts
FROM customers c
JOIN accounts a
    ON c.customer_id = a.customer_id
GROUP BY c.customer_id
HAVING COUNT(a.account_id) > 1;

-- Retrieve customer names along with their account balances.

SELECT c.name, a.balance
FROM customers c
JOIN accounts a
    ON c.customer_id = a.customer_id;
    
-- Display all customers and their accounts, including customers without accounts.    
    
SELECT c.customer_id, c.name, c.city,
       a.account_id, a.account_type, a.balance
FROM customers c
LEFT JOIN accounts a
    ON c.customer_id = a.customer_id;    
    
-- Display all accounts and corresponding customer details.

SELECT a.account_id, a.account_type, a.balance,
       c.customer_id, c.name, c.city
FROM accounts a
JOIN customers c
    ON a.customer_id = c.customer_id;    
    
-- Retrieve customer names and account types where balance is greater than 20,000.

SELECT c.name, a.account_type, a.balance
FROM customers c
JOIN accounts a
    ON c.customer_id = a.customer_id
WHERE a.balance > 20000;    

-- List customers with their total balance using JOIN.

SELECT c.customer_id, c.name,
       COALESCE(SUM(a.balance), 0) AS total_balance
FROM customers c
LEFT JOIN accounts a
    ON c.customer_id = a.customer_id
GROUP BY c.customer_id, c.name;

-- Display customer names and balances sorted by balance.

SELECT c.name, a.balance
FROM customers c
JOIN accounts a
    ON c.customer_id = a.customer_id
ORDER BY a.balance ASC;

-- Count the number of accounts for each city using JOIN.

SELECT c.city,
       COUNT(a.account_id) AS total_accounts
FROM customers c
LEFT JOIN accounts a
    ON c.customer_id = a.customer_id
GROUP BY c.city
ORDER BY total_accounts DESC;

-- Find accounts with balance greater than the average balance.

SELECT *
FROM accounts
WHERE balance > (
    SELECT AVG(balance)
    FROM accounts
);

-- Retrieve customers who have accounts

SELECT *
FROM customers
WHERE customer_id IN (
    SELECT customer_id
    FROM accounts
);

-- Find customers who do not have any accounts

SELECT *
FROM customers
WHERE customer_id NOT IN (
    SELECT customer_id
    FROM accounts
);

-- Display account(s) with the maximum balance

SELECT *
FROM accounts
WHERE balance = (
    SELECT MAX(balance)
    FROM accounts
);

-- Find customers whose total balance is greater than 40,000

SELECT *
FROM customers c
WHERE (
    SELECT COALESCE(SUM(a.balance), 0)
    FROM accounts a
    WHERE a.customer_id = c.customer_id
) > 40000;

