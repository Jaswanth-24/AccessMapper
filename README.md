**AccessMapper**

**Project Overview**

Access Mapper is a backend service that connects to the GitHub API to analyze repository access within an organization. It retrieves repositories and their collaborators, aggregates the data, and generates a report showing which users have access to which repositories. The service exposes REST API endpoints that return the access report in JSON format.

**Tech Stack**: Java 17, Springboot 3.5.11, Maven, GitHub API

**How to Run the Project**

1. Clone the Repository
   git clone https://github.com/Jaswanth-24/AccessMapper.git
   
   cd AccessMapper
   
3. Set GitHub Token

   Create a GitHub Personal Access Token with the following permissions:
   
      repo
      read:org

   Set it as an environment variable.

   Windows (Command Prompt): set GITHUB_TOKEN=your_github_token

   Mac/Linux: export GITHUB_TOKEN=your_github_token
5. Run the Application

   mvn spring-boot:run

   The application will start at: http://localhost:8080

   API Endpoints

   1. Get User Organizations: Returns the list of organizations the authenticated GitHub user belongs to.

      GET /api/orgs

      Example: http://localhost:8080/api/orgs

   2. Get Organization Repositories: Returns all repositories belonging to a specific organization.

      GET /api/orgs/{org}/repos

      Example: http://localhost:8080/api/orgs/{org}/repos
   3. Generate Access Report: Returns a report showing which users have access to which repositories in the specified organization.

      GET /api/access-report?org={org}

      Example: http://localhost:8080/api/access-report?org={org}

**Note**
1.{org} represents the GitHub organization name.

2.Ensure that the GitHub token has sufficient permissions to access organization repositories and collaborators.

{org} represents the GitHub organization name.

Ensure that the GitHub token has sufficient permissions to access organization repositories and collaborators.


**Scalability & Design Considerations**

This implementation is designed to support organizations with 100+ repositories and 1000+ users.

Pagination Handling

GitHub API pagination is handled using per_page=100 and page parameters to retrieve repositories efficiently.

Efficient Data Aggregation

Access reports are generated using Java collections such as HashMap and Set to efficiently map users to repositories.

Clean Architecture

The project follows a layered architecture (Controller → Service → Client → DTO) to keep the code maintainable and scalable.

Future Optimization

The current implementation fetches collaborators per repository. For very large organizations, this can be further optimized using asynchronous API calls or parallel processing.
