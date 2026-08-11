# Beginner-Friendly Deployment Runbook
**End-to-End DevOps Process Note for Multi-App Microservices Architecture**

## Architecture Overview

This architecture deploys a production-ready, modular, multi-application environment on a single AWS EC2 instance. Applications are decoupled into independent directories within a single **Monorepo** (`github-action-projects`), communicating via a shared Docker network while routing external traffic through Nginx and Cloudflare.

```text
                                [ User Browsers ]
                                       │
                                       ▼
                       [ Cloudflare DNS & Proxy (Orange) ]
                      (*.dibyashworhamal.com.np SSL/TLS)
                                       │
                                       ▼ (Ports 80 / 443)
                            [ AWS EC2 Public IP ]
                                       │
                         [ Native Nginx Reverse Proxy ]
                                       │
 ┌──────────────┬──────────────┬───────┴──────┬──────────────┬──────────────┬──────────────┐
 │              │              │              │              │              │              │
(8082)         (8084)         (8083)         (8085)         (5000)         (8090)         (8081)
 │              │              │              │              │              │              │
 ┌───▼────────┐ ┌───▼────────┐ ┌───▼────────┐ ┌───▼────────┐ ┌───▼────────┐ ┌───▼────────┐ ┌───▼────────┐
 │ebs-java-app│ │weather-app │ │wordpress   │ │mern-front  │ │mern-backend│ │phpmyadmin  │ │mongo-express  │
 └───┬────────┘ └────────────┘ └───┬────────┘ └───┬────────┘ └───┬────────┘ └───┬────────┘ └────────────┘
     │ (MySQL)                     │ (MySQL)      │              │ (MongoDB)    │ (MySQL)
     └─────────────────┐           │              │ (API Calls)  │              │
                       ▼           ▼              └─────────────►│              │
              ┌─────────────────────────────┐                    ▼              │
              │ mysql:8.0 (dip_db, wp_db)   │◄──────────────────────────────────┘
              └─────────────────────────────┘                    │
                       ┌─────────────────────────────────────────┘
                       ▼
              ┌─────────────────────────────┐
              │ mongodb:8.0 (mern_db)       │
              └─────────────────────────────┘
              [ Shared Docker Network: devops-shared-network ]
```

## Phase 1: Prerequisites & Account Requirements

Before starting, ensure you have the following accounts and access:

1. **GitHub Repository**: Cloned repo with source code, Dockerfiles, and compose files (`github-action-projects`).
2. **AWS Account**: Access to launch EC2 instances.
3. **Cloudflare Account**: Domain (`dibyashworhamal.com.np`) added with active nameservers.
4. **Docker Hub Account**: For storing public/private Docker images.

---

## Phase 2: AWS EC2 Server Preparation

### Step 1: Launch EC2 Instance & Configure Firewall
1. Go to **AWS Console** ➔ **EC2** ➔ **Launch Instance**.
2. Select **Ubuntu 24.04 LTS**.
3. Choose instance type (e.g., `t2.micro` or `t3.small`).
4. Download your Key Pair (`.pem` file).
5. In **Security Groups**, allow these Inbound Rules:
   * **SSH (Port 22)** ➔ Source: `0.0.0.0/0`
   * **HTTP (Port 80)** ➔ Source: `0.0.0.0/0`
   * **HTTPS (Port 443)** ➔ Source: `0.0.0.0/0`

### Step 2: Expand Disk Storage (Prevent Disk Full Errors)
Default EC2 disk size is 8GB, which fills up quickly with Docker images.
1. In AWS Console ➔ **EC2** ➔ **Volumes** ➔ Modify volume size from `8` to **`20` GB**.
2. SSH into your EC2 server (`ssh -i key.pem ubuntu@YOUR_EC2_PUBLIC_IP`).
3. Run these commands to apply the new disk size:
   ```bash
   sudo growpart /dev/xvda 1 2>/dev/null || sudo growpart /dev/nvme0n1 1
   sudo resize2fs /dev/xvda1 2>/dev/null || sudo resize2fs /dev/nvme0n1p1
   df -h /
   ```

### Step 3: Install Docker, Nginx, & Setup Network
Run these commands on your EC2 terminal:
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu
newgrp docker

# Install Nginx
sudo apt install nginx -y
sudo systemctl enable --now nginx

# Pre-create the shared Docker network required by all applications
docker network create devops-shared-network
```

---

## Phase 3: GitHub Secrets Configuration

Go to GitHub Repo ➔ **Settings** ➔ **Secrets and variables** ➔ **Actions** ➔ **New repository secret**.

Create the following 10 secrets:

| Secret Name | Description / Value |
| :--- | :--- |
| `EC2_HOST` | AWS EC2 Public IPv4 Address (e.g., `54.210.xx.xx`) |
| `EC2_USERNAME` | `ubuntu` |
| `EC2_SSH_KEY` | Full content of your `.pem` key file |
| `DOCKERHUB_USERNAME` | Your Docker Hub account username |
| `DOCKERHUB_PASSWORD` | Your Docker Hub Access Token / Password |
| `DATABASE_ENV_FILE` | Raw `.env` content for `database/` |
| `JAVA_ENV_FILE` | Raw `.env` content for `java-app/` |
| `NODE_ENV_FILE` | Raw `.env` content for `node.js-app/` |
| `WORDPRESS_ENV_FILE` | Raw `.env` content for `wordpress/` |
| `MERN_ENV_FILE` | Raw `.env` content for `mern-app/` |

---

## Phase 4: Deploying Databases (First Deployment)

Databases must always be deployed **before** applications so that the shared database containers and network exist.

### Step 1: Run Database Pipeline
1. Go to GitHub Repo ➔ **Actions** tab.
2. Select **Database Deployment Pipeline** on the left menu.
3. Click **Run workflow** ➔ Select `main` branch ➔ Click **Run workflow**.
4. Wait for the green checkmark (`✔`).

### Step 2: Initialize WordPress Database in phpMyAdmin
1. Temporarily access phpMyAdmin via EC2 IP on port 8090 (`http://YOUR_EC2_PUBLIC_IP:8090`).
2. Log in using MySQL credentials (`dip_hamal` / `dip_hamal123`).
3. Click **Databases** tab ➔ Create a new database named **`wp_db`**.

---

## Phase 5: Deploying Applications via CI/CD

Trigger application pipelines manually via the GitHub Actions UI or by pushing code changes:

### Workflow Execution Strategy:
* **Java App Pipeline (`java-cicd.yml`)**: Compiles Maven code, builds Docker image, pushes to GitHub Container Registry (GHCR), and deploys to EC2.
* **Node.js Weather App Pipeline (`node-cicd.yml`)**: Builds Node image, pushes to Docker Hub, and deploys to EC2.
* **WordPress Pipeline (`wordpress-cicd.yml`)**: Validates compose syntax, pulls WordPress image on EC2, and launches container.
* **MERN Stack Pipeline (`mern-cicd.yml`)**: Builds React Frontend and Node Backend images, pushes both to Docker Hub, and deploys to EC2.

*Action*: Go to GitHub **Actions** tab and click **Run workflow** for each application pipeline.

---

## Phase 6: Native Nginx Reverse Proxy Setup

Nginx acts as the single entry point, taking traffic from ports 80/443 and proxying to container ports on localhost.

### Step 1: Create Nginx Site Configuration
On your EC2 terminal, create the configuration file:
```bash
sudo nano /etc/nginx/sites-available/dibyashworhamal.conf
```

Add server blocks mapping domain names to container local host ports:
* `ebs.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8082`
* `pma.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8090`
* `mongo.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8081`
* `wp.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8083`
* `weather.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8084`
* `mern.dibyashworhamal.com.np` ➔ `http://127.0.0.1:8085`
* `mern-api.dibyashworhamal.com.np` ➔ `http://127.0.0.1:5000`

### Step 2: Enable & Test Nginx
```bash
# Enable site configuration
sudo ln -s /etc/nginx/sites-available/dibyashworhamal.conf /etc/nginx/sites-enabled/

# Remove default welcome page configuration
sudo rm -f /etc/nginx/sites-enabled/default

# Test syntax and reload
sudo nginx -t
sudo systemctl reload nginx
```

---

## Phase 7: Cloudflare DNS & SSL Configuration

### Step 1: Configure Cloudflare DNS A Records
In Cloudflare Dashboard ➔ Select Domain (`dibyashworhamal.com.np`) ➔ **DNS Records**:

Add **A Records** with **Proxied (Orange Cloud)** status pointing to your EC2 Public IP:
* `ebs` ➔ `YOUR_EC2_PUBLIC_IP`
* `pma` ➔ `YOUR_EC2_PUBLIC_IP`
* `mongo` ➔ `YOUR_EC2_PUBLIC_IP`
* `wp` ➔ `YOUR_EC2_PUBLIC_IP`
* `weather` ➔ `YOUR_EC2_PUBLIC_IP`
* `mern` ➔ `YOUR_EC2_PUBLIC_IP`
* `mern-api` ➔ `YOUR_EC2_PUBLIC_IP`

> **Note on Subdomain Rule**: Use single-level subdomains (e.g., `mern-api` instead of `api.mern`). Cloudflare's free SSL wildcard covers `*.domain.com` (1 level deep). Double subdomains (`*.*.domain.com`) trigger `ERR_SSL_VERSION_OR_CIPHER_MISMATCH`.

### Step 2: Install Certbot & Request SSL Certificates
On your EC2 terminal, install Certbot and request SSL certificates:
```bash
# Install Certbot
sudo apt update
sudo apt install certbot python3-certbot-nginx -y

# Obtain Let's Encrypt SSL Certificates for all domains
sudo certbot --nginx \
  --agree-tos \
  --email hamaldivyashwor2057@gmail.com \
  --redirect \
  -d ebs.dibyashworhamal.com.np \
  -d pma.dibyashworhamal.com.np \
  -d mongo.dibyashworhamal.com.np \
  -d wp.dibyashworhamal.com.np \
  -d weather.dibyashworhamal.com.np \
  -d mern.dibyashworhamal.com.np \
  -d mern-api.dibyashworhamal.com.np
```

### Step 3: Enable Cloudflare Full (Strict) SSL
In Cloudflare Dashboard ➔ **SSL/TLS** ➔ **Overview** ➔ Change encryption mode to **Full (Strict)**.

---

## Phase 8: Operations & Maintenance Checklist

### 1. SSL Auto-Renewal (90-Day Automation)
Certbot installs an automatic background timer (`certbot.timer`) that checks and renews certificates automatically before they expire.
* **Verify Timer Status**: `sudo systemctl status certbot.timer`
* **Test Auto-Renewal Execution**: `sudo certbot renew --dry-run`

### 2. Disk Space Cleanup Commands
Run periodically if EC2 disk usage gets high:
```bash
# Prune unused Docker objects
docker system prune -a --volumes -y

# Limit log sizes
sudo journalctl --vacuum-size=100M
```

---

## Final Verification Checklist

Navigate to each domain in your browser to verify HTTPS connectivity:

* ☕ **Java Event Booking System**: `https://ebs.dibyashworhamal.com.np`
* 🗄️ **phpMyAdmin Interface**: `https://pma.dibyashworhamal.com.np`
* 🍃 **Mongo Express Interface**: `https://mongo.dibyashworhamal.com.np`
* 📝 **WordPress Site**: `https://wp.dibyashworhamal.com.np`
* 🌤️ **Node.js Weather Application**: `https://weather.dibyashworhamal.com.np`
* ⚛️ **MERN Stack Frontend**: `https://mern.dibyashworhamal.com.np`
* ⚙️ **MERN Stack Backend API**: `https://mern-api.dibyashworhamal.com.np`