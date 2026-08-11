#!/bin/bash
set -e

echo " 1. Updating System & Installing Certbot "
sudo apt update
sudo apt install certbot python3-certbot-nginx -y

echo " 2. Requesting Let's Encrypt SSL Certificates "
# Certbot will verify domain ownership, issue SSL certificates,
# and automatically modify /etc/nginx/sites-available/dibyashworhamal.conf
sudo certbot --nginx \
  --non-interactive \
  --agree-tos \
  --email hamaldivyashwor2057@gmail.com \
  --redirect \
  -d ebs.dibyashworhamal.com.np \
  -d pma.dibyashworhamal.com.np \
  -d mongo.dibyashworhamal.com.np \
  -d wordpress.dibyashworhamal.com.np \
  -d weatherapp.dibyashworhamal.com.np \
  -d mern.dibyashworhamal.com.np \
  -d mern-api.dibyashworhamal.com.np

echo " 3. Testing Nginx Configuration & Reloading "
sudo nginx -t
sudo systemctl reload nginx

echo " 4. Testing Automatic Renewal "
# Simulates a renewal run to verify certbot.timer works
sudo systemctl status certbot.timer
sudo certbot renew --dry-run

echo " SUCCESS! SSL Certificates Installed Successfully! "