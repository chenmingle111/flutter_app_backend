# Chat 服务前端集成文档

## 一、接口概述

本文档描述了前端如何调用后端 Chat 服务，实现与 AI 模型的对话功能。

**核心特性：**
- ✅ 简单 REST API
- ✅ JSON 请求/响应格式
- ✅ 统一错误处理

---

## 二、API 端点

### 发送消息

| 项目 | 内容 |
|------|------|
| **方法** | `POST` |
| **路径** | `/chat` |
| **完整URL** | `http://localhost:8080/chat` |
| **Content-Type** | `application/json` |
| **响应类型** | `application/json` |

---

## 三、请求格式

### 请求体 (Request Body)

```json
{
  "message": "用户输入的消息内容"
}
```

### 字段说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| `message` | string | ✅ 是 | 用户输入的自然语言消息，不能为空或纯空格 |

### 请求示例

```http
POST /chat HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "message": "你好，请介绍一下 Spring Boot"
}
```

---

## 四、响应格式

### 成功响应

```json
{
  "reply": "Spring Boot 是一个基于 Spring 框架的开源 Java 框架..."
}
```

### 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `reply` | string | AI 模型的回复内容 |

### 错误响应

当请求失败时，返回标准 HTTP 错误状态码。

**常见错误情况：**
- 400 Bad Request: `message` 字段为空
- 500 Internal Server Error: 服务器内部错误或模型调用失败

---

## 五、前端实现示例

### 5.1 原生 JavaScript (Fetch API)

```javascript
async function sendChatMessage(message) {
  try {
    const response = await fetch('http://localhost:8080/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ message })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log('AI Reply:', data.reply);
    return data.reply;
  } catch (error) {
    console.error('Error:', error);
    alert('发送消息失败');
  }
}

// 使用示例
sendChatMessage('你好');
```

### 5.2 React 实现示例

```jsx
import React, { useState } from 'react';

function ChatComponent() {
  const [message, setMessage] = useState('');
  const [reply, setReply] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSendMessage = async () => {
    if (!message.trim()) return;

    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message }),
      });

      if (!res.ok) throw new Error('Network response was not ok');

      const data = await res.json();
      setReply(data.reply);
    } catch (error) {
      console.error('Error:', error);
      alert('发送失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <input
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        disabled={loading}
      />
      <button onClick={handleSendMessage} disabled={loading}>
        {loading ? '发送中...' : '发送'}
      </button>
      {reply && <div>AI: {reply}</div>}
    </div>
  );
}
```

### 5.3 Vue 3 实现示例

```vue
<template>
  <div>
    <input v-model="message" :disabled="loading" @keyup.enter="sendMessage" />
    <button @click="sendMessage" :disabled="loading">发送</button>
    <div v-if="reply">AI: {{ reply }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const message = ref('');
const reply = ref('');
const loading = ref(false);

const sendMessage = async () => {
  if (!message.value.trim()) return;

  loading.value = true;
  try {
    const res = await fetch('http://localhost:8080/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: message.value }),
    });

    if (!res.ok) throw new Error('Request failed');

    const data = await res.json();
    reply.value = data.reply;
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};
</script>
```

### 5.4 Axios 实现示例

```javascript
import axios from 'axios';

async function sendChatMessage(message) {
  try {
    const response = await axios.post('http://localhost:8080/chat', {
      message
    });
    console.log('AI Reply:', response.data.reply);
    return response.data.reply;
  } catch (error) {
    console.error('Error:', error);
  }
}
```
