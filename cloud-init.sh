#!/bin/bash

# Redirect all output (stdout and stderr) to a log file
exec > /var/log/init-script.log 2>&1

echo "Starting system setup..."

# Update system packages
echo "Updating system packages..."
apt-get update && apt-get upgrade -y

# Install Docker
echo "Installing Docker..."
apt-get install -y docker.io
systemctl enable docker
systemctl start docker
echo "Docker installation completed."

# Install PostgreSQL and PostGIS
echo "Installing PostgreSQL and PostGIS..."
apt-get install -y postgresql-client postgresql postgis postgresql-14-postgis-3
systemctl enable postgresql
systemctl start postgresql
echo "PostgreSQL and PostGIS installation completed."

# Configure PostgreSQL database and user
echo "Configuring PostgreSQL user and database..."
su - postgres -c "createuser -s pet_admin"
su - postgres -c "createdb find_your_pet"

# Set password for pet_admin
echo "Setting password for pet_admin user..."
su - postgres -c "psql -c \"ALTER USER pet_admin WITH PASSWORD 'pet_admin';\""

# Update PostgreSQL configurations to allow external access with password
echo "Updating PostgreSQL configurations for external access..."
echo "host all  all             0.0.0.0/0               md5" >> /etc/postgresql/14/main/pg_hba.conf
echo "listen_addresses='*'" >> /etc/postgresql/14/main/postgresql.conf
systemctl restart postgresql
echo "PostgreSQL configuration updated."

# Set PostgreSQL user permissions
echo "Configuring PostgreSQL user permissions..."
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
echo "PostgreSQL user permissions configured."

# Install and configure Nginx
echo "Installing and configuring Nginx..."
apt-get install -y nginx
cat << EOF > /etc/nginx/sites-available/default
server {
    listen 80 default_server;
    listen [::]:80 default_server;

    root /var/www/html;
    index index.html index.htm index.nginx-debian.html;

    server_name _;

    location / {
        proxy_pass http://localhost:8088;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    error_page 404 /404.html;
    location = /404.html {
        internal;
    }

    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        internal;
    }

    gzip on;
    gzip_disable "msie6";
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_buffers 16 8k;
    gzip_http_version 1.1;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript application/font-woff2;
}
EOF
# Enable and start Nginx
systemctl enable nginx
systemctl start nginx
echo "Nginx installation and configuration completed."

# Configure firewall rules (iptables)
echo "Configuring firewall rules for HTTP, HTTPS, Application, and PostgreSQL..."
iptables -I INPUT -p tcp --dport 80 -j ACCEPT    # HTTP
iptables -I INPUT -p tcp --dport 443 -j ACCEPT   # HTTPS
iptables -I INPUT -p tcp --dport 8088 -j ACCEPT  # Application
iptables -I INPUT -p tcp --dport 5432 -j ACCEPT  # PostgreSQL
echo "Firewall rules configured."

# Save iptables rules
echo "Saving iptables rules..."
apt-get install -y iptables-persistent
netfilter-persistent save
echo "iptables rules saved."

echo "Initialization completed. All services are set up and running."
