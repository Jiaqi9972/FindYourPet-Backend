#!/bin/bash

# Update system packages
apt-get update && apt-get upgrade -y

# Install Docker
apt-get install -y docker.io
systemctl enable docker
systemctl start docker

# Install PostgreSQL
apt-get install -y postgresql-client postgresql
su - postgres -c "createuser -s pet_admin"
su - postgres -c "createdb find_your_pet"
echo "host all  all             0.0.0.0/0               md5" >> /etc/postgresql/14/main/pg_hba.conf
echo "listen_addresses='*'" >> /etc/postgresql/14/main/postgresql.conf
systemctl enable postgresql
systemctl start postgresql

# Install Nginx
apt-get install -y nginx
cat << EOF > /etc/nginx/conf.d/default.conf
server {
    listen 80;
    # Add Nginx configuration for your application later
    # e.g. proxy_pass http://localhost:3000;
}
EOF
systemctl enable nginx
systemctl start nginx