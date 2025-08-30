-- Add deceased fields to customer table
ALTER TABLE customer 
ADD COLUMN is_deceased BOOLEAN DEFAULT FALSE,
ADD COLUMN deceased_date DATE;

-- Add comments for documentation
COMMENT ON COLUMN customer.is_deceased IS 'Flag indicating if the customer is deceased';
COMMENT ON COLUMN customer.deceased_date IS 'Date when the customer passed away';
