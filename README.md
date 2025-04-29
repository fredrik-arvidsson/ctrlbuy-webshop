fil: 🛍️ Java Spring Boot Webshop
Det här är ett fullstack-backendprojekt för en webbshop byggd med Java Spring Boot. Projektet utvecklas i lager, med en tydlig och skalbar arkitektur i fokus, och använder moderna tekniker för utveckling, testning, containerisering och deployment.
🚀 Syfte
Syftet är att bygga ett professionellt backend-system för en webbshop – från grunden till produktion – för att visa kunskaper inom:
* Java och Spring Boot
* RESTful API-design
* Säkerhet med roller och lösenord
* Databashantering
* CI/CD-flöden
* Docker & Kubernetes
* Deployment till AWS
  📦 Teknikstack
  OmrådeTeknikProgrammeringJava 21, Spring BootDatabasMySQLAPIRESTSäkerhetSpring Security, BCrypt, JWTByggverktygMavenTestningJUnit 5, PostmanContaineriseringDocker, Docker ComposeDeploymentAWS (ECS/EKS), KubernetesCI/CDGitHub Actions
  🛠️ MySQL: AUTO_INCREMENT och PRIMARY KEY
* När du skapar tabeller med en AUTO_INCREMENT-kolumn i MySQL, säkerställ att kolumnen är en del av en nyckel,
* vanligtvis en PRIMARY KEY. Detta är nödvändigt för att säkerställa unika värden och följa MySQL:s bästa praxis.
  🧱 Lager-baserad utveckling
  Projektet byggs stegvis i lager för att säkerställa enkelhet i början och möjlighet att skala med tiden:
  🔹 Lager 1: Enkel Webbshop (MVP)
* CRUD-funktionalitet för produkter via REST
* MySQL-databasanslutning
* Testning via Postman eller Insomnia
  🔹 Lager 2: Användarhantering och Säkerhet
* Inloggning och registrering
* Hashning av lösenord (BCrypt)
* Roller: ADMIN, CUSTOMER
* Åtkomstskyddade endpoints (t.ex. bara ADMIN får skapa produkter)
  🔹 Lager 3: Arkitektur och Test
* DTOs för att kapsla data
* Services och repositories
* Global felhantering (ExceptionHandlers)
* Enhetstester med JUnit
  🔹 Lager 4: Containerisering och CI/CD
* Dockerfile för Spring Boot
* Docker Compose (med MySQL)
* CI/CD-pipeline i GitHub Actions:
    * Build
    * Test
    * Push till Docker Hub / AWS ECR
      🔹 Lager 5: Deployment i molnet
* Kubernetes-konfiguration (t.ex. Minikube, EKS)
* Deployment till AWS
* Miljövariabler och secrets management
  🔄 Kom igång (utvecklingsläge)
  ✅ Förkrav
* Java 21
* Maven
* Docker (valfritt, rekommenderas)
* MySQL-server eller MySQL Docker-container
* 
* 
* Låt oss sedan strukturera upp projektet i följande paket:

model: För domänmodeller/entiteter
repository: För databasåtkomst
service: För affärslogik
controller: För REST-endpoints
config: För konfigurationer
exception: För felhantering
  🚀 Klona projektet