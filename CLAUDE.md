# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is a Java-based project using Spring Boot 4.0.5 and Spring AI. It consists of two main modules:
- **`test-bot`**: The main application providing a bot interface (Telegram, Web) and an AI Agent powered by OpenAI and Spring AI.
- **`mcp-server`**: A Model Context Protocol (MCP) server implemented using Spring AI's MCP server starter.

## Architecture
- **AI Agent**: Centered around `AIAgent.java`, which uses a `ChatClient` with chat memory and tool callbacks.
- **Integrations**: Includes a Telegram bot (`TelegramBot.java`) and a web controller (`ChatController.java`).
- **MCP Integration**: The `test-bot` acts as an MCP client (`spring-ai-starter-mcp-client`), while `mcp-server` provides the server-side tool implementation.

## Development Commands
The project uses Maven for build management.

### Build and Run
- Build the project: `./mvnw clean install`
- Run the main bot application: `./mvnw spring-boot:run` (from root)
- Run the MCP server: `./mvnw spring-boot:run -pl mcp-server`

### Testing
- Run all tests: `./mvnw test`
- Run a specific test class: `./mvnw test -Dtest=ClassName`

## Tech Stack
- **Java**: 21
- **Framework**: Spring Boot 4.0.5
- **AI**: Spring AI (2.0.0-M4), OpenAI
- **Protocol**: Model Context Protocol (MCP)
- **Messaging**: Telegram Bots Spring Boot Starter