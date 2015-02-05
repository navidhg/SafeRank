import psycopg2

# Connect to database
conn = psycopg2.connect('dbname=safetydata user=navidhg')
conn.set_session(autocommit=True)
cur = conn.cursor()

# Create table queries
createUserTableQuery = 'CREATE TABLE UserDetail (\
							id SERIAL PRIMARY KEY, email TEXT)'
createDetailTableQuery = 'CREATE TABLE SafetyDetail(\
							userID integer REFERENCES UserDetail(id),\
							sampletime TIMESTAMP,\
							rating INTEGER,\
							latitude NUMERIC,\
							longitude NUMERIC,\
							brightness NUMERIC,\
							PRIMARY KEY(userID, sampletime))'

# Execute creation queries
cur.execute(createUserTableQuery)
cur.execute(createDetailTableQuery)