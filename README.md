# Test Bot

A Spring Boot application demonstrating an AI Agent integrated with multiple interfaces (Web and Telegram) and extended capabilities via the Model Context Protocol (MCP).

## 🚀 Overview

Test Bot is a sophisticated AI assistant powered by Spring AI and OpenAI. It leverages the Model Context Protocol (MCP) to decouple tool implementation from the agent logic, allowing the bot to fetch external data (such as employee information) through a dedicated MCP server.

### Key Features
- **AI Agent**: A centralized agent logic using Spring AI with chat memory for context-aware conversations.
- **Multi-Channel Interface**: 
  - **Telegram**: Full integration with Telegram Bots API for mobile interaction.
  - **Web**: A RESTful API endpoint for web-based chat.
- **MCP Integration**: Uses an MCP client-server architecture to provide tools dynamically to the AI.

## 🏗 Architecture

The project is split into two primary Maven modules:

1. **`test-bot` (The Client/Agent)**:
   - Contains the `AIAgent` which orchestrates the LLM calls.
   - Implements `ChatController` for HTTP access.
   - Implements `TelegramBot` for messaging access.
   - Acts as an MCP client to consume tools from the MCP server.

2. **`mcp-server` (The Tool Provider)**:
   - A standalone MCP server that exposes specific tools (e.g., `getEmployee`, `getAllEmployees`).
   - Allows the agent to perform structured data retrieval without hardcoding the logic into the agent itself.

## 🛠 Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.5
- **AI Orchestration**: Spring AI (2.0.0-M4)
- **LLM**: OpenAI
- **Protocol**: Model Context Protocol (MCP)
- **Messaging**: Telegram Bots Spring Boot Starter

## 🚦 Getting Started

### Prerequisites
- JDK 21
- Maven
- OpenAI API Key
- Telegram Bot Token (for Telegram integration)

### Installation & Running

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd test-bot
   ```

2. **Configure Environment**:
   Add your keys to `application.properties` or as environment variables:
   - `spring.ai.openai.api-key=your_openai_key`
   - `telegram.api.key=your_telegram_bot_token`

3. **Run the MCP Server**:
   ```bash
   ./mvnw spring-boot:run -pl mcp-server
   ```

4. **Run the Test Bot**:
   ```bash
   ./mvnw spring-boot:run
   ```

## 📖 Usage

### Web API
You can interact with the bot via a simple GET request:
`GET /chat?query=Hello assistant!`

### Telegram
Send messages to the bot `SfeirAIBot` on Telegram.

## 🧪 Testing
Run the test suite using Maven:
```bash
./mvnw test
```
