echo "CREATE DATABASE IF NOT EXISTS $1" | mysql -uroot 
mysql -uroot $1< ./create_tables.sql 
