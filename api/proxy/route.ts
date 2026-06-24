import { NextResponse } from 'next/server';
import jwt from 'jsonwebtoken';
import { handleGlassError } from '../utils/errorHandler';

// In production, load this from secure environment variables
const JWT_SECRET = process.env.JWT_SECRET || 'omni_agent_os_super_secure_secret_key_2026';
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;

export async function POST(request: Request) {
  try {
    // 1. Extract Authorization header
    const authHeader = request.headers.get('Authorization');
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return handleGlassError(
        'Unauthorized',
        'Missing or malformed Bearer token.',
        401,
        'error'
      );
    }

    const token = authHeader.split(' ')[1];

    // 2. Validate JWT token
    let decodedToken;
    try {
      decodedToken = jwt.verify(token, JWT_SECRET);
    } catch (err) {
      return handleGlassError(
        'Unauthorized',
        'Invalid or expired JWT token.',
        401,
        'error'
      );
    }

    // 3. Parse incoming body (prompt, history, model, enableSearch)
    const body = await request.json();
    const { prompt, history = [], model = 'gemini-3.5-flash', enableSearch = false } = body;

    if (!prompt) {
      return handleGlassError(
        'Bad Request',
        '"prompt" field is required.',
        400,
        'warning'
      );
    }

    // 4. Verify Gemini API Key configuration
    if (!GEMINI_API_KEY) {
      return handleGlassError(
        'Internal Server Error',
        'Gemini API credentials not configured on host.',
        500,
        'error'
      );
    }

    // 5. Structure payload for official Google Generative AI REST API
    const contents = [...history];
    contents.push({
      role: 'user',
      parts: [{ text: prompt }]
    });

    const payload: any = {
      contents,
      generationConfig: {
        temperature: 0.7,
      }
    };

    // Integrate Google Search Grounding tool dynamically if requested
    if (enableSearch) {
      payload.tools = [{ googleSearch: {} }];
    }

    // 6. Forward the request to the official Google Gemini endpoint
    const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${GEMINI_API_KEY}`;
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorData = await response.json();
      return handleGlassError(
        'External API Error',
        'Google Generative AI service request failed.',
        response.status,
        'error',
        errorData
      );
    }

    const data = await response.json();
    
    // 7. Return the verified real data back to the client
    return NextResponse.json(data);

  } catch (error: any) {
    console.error('Proxy Error:', error);
    return handleGlassError(
      'Internal Server Error',
      error.message || 'An unexpected server exception occurred.',
      500,
      'error'
    );
  }
}
