#!/bin/bash

# Update system packages
apt-get update && apt-get upgrade -y

# Install Docker
apt-get install -y docker.io
systemctl enable docker
systemctl start docker

# Install PostgreSQL and PostGIS
apt-get install -y postgresql-client postgresql postgis postgresql-14-postgis-3
systemctl enable postgresql
systemctl start postgresql

# Configure PostgreSQL database and user
su - postgres -c "createuser -s pet_admin"
su - postgres -c "createdb find_your_pet"

# Set password for pet_admin
su - postgres -c "psql -c \"ALTER USER pet_admin WITH PASSWORD 'pet_admin';\""

# Update PostgreSQL configurations to allow external access with password
echo "host all  all             0.0.0.0/0               md5" >> /etc/postgresql/14/main/pg_hba.conf
echo "listen_addresses='*'" >> /etc/postgresql/14/main/postgresql.conf
systemctl restart postgresql

# Set PostgreSQL user permissions
sudo -u postgres psql << EOF
-- Connect to the postgres database
\c postgres

-- Revoke superuser privileges if any
ALTER USER pet_admin NOSUPERUSER;

-- Grant connect privilege
GRANT CONNECT ON DATABASE find_your_pet TO pet_admin;

-- Switch to the find_your_pet database
\c find_your_pet

-- Grant permissions on public schema
GRANT USAGE ON SCHEMA public TO pet_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pet_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO pet_admin;

-- Set default privileges for future tables and sequences
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO pet_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO pet_admin;

-- Enable PostGIS extension
CREATE EXTENSION postgis;

-- Verify PostGIS installation
SELECT PostGIS_Version();

EOF

# Install and configure Nginx
apt-get install -y nginx
cat << EOF > /etc/nginx/conf.d/default.conf
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://localhost:8088;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Enable and start Nginx
systemctl enable nginx
systemctl start nginx

# Configure firewall rules (iptables)
iptables -I INPUT -p tcp --dport 80 -j ACCEPT    # HTTP
iptables -I INPUT -p tcp --dport 443 -j ACCEPT   # HTTPS
iptables -I INPUT -p tcp --dport 8088 -j ACCEPT  # Application
iptables -I INPUT -p tcp --dport 5432 -j ACCEPT  # PostgreSQL

# Save iptables rules
apt-get install -y iptables-persistent
netfilter-persistent save

echo "Initialization completed. All services are set up and running."
