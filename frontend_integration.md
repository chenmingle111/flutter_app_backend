# Frontend Integration Guide for ChatController

This guide explains how to integrate your frontend application with the backend `ChatController` to enable streaming AI chat functionality.

## Endpoint Overview

- **URL**: `http://localhost:8080/ai/stream`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Response Type**: `text/event-stream` (Server-Sent Events / Streamed Text)

## Request Format

The backend expects a JSON object with a `message` field.

```json
{
  "message": "Your question here"
}
```

## Frontend Implementation (JavaScript/TypeScript)

Since the endpoint is a `POST` request that streams data, you cannot use the standard `EventSource` API (which only supports `GET`). Instead, you should use the `fetch` API with a readable stream reader.

### Example Code

Here is a complete example function to send a message and handle the streaming response.

```javascript
/**
 * Sends a message to the AI chat endpoint and handles the streaming response.
 * 
 * @param {string} message - The user's message.
 * @param {function} onChunk - Callback function to handle each chunk of text received.
 * @param {function} onComplete - Callback function called when the stream ends.
 * @param {function} onError - Callback function for errors.
 */
async function sendChatMessage(message, onChunk, onComplete, onError) {
    try {
        const response = await fetch('http://localhost:8080/ai/stream', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ message: message }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder('utf-8');

        while (true) {
            const { done, value } = await reader.read();
            
            if (done) {
                if (onComplete) onComplete();
                break;
            }

            const chunk = decoder.decode(value, { stream: true });
            if (onChunk) onChunk(chunk);
        }
    } catch (error) {
        console.error('Error fetching chat stream:', error);
        if (onError) onError(error);
    }
}

// --- Usage Example ---

const userMessage = "Hello, tell me a joke.";

console.log("User:", userMessage);

sendChatMessage(
    userMessage,
    (chunk) => {
        // This will be called multiple times as data arrives
        // You would typically append this 'chunk' to your UI
        process.stdout.write(chunk); 
    },
    () => {
        console.log("\n[Stream Completed]");
    },
    (error) => {
        console.error("Chat failed:", error);
    }
);
```

### Integration Tips

1.  **State Management**: In a React/Vue/Angular app, you would append the `chunk` to a state variable (e.g., `currentResponse`) that is bound to your UI.
2.  **Markdown Rendering**: The AI response often contains Markdown. You may want to use a library like `react-markdown` or `marked` to render the accumulated text.
3.  **Error Handling**: Ensure you handle network errors and potential backend timeouts gracefully.

## Testing with cURL

You can also test the endpoint using the command line:

```bash
curl -X POST http://localhost:8080/ai/stream \
     -H "Content-Type: application/json" \
     -d '{"message": "Hello, who are you?"}'
```
