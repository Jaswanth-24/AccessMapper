**# AccessMapper**

**Project Overview**

Access Mapper is a backend service that connects to the GitHub API to analyze repository access within an organization. It retrieves repositories and their collaborators, aggregates the data, and generates a report showing which users have access to which repositories. The service exposes REST API endpoints that return the access report in JSON format.

**Tech Stack**: Java 17, Springboot 3.5.11, Maven, GitHub API

**How to Run the Project**

1. Clone the Repository
   git clone https://github.com/Jaswanth-24/AccessMapper.git
   cd AccessMapper

   
3. Set GitHub Token
   Create a GitHub Personal Access Token with repo and read:org permissions and set it as an environment variable
   run this command to set environment variable in same command prompt:
   **set GITHUB_TOKEN=paste_your_github_token**

   
4. Run the Application
   **mvn spring-boot:run**
   
   The application will start on: **http://localhost:8080**

   
5. Test the API
   
   1. Get User Organizations: Returns the list of organizations the authenticated GitHub user belongs to.

   GET /api/orgs

   http://localhost:8080/api/orgs
  
   2. Get Organization Repositories: Returns all repositories that belong to a specific organization.

  GET /api/orgs/{org}/repos
  
  http://localhost:8080/api/orgs/{org}/repos
  
  3. Generate Access Report: Returns a report showing which users have access to which repositories in an organization.

  GET /api/access-report?org={org}
  
  http://localhost:8080/api/access-report?org={org}

  **NOTE**: {org}: organization name
