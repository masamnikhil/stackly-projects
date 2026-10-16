-- Retrieve all bookings with fare greater than 400.

SELECT * FROM bookings WHERE fare > 400;

-- Find bookings where status is not 'Confirmed'.

SELECT *
FROM bookings
WHERE status != 'Confirmed';

-- Display trains starting from Chennai.

SELECT *
FROM trains
WHERE source = 'Chennai';

-- Retrieve bookings with fare between 300 and 500.

SELECT *
FROM bookings
WHERE fare BETWEEN 300 AND 500;

-- Find passengers whose names start with 'A'.

SELECT *
FROM bookings
WHERE passenger_name LIKE 'A%';

-- Retrieve train names along with passenger names.

SELECT t.train_name, b.passenger_name
FROM trains t
INNER JOIN bookings b
ON t.train_id = b.train_id
ORDER BY t.train_name;

-- Count number of bookings for each train.

SELECT t.train_name,
       COUNT(b.booking_id) AS total_bookings
FROM trains t
LEFT JOIN bookings b
ON t.train_id = b.train_id
GROUP BY t.train_id
ORDER BY total_bookings DESC;

-- Display train names and total fare collected for each train.

SELECT t.train_name,
       COALESCE(SUM(
           CASE
               WHEN b.status = 'Confirmed' THEN b.fare
               ELSE 0
           END
       ), 0) AS total_fare_collected
FROM trains t
LEFT JOIN bookings b
ON t.train_id = b.train_id
GROUP BY t.train_id, t.train_name
ORDER BY total_fare_collected DESC;

-- Find bookings with fare equal to the highest fare.

SELECT *
FROM bookings
WHERE fare = (
    SELECT MAX(fare)
    FROM bookings);
    
-- Retrieve trains that have more than one booking.

SELECT *
FROM trains
WHERE train_id IN (
    SELECT train_id
    FROM bookings
    GROUP BY train_id
    HAVING COUNT(*) > 1
);





