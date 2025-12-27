INSERT INTO sitter (contact_name, email, password, company_name, address, location, time_of_opening, time_of_closing, charges_per_hour, enable, logo, role)
VALUES
    ('John Doe', 'john.doe@example.com', 'password123', 'ABC Sitters', '123 Main Street, Connaught Place, Delhi', ST_GeomFromText('POINT(28.6139 77.2090)', 4326), '09:00:00', '18:00:00', 100.0, true, 'logo.png', 0),
    ('Jane Smith', 'jane.smith@example.com', 'securepass', 'XYZ Care', '456 Park Avenue, Karol Bagh, Delhi', ST_GeomFromText('POINT(28.6239 77.2190)', 4326), '10:00:00', '19:00:00', 150.0, true, 'logo2.png', 0),
    ('Rahul Kumar', 'rahul.kumar@example.com', 'mypassword', 'PQR Services', '789 Nehru Place, Delhi', ST_GeomFromText('POINT(28.5575 77.2417)', 4326), '08:00:00', '17:00:00', 120.0, true, 'logo3.png', 0),
    ('Anjali Sharma', 'anjali.sharma@example.com', 'strongpass', 'LMN Group', '321 MG Road, Saket, Delhi', ST_GeomFromText('POINT(28.5314 77.2155)', 4326), '09:30:00', '18:30:00', 130.0, true, 'logo4.png', 0),
    ('Amit Verma', 'amit.verma@example.com', 'mypassword123', 'DEF Sitters', '654 Lajpat Nagar, Delhi', ST_GeomFromText('POINT(28.5739 77.2282)', 4326), '11:00:00', '20:00:00', 110.0, true, 'logo5.png', 0),
    ('Priya Gupta', 'priya.gupta@example.com', 'secure123', 'GHI Care', '987 Defence Colony, Delhi', ST_GeomFromText('POINT(28.5619 77.2356)', 4326), '12:00:00', '21:00:00', 140.0, true, 'logo6.png', 0),
    ('Vikas Singh', 'vikas.singh@example.com', 'mysecurepass', 'JKL Services', '159 Greater Kailash, Delhi', ST_GeomFromText('POINT(28.5486 77.2511)', 4326), '08:30:00', '17:30:00', 160.0, true, 'logo7.png', 0),
    ('Neha Chopra', 'neha.chopra@example.com', 'passwordsecure', 'MNO Group', '753 Vasant Kunj, Delhi', ST_GeomFromText('POINT(28.5425 77.1865)', 4326), '07:00:00', '16:00:00', 170.0, true, 'logo8.png', 0),
    ('Ravi Malhotra', 'ravi.malhotra@example.com', 'ravipass', 'PQR Sitters', '357 Chanakyapuri, Delhi', ST_GeomFromText('POINT(28.5944 77.2136)', 4326), '10:30:00', '19:30:00', 180.0, true, 'logo9.png', 0),
    ('Sneha Agarwal', 'sneha.agarwal@example.com', 'snehapass', 'XYZ Services', '852 Hauz Khas, Delhi', ST_GeomFromText('POINT(28.5456 77.2056)', 4326), '09:00:00', '18:00:00', 190.0, true, 'logo10.png', 0);

