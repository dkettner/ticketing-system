# Ticketing System
An open-source ticketing system to manage projects and tickets alone or in teams. All parts of the system (database, backend with RESTful API and a POC-frontend) are meant to be hosted on your own machine in your local network to be independent of external services.

## Quickstart-Guide
1. Make sure that you have [Docker](https://www.docker.com/) with [Docker Compose](https://docs.docker.com/compose/install/) installed on your hosting machine.
1. Download *docker-compose.yaml* from the [latest release](https://github.com/dkettner/ticketing-system/releases/tag/v0.1.0).
1. __CHANGE THE PASSWORDS__ in the downloaded compose file.
1. Open a terminal and navigate to the file.
1. Start the Ticketing System by running: `docker compose up -d`
1. After a few seconds the Ticketing System will be reachable in your browser under: http://localhost:10000

## Disclaimer
- This software is a prototype and likely has a lot of bugs. Use at your own risk.
- Currently it does not encrypt your connections and cannot be considered secure.
- Only host in your local network. Public hosting is highly discouraged.
